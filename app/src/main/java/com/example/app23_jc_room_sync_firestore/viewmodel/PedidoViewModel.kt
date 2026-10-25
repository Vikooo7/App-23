package com.example.app23_jc_room_sync_firestore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app23_jc_room_sync_firestore.model.MonitorConexion
import com.example.app23_jc_room_sync_firestore.model.PedidoRepository
import com.example.app23_jc_room_sync_firestore.model.local.AppDatabase
import com.example.app23_jc_room_sync_firestore.model.remote.PedidoRemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// AndroidViewModel: Room y el monitor de red necesitan el Context de la aplicación
class PedidoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PedidoRepository(
        dao = AppDatabase.obtener(application).pedidoDao(),
        remoto = PedidoRemoteDataSource()
    )
    private val monitor = MonitorConexion(application)

    private val _uiState = MutableStateFlow(PedidoUiState())
    val uiState: StateFlow<PedidoUiState> = _uiState.asStateFlow()

    init {
        // 1) Lista de pedidos desde Room
        viewModelScope.launch {
            repository.pedidos.collect { lista ->
                _uiState.update { it.copy(pedidos = lista) }
            }
        }

        // 2) Estado de la conexión y aviso al usuario cuando cambia
        viewModelScope.launch {
            var primeraLectura = true
            monitor.hayConexion.collect { enLinea ->
                val aviso = when {
                    !enLinea -> "Sin conexión a Internet. Los pedidos se guardarán en el celular."
                    !primeraLectura -> "Conexión recuperada. Ya puedes sincronizar."
                    else -> null
                }
                primeraLectura = false
                _uiState.update { it.copy(enLinea = enLinea, mensaje = aviso ?: it.mensaje) }
            }
        }
    }

    fun onProductoChange(valor: String) = _uiState.update { it.copy(producto = valor) }

    fun onCantidadChange(valor: String) {
        if (valor.length <= 4 && valor.all { it.isDigit() }) {
            _uiState.update { it.copy(cantidad = valor) }
        }
    }

    fun guardarPedido() {
        val estado = _uiState.value
        val producto = estado.producto.trim()
        val cantidad = estado.cantidad.toIntOrNull()

        when {
            producto.isEmpty() -> return mostrarMensaje("Ingresa el nombre del producto")
            cantidad == null || cantidad <= 0 -> return mostrarMensaje("La cantidad debe ser mayor a 0")
        }

        viewModelScope.launch {
            repository.registrar(producto, cantidad!!)
            _uiState.update {
                it.copy(
                    producto = "",
                    cantidad = "",
                    mensaje = if (it.enLinea) "Pedido guardado. Pulsa Sincronizar para enviarlo a Firestore."
                    else "Pedido guardado sin conexión. Se enviará cuando sincronices."
                )
            }
        }
    }

    fun sincronizar() {
        val estado = _uiState.value
        when {
            estado.sincronizando -> return
            !estado.enLinea -> return mostrarMensaje("Sin conexión: los pedidos siguen guardados en el celular")
            estado.pendientes == 0 -> return mostrarMensaje("No hay pedidos pendientes")
        }

        viewModelScope.launch {
            _uiState.update { it.copy(sincronizando = true) }
            val resultado = repository.sincronizar()
            _uiState.update {
                it.copy(
                    sincronizando = false,
                    ultimaSincronizacion =
                        if (resultado.enviados > 0) System.currentTimeMillis() else it.ultimaSincronizacion,
                    mensaje = when {
                        resultado.fallidos == 0 ->
                            "Sincronización completa: ${resultado.enviados} pedido(s) enviados a Firestore"
                        resultado.enviados == 0 ->
                            "No se pudo sincronizar. Firestore no respondió; inténtalo de nuevo."
                        else ->
                            "Se enviaron ${resultado.enviados}; ${resultado.fallidos} con error. Vuelve a sincronizar."
                    }
                )
            }
        }
    }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    private fun mostrarMensaje(texto: String) = _uiState.update { it.copy(mensaje = texto) }
}
