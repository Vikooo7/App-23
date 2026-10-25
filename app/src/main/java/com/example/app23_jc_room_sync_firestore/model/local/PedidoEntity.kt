package com.example.app23_jc_room_sync_firestore.model.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.app23_jc_room_sync_firestore.model.EstadoSync

// Tabla "pedidos" en la base local del celular
@Entity(
    tableName = "pedidos",
    indices = [Index(value = ["uuid"], unique = true)]
)
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String,
    val producto: String,
    val cantidad: Int,
    val fechaRegistro: Long,
    val estado: EstadoSync = EstadoSync.PENDIENTE,   // Room guarda el enum como texto
    val sincronizadoEn: Long? = null,
    val mensajeError: String? = null
)
