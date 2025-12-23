package com.noghre.sod.data.remote.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

/**
 * Observes network connectivity changes.
 * Provides reactive Flow for network state changes.
 */
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    /**
     * Flow that emits true when device is connected, false when disconnected.
     */
    val isConnected: Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as? ConnectivityManager

        if (connectivityManager == null) {
            channel.send(false)
            channel.close()
            return@callbackFlow
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Timber.d("Network available")
                try {
                    channel.trySend(true)
                } catch (e: Exception) {
                    Timber.e(e, "Error sending network available")
                }
            }

            override fun onLost(network: Network) {
                Timber.d("Network lost")
                try {
                    channel.trySend(false)
                } catch (e: Exception) {
                    Timber.e(e, "Error sending network lost")
                }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val hasInternet = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
                try {
                    channel.trySend(hasInternet)
                } catch (e: Exception) {
                    Timber.e(e, "Error sending capabilities changed")
                }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        // Emit initial state
        val isCurrentlyConnected = connectivityManager.activeNetwork != null
        try {
            channel.send(isCurrentlyConnected)
        } catch (e: Exception) {
            Timber.e(e, "Error sending initial state")
        }

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    /**
     * Check if device is currently connected (synchronous).
     */
    fun isNetworkAvailable(): Boolean {
        val network = connectivityManager?.activeNetwork ?: return false
        val capabilities = connectivityManager?.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
