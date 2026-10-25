package com.example.app23_jc_room_sync_firestore.view

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 1761084903120 -> "21/10/2026 17:15" (hora del celular) */
fun formatearFechaHora(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(millis))

/** 1761084903120 -> "17:15:03" */
fun formatearHora(millis: Long): String =
    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(millis))
