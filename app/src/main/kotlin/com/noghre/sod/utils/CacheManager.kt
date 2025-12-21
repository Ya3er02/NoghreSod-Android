package com.noghre.sod.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.File

/**
 * Cache manager for in-memory and disk caching.
 */
class CacheManager(context: Context) {

    private val cacheDir: File = File(context.cacheDir, "app_cache")
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "app_cache_prefs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val inMemoryCache = mutableMapOf<String, Any>()

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    /**
     * Save object to in-memory cache.
     */
    fun <T> putInMemory(key: String, value: T) {
        inMemoryCache[key] = value as Any
        Timber.d("Cached in memory: $key")
    }

    /**
     * Get object from in-memory cache.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getFromMemory(key: String): T? {
        return inMemoryCache[key] as? T
    }

    /**
     * Save object to disk cache as JSON.
     */
    fun <T> putToDisk(key: String, value: T) {
        try {
            val json = gson.toJson(value)
            prefs.edit().putString(key, json).apply()
            Timber.d("Cached to disk: $key")
        } catch (e: Exception) {
            Timber.e(e, "Error caching to disk: $key")
        }
    }

    /**
     * Get object from disk cache.
     */
    inline fun <reified T> getFromDisk(key: String): T? {
        return try {
            val json = prefs.getString(key, null)
            if (json != null) {
                gson.fromJson(json, object : TypeToken<T>() {}.type)
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error retrieving from disk: $key")
            null
        }
    }

    /**
     * Clear in-memory cache.
     */
    fun clearMemory() {
        inMemoryCache.clear()
        Timber.d("Cleared in-memory cache")
    }

    /**
     * Clear disk cache.
     */
    fun clearDisk() {
        prefs.edit().clear().apply()
        cacheDir.deleteRecursively()
        cacheDir.mkdirs()
        Timber.d("Cleared disk cache")
    }

    /**
     * Clear all caches.
     */
    fun clearAll() {
        clearMemory()
        clearDisk()
        Timber.d("Cleared all caches")
    }

    /**
     * Get cache size.
     */
    fun getCacheSize(): Long {
        return cacheDir.walkTopDown().sumOf { it.length() }
    }
}
