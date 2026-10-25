package com.example.app23_jc_room_sync_firestore.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app23_jc_room_sync_firestore.viewmodel.PedidoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPedidos(viewModel: PedidoViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Avisos: conexión perdida o recuperada, pedido guardado, resultado de la sincronización
    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Pedidos") })
                BannerConexion(enLinea = state.enLinea)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                FormularioPedido(
                    producto = state.producto,
                    cantidad = state.cantidad,
                    onProductoChange = viewModel::onProductoChange,
                    onCantidadChange = viewModel::onCantidadChange,
                    onGuardar = viewModel::guardarPedido
                )
            }
            item {
                PanelSincronizacion(
                    pendientes = state.pendientes,
                    enLinea = state.enLinea,
                    sincronizando = state.sincronizando,
                    ultimaSincronizacion = state.ultimaSincronizacion,
                    onSincronizar = viewModel::sincronizar
                )
            }
            item {
                Text(
                    text = "Pedidos guardados en el celular (${state.pedidos.size})",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            if (state.pedidos.isEmpty()) {
                item {
                    Text(
                        text = "Aún no hay pedidos. Registra uno, con o sin Internet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.pedidos, key = { it.id }) { pedido ->
                    PedidoItem(pedido)
                }
            }
        }
    }
}
