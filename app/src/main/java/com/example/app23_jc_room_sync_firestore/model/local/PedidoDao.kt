package com.example.app23_jc_room_sync_firestore.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    // Flow: la lista en pantalla se actualiza sola cuando cambia la tabla
    @Query("SELECT * FROM pedidos ORDER BY fechaRegistro DESC")
    fun observarTodos(): Flow<List<PedidoEntity>>

    // PENDIENTE y ERROR se vuelven a intentar en cada sincronización
    @Query("SELECT * FROM pedidos WHERE estado != 'SINCRONIZADO' ORDER BY fechaRegistro")
    suspend fun obtenerNoSincronizados(): List<PedidoEntity>

    @Insert
    suspend fun insertar(pedido: PedidoEntity): Long

    // Se actualiza un lote completo de una sola vez
    @Query("UPDATE pedidos SET estado = 'SINCRONIZADO', sincronizadoEn = :fecha, mensajeError = NULL WHERE id IN (:ids)")
    suspend fun marcarSincronizados(ids: List<Long>, fecha: Long)

    @Query("UPDATE pedidos SET estado = 'ERROR', mensajeError = :mensaje WHERE id IN (:ids)")
    suspend fun marcarError(ids: List<Long>, mensaje: String)
}
