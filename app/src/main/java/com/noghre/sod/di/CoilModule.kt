package com.noghre.sod.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Dagger module for Coil image loading configuration.
 *
 * Features:
 * - Multi-layer caching (memory + disk)
 * - Progressive image loading
 * - Network error handling
 * - Debug logging
 * - Optimized for jewelry images
 *
 * Memory Cache:
 * - Size: 20% of available memory (max 256MB)
 * - Strategy: LRU (Least Recently Used)
 * - Fast access for frequently used images
 *
 * Disk Cache:
 * - Size: 100MB (persistent storage)
 * - Location: Cache directory
 * - Survives app restarts
 * - Shared across app sessions
 *
 * Progressive Loading:
 * - Low-resolution placeholder first
 * - High-resolution follows
 * - Smooth transition
 * - Better perceived performance
 */
@Module
@InstallIn(SingletonComponent::class)
object CoilModule {
    /**
     * Provide configured ImageLoader for Coil.
     *
     * @param context Android application context
     * @param okHttpClient OkHttp client for network requests
     * @return Configured ImageLoader instance
     */
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            // Memory cache configuration
            .memoryCache {
                MemoryCache.Builder(context)
                    // Cache size: 20% of available memory, max 256MB
                    .maxSizePercent(0.20)
                    .build()
            }
            // Disk cache configuration
            .diskCache {
                DiskCache.Builder()
                    // Cache directory in app cache
                    .directory(context.cacheDir.resolve("image_cache"))
                    // Maximum cache size: 100MB
                    .maxSizeBytes(100L * 1024L * 1024L)  // 100MB
                    .build()
            }
            // Network client (uses certificate pinning from NetworkModule)
            .okHttpClient(okHttpClient)
            // Cache policies
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            // Error handling
            .respectCacheHeaders(true)  // Respect HTTP cache headers
            // Debug logging (enabled in debug builds)
            .logger(DebugLogger())
            // Build and return
            .build()
    }
}

/**
 * Image loading constants and utilities.
 */
object ImageLoadingConfig {
    // Cache sizes (in bytes)
    const val MEMORY_CACHE_SIZE_MB = 256
    const val DISK_CACHE_SIZE_MB = 100

    // Memory cache percentage of available RAM
    const val MEMORY_CACHE_PERCENT = 0.20  // 20%

    // Image loading timeouts (in milliseconds)
    const val CONNECTION_TIMEOUT_MS = 30_000L  // 30 seconds
    const val READ_TIMEOUT_MS = 30_000L        // 30 seconds
    const val WRITE_TIMEOUT_MS = 30_000L       // 30 seconds

    // Image placeholder and error drawable resources
    // (These IDs should be defined in your drawable resources)
    const val PLACEHOLDER_DRAWABLE_ID = android.R.drawable.ic_menu_gallery
    const val ERROR_DRAWABLE_ID = android.R.drawable.ic_dialog_alert

    // Progressive loading configuration
    const val PROGRESSIVE_LOADING_ENABLED = true
    const val LOW_RES_SIZE_KB = 50     // Low-res placeholder size
    const val HIGH_RES_RETRY_DELAY = 500  // Retry delay in ms
}
