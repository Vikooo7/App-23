package com.example.app23_jc_room_sync_firestore.model

// Estado de sincronización: separado de los datos del negocio
enum class EstadoSync { PENDIENTE, SINCRONIZADO, ERROR }

// Modelo que usa la app (ViewModel y vistas)
data class Pedido(
    val id: Long,                 // id local en Room
    val uuid: String,             // identificador único global, generado en el celular
    val producto: String,
    val cantidad: Int,
    val fechaRegistro: Long,      // milisegundos
    val estado: EstadoSync,
    val sincronizadoEn: Long?,    // cuándo Firestore confirmó; null hasta sincronizar
    val mensajeError: String?
)
