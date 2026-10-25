package com.example.app23_jc_room_sync_firestore.viewmodel

import com.example.app23_jc_room_sync_firestore.model.EstadoSync
import com.example.app23_jc_room_sync_firestore.model.Pedido

data class PedidoUiState(
    val pedidos: List<Pedido> = emptyList(),
    val producto: String = "",
    val cantidad: String = "",
    val enLinea: Boolean = true,
    val sincronizando: Boolean = false,
    val ultimaSincronizacion: Long? = null,
    val mensaje: String? = null
) {
    // Se calcula a partir de la lista; no se guarda aparte
    val pendientes: Int
        get() = pedidos.count { it.estado != EstadoSync.SINCRONIZADO }
}
