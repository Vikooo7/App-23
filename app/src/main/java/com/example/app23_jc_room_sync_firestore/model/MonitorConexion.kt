package com.example.app23_jc_room_sync_firestore.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

// Observa la red del celular y emite true (en línea) o false (sin conexión)
class MonitorConexion(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val hayConexion: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {

            // Cambió la red: se comprueba que tenga Internet real (VALIDATED)
            override fun onCapabilitiesChanged(network: Network, capacidades: NetworkCapabilities) {
                trySend(tieneInternet(capacidades))
            }

            // Se perdió la red (modo avión, Wi-Fi apagado, sin señal)
            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        // Estado inicial, al abrir la app
        trySend(estadoActual())
        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()   // solo emite cuando el estado cambia

    private fun estadoActual(): Boolean {
        val red = connectivityManager.activeNetwork ?: return false
        val capacidades = connectivityManager.getNetworkCapabilities(red) ?: return false
        return tieneInternet(capacidades)
    }

    private fun tieneInternet(capacidades: NetworkCapabilities): Boolean =
        capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
