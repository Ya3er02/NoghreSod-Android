package com.noghre.sod.data.remote.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Network state monitoring for detecting connectivity changes.
 * Provides real-time updates about network availability.
 */
@Singleton
class NetworkStateMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val connectivityManager: ConnectivityManager =
        ContextCompat.getSystemService(context, ConnectivityManager::class.java)
            ?: throw IllegalStateException("ConnectivityManager not available")

    /**
     * Observes network connectivity state as a Flow.
     * Emits true when network is available, false otherwise.
     */
    val networkState: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Timber.d("Network available")
                trySend(true)
            }

            override fun onLost(network: Network) {
                Timber.d("Network lost")
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Timber.d("Network capabilities changed")
                trySend(isNetworkAvailable())
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Emit initial state
        trySend(isNetworkAvailable())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    /**
     * Check if network is currently available.
     */
    fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Check if network is available and has internet capability.
     */
    fun isOnline(): Boolean {
        return isNetworkAvailable()
    }

    /**
     * Check if connected via WiFi.
     */
    fun isWifiConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    /**
     * Check if connected via mobile data.
     */
    fun isMobileConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    /**
     * Get network type description.
     */
    fun getNetworkType(): String {
        return when {
            isWifiConnected() -> "WiFi"
            isMobileConnected() -> "Mobile"
            else -> "Unknown"
        }
    }
}
