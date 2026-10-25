package com.example.app23_jc_room_sync_firestore.model.remote

import com.example.app23_jc_room_sync_firestore.model.aMapaFirestore
import com.example.app23_jc_room_sync_firestore.model.local.PedidoEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

// Única clase que conoce Cloud Firestore
class PedidoRemoteDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val coleccion = firestore.collection("pedidos")

    /**
     * Envía varios pedidos en un solo lote (WriteBatch): o se guardan todos o ninguno.
     * Cada pedido va al documento pedidos/{uuid}, así reenviarlo lo sobrescribe en vez de duplicarlo.
     *
     * Devuelve true si Firestore confirmó el lote y false si no respondió a tiempo.
     */
    suspend fun enviarLote(pedidos: List<PedidoEntity>): Boolean {
        val lote = firestore.batch()
        pedidos.forEach { pedido ->
            lote.set(coleccion.document(pedido.uuid), pedido.aMapaFirestore())
        }

        val confirmado = withTimeoutOrNull(TIEMPO_MAXIMO_MS) {
            lote.commit().await()
            true
        }
        return confirmado == true
    }

    companion object {
        // Firestore admite hasta 500 operaciones por lote; se deja margen
        const val TAMANO_LOTE = 400

        // Sin señal, Firestore no falla: guarda la escritura en su caché y espera.
        // Por eso se pone un límite para no quedarse esperando indefinidamente.
        private const val TIEMPO_MAXIMO_MS = 15_000L
    }
}
