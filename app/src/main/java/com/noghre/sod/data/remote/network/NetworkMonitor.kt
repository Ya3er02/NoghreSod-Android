package com.noghre.sod.data.remote.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

/**
 * Network connectivity monitor
 * Detects online/offline status for offline-first strategy
 */
class NetworkMonitor(context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Observe network connectivity status
     */
    val isConnected: Flow<Boolean> = flow {
        val activeNetwork = connectivityManager.activeNetwork
        val isOnline = if (activeNetwork != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            false
        }
        emit(isOnline)
        Timber.d("Network status: ${if (isOnline) "Online" else "Offline"}")
    }
}