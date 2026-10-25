package com.example.app23_jc_room_sync_firestore.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PanelSincronizacion(
    pendientes: Int,
    enLinea: Boolean,
    sincronizando: Boolean,
    ultimaSincronizacion: Long?,
    onSincronizar: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Pendientes de envío: $pendientes", style = MaterialTheme.typography.titleMedium)
            Text(
                text = ultimaSincronizacion?.let { "Última sincronización: ${formatearHora(it)}" }
                    ?: "Aún no se sincroniza en esta sesión",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Solo se habilita con conexión y con algo por enviar
            Button(
                onClick = onSincronizar,
                enabled = enLinea && !sincronizando && pendientes > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (sincronizando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = LocalContentColor.current
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sincronizando...")
                } else {
                    Icon(Icons.Default.Sync, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sincronizar con Firestore")
                }
            }

            if (!enLinea && pendientes > 0) {
                Text(
                    text = "El botón se activará cuando vuelva la conexión",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
