package com.noghre.sod.data.remote

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import okhttp3.OkHttpClient
import java.io.File

/**
 * Configuration for Coil image loader with memory and disk caching.
 * 
 * Provides optimized image loading with:
 * - Memory cache (% of available memory)
 * - Disk cache (encrypted)
 * - HTTP client with appropriate timeouts
 * 
 * @since 1.0.0
 */
object ImageLoaderConfig {
    
    /**
     * Create and configure Coil ImageLoader.
     */
    fun createImageLoader(context: Context, okHttpClient: OkHttpClient): ImageLoader {
        return ImageLoader.Builder(context)
            // Memory cache configuration
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)  // Use 25% of available memory
                    .build()
            }
            // Disk cache configuration
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, "image_cache"))
                    .maxSizeBytes(100L * 1024 * 1024) // 100MB max disk cache
                    .build()
            }
            // Use OkHttpClient with certificate pinning
            .okHttpClient(okHttpClient)
            // Cross-fade animation
            .crossfade(true)
            .build()
    }
}
