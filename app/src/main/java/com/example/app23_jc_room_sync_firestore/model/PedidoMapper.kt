package com.example.app23_jc_room_sync_firestore.model

import com.example.app23_jc_room_sync_firestore.model.local.PedidoEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.util.Date

// Room → modelo de la app
fun PedidoEntity.aModelo() = Pedido(
    id = id,
    uuid = uuid,
    producto = producto,
    cantidad = cantidad,
    fechaRegistro = fechaRegistro,
    estado = estado,
    sincronizadoEn = sincronizadoEn,
    mensajeError = mensajeError
)

// Room → documento de Firestore. Solo los datos del negocio: el estado de sincronización es local.
fun PedidoEntity.aMapaFirestore(): Map<String, Any> = mapOf(
    "producto" to producto,
    "cantidad" to cantidad,
    "fechaRegistro" to Timestamp(Date(fechaRegistro)),   // tipo fecha de Firestore, hora del celular
    "recibidoEn" to FieldValue.serverTimestamp()         // Firestore pone su propia hora al recibirlo
)
