package com.example.app23_jc_room_sync_firestore.model

import com.example.app23_jc_room_sync_firestore.model.local.PedidoDao
import com.example.app23_jc_room_sync_firestore.model.local.PedidoEntity
import com.example.app23_jc_room_sync_firestore.model.remote.PedidoRemoteDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

data class ResultadoSync(val enviados: Int, val fallidos: Int)

// Coordina Room (siempre) y Firestore (solo al sincronizar)
class PedidoRepository(
    private val dao: PedidoDao,
    private val remoto: PedidoRemoteDataSource
) {

    // La pantalla lee siempre de Room, haya o no conexión
    val pedidos: Flow<List<Pedido>> =
        dao.observarTodos().map { lista -> lista.map { it.aModelo() } }

    // Offline-first: el pedido se guarda en el celular, con o sin Internet
    suspend fun registrar(producto: String, cantidad: Int) {
        dao.insertar(
            PedidoEntity(
                uuid = UUID.randomUUID().toString(),
                producto = producto,
                cantidad = cantidad,
                fechaRegistro = System.currentTimeMillis()
            )
        )
    }

    // Envía a Firestore los pedidos PENDIENTE o ERROR, en lotes atómicos
    suspend fun sincronizar(): ResultadoSync {
        var enviados = 0
        var fallidos = 0
        val pendientes = dao.obtenerNoSincronizados()

        for (lote in pendientes.chunked(PedidoRemoteDataSource.TAMANO_LOTE)) {
            val ids = lote.map { it.id }
            try {
                if (remoto.enviarLote(lote)) {
                    dao.marcarSincronizados(ids, System.currentTimeMillis())
                    enviados += lote.size
                } else {
                    // Firestore no confirmó a tiempo: el resto queda pendiente para el próximo intento
                    dao.marcarError(ids, "Firestore no confirmó a tiempo")
                    fallidos += lote.size
                    break
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Firestore rechazó el lote (por ejemplo, las reglas). Al ser atómico, falla completo.
                dao.marcarError(ids, e.message ?: "Firestore rechazó el lote")
                fallidos += lote.size
            }
        }
        return ResultadoSync(enviados, fallidos)
    }
}
