package com.example.app23_jc_room_sync_firestore.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val VerdeEnLinea = Color(0xFF2E7D32)
private val RojoOffline = Color(0xFFC62828)

// Franja superior: verde con nube = en línea; roja con nube tachada = sin conexión
@Composable
fun BannerConexion(enLinea: Boolean) {
    val fondo by animateColorAsState(
        targetValue = if (enLinea) VerdeEnLinea else RojoOffline,
        label = "fondoConexion"
    )

    Surface(color = fondo, contentColor = Color.White, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (enLinea) Icons.Default.CloudDone else Icons.Default.CloudOff,
                contentDescription = null
            )
            Column {
                Text(
                    text = if (enLinea) "En línea" else "Sin conexión · Modo offline",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = if (enLinea) "Los pedidos se pueden enviar a Firestore"
                    else "Los pedidos se guardan en el celular",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
