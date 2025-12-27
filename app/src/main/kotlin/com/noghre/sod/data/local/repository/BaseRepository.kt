package com.noghre.sod.data.local.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

/**
 * Advanced repository pattern for managing data from multiple sources
 * Implements caching with TTL and network-aware data fetching
 */
abstract class BaseRepository<T : Any>(
    private val cacheDuration: Long = TimeUnit.HOURS.toMillis(1),
    private val enableLogging: Boolean = true
) {
    
    private val cacheMap = mutableMapOf<String, CachedData<T>>()
    private val tag = this::class.simpleName ?: "Repository"
    
    // ============================================
    // Cache Management
    // ============================================
    
    protected fun isCacheValid(key: String): Boolean {
        val cached = cacheMap[key] ?: return false
        val isValid = System.currentTimeMillis() - cached.timestamp < cacheDuration
        if (!isValid) {
            cacheMap.remove(key)
            log("Cache expired for key: $key")
        }
        return isValid
    }
    
    protected fun getFromCache(key: String): T? {
        return if (isCacheValid(key)) {
            cacheMap[key]?.data?.also {
                log("Retrieved from cache: $key")
            }
        } else {
            null
        }
    }
    
    protected fun cacheData(key: String, data: T) {
        cacheMap[key] = CachedData(data, System.currentTimeMillis())
        log("Data cached: $key")
    }
    
    protected fun clearCache(key: String? = null) {
        if (key != null) {
            cacheMap.remove(key)
            log("Cache cleared for key: $key")
        } else {
            cacheMap.clear()
            log("All cache cleared")
        }
    }
    
    // ============================================
    // Network-Aware Data Fetching
    // ============================================
    
    protected fun <R : Any> fetchWithCache(
        key: String,
        remoteCall: suspend () -> R,
        mapper: (R) -> T = { it as T }
    ): Flow<RepositoryResult<T>> = flow {
        // Try to get from cache first
        getFromCache(key)?.let { cachedData ->
            emit(RepositoryResult.Success(cachedData, isCached = true))
            return@flow
        }
        
        emit(RepositoryResult.Loading())
        
        try {
            val result = remoteCall()
            val mappedData = mapper(result)
            cacheData(key, mappedData)
            emit(RepositoryResult.Success(mappedData, isCached = false))
        } catch (e: Exception) {
            logError("Error fetching $key", e)
            emit(RepositoryResult.Error(e.message ?: "Unknown error", e))
        }
    }.catch { exception ->
        logError("Flow error for $key", exception as Exception)
        emit(RepositoryResult.Error(exception.message ?: "Unknown error", exception))
    }
    
    // ============================================
    // Local Database Operations
    // ============================================
    
    protected suspend fun saveLocally(data: T, key: String) {
        try {
            onSaveLocal(data, key)
            log("Data saved locally: $key")
        } catch (e: Exception) {
            logError("Error saving locally: $key", e)
        }
    }
    
    protected fun getLocalFlow(key: String): Flow<T?> = flow {
        try {
            emit(onGetLocalFlow(key))
        } catch (e: Exception) {
            logError("Error getting local flow: $key", e)
            emit(null)
        }
    }
    
    // ============================================
    // Abstract Methods (Subclass Implementation)
    // ============================================
    
    /**
     * Implement to save data to local database
     */
    protected open suspend fun onSaveLocal(data: T, key: String) {}
    
    /**
     * Implement to get data from local database as Flow
     */
    protected open suspend fun onGetLocalFlow(key: String): T? = null
    
    /**
     * Called when an error occurs
     */
    protected open fun onError(error: String, exception: Throwable) {}
    
    // ============================================
    // Logging
    // ============================================
    
    private fun log(message: String) {
        if (enableLogging) {
            Log.d(tag, message)
        }
    }
    
    private fun logError(message: String, exception: Throwable) {
        if (enableLogging) {
            Log.e(tag, message, exception)
        }
        onError(message, exception)
    }
    
    // ============================================
    // Inner Classes
    // ============================================
    
    private data class CachedData<T>(
        val data: T,
        val timestamp: Long
    )
}

/**
 * Result wrapper for repository operations
 */
sealed class RepositoryResult<out T : Any> {
    data class Success<T : Any>(
        val data: T,
        val isCached: Boolean = false
    ) : RepositoryResult<T>()
    
    data class Error<T : Any>(
        val message: String,
        val exception: Throwable? = null
    ) : RepositoryResult<T>()
    
    data class Loading<T : Any>(
        val isLoading: Boolean = true
    ) : RepositoryResult<T>()
}

/**
 * Extension functions for RepositoryResult
 */
inline fun <T : Any> RepositoryResult<T>.handle(
    onSuccess: (T, Boolean) -> Unit,
    onError: (String) -> Unit = {},
    onLoading: (() -> Unit)? = null
) {
    when (this) {
        is RepositoryResult.Success -> onSuccess(this.data, this.isCached)
        is RepositoryResult.Error -> onError(this.message)
        is RepositoryResult.Loading -> onLoading?.invoke()
    }
}

fun <T : Any> RepositoryResult<T>.getDataOrNull(): T? {
    return when (this) {
        is RepositoryResult.Success -> this.data
        else -> null
    }
}

fun <T : Any> RepositoryResult<T>.getErrorOrNull(): String? {
    return when (this) {
        is RepositoryResult.Error -> this.message
        else -> null
    }
}

fun <T : Any> RepositoryResult<T>.isLoading(): Boolean {
    return this is RepositoryResult.Loading
}
