package com.example.app23_jc_room_sync_firestore.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.app23_jc_room_sync_firestore.model.EstadoSync
import com.example.app23_jc_room_sync_firestore.model.Pedido

@Composable
fun PedidoItem(pedido: Pedido) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(pedido.producto, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Cantidad: ${pedido.cantidad} · ${formatearFechaHora(pedido.fechaRegistro)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                pedido.mensajeError?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
            EtiquetaEstado(pedido)
        }
    }
}

@Composable
private fun EtiquetaEstado(pedido: Pedido) {
    val (icono, texto, color) = when (pedido.estado) {
        EstadoSync.PENDIENTE -> Triple(Icons.Default.Schedule, "Pendiente", Color(0xFFB26A00))
        EstadoSync.SINCRONIZADO -> Triple(
            Icons.Default.CloudDone,
            "Enviado ${pedido.sincronizadoEn?.let { formatearHora(it) } ?: ""}",
            Color(0xFF2E7D32)
        )
        EstadoSync.ERROR -> Triple(Icons.Default.ErrorOutline, "Error", Color(0xFFC62828))
    }
    EtiquetaConIcono(icono, texto, color)
}

@Composable
private fun EtiquetaConIcono(icono: ImageVector, texto: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Text(texto, style = MaterialTheme.typography.labelMedium, color = color)
    }
}
