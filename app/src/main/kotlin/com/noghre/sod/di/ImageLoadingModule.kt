package com.noghre.sod.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.noghre.sod.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Singleton

/**
 * 📤 Image Loading Module
 * 
 * Configures Coil image loader with:
 * - Memory caching (25% of available memory)
 * - Disk caching (50 MB)
 * - Network integration via OkHttp
 * - Cross-fade animations
 * - Proper error handling
 */
@Module
@InstallIn(SingletonComponent::class)
object ImageLoadingModule {
    
    /**
     * Provides optimized ImageLoader instance
     */
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        Timber.d("[ImageLoading] Creating optimized ImageLoader")
        
        return ImageLoader.Builder(context)
            // Network client for downloading images
            .okHttpClient(okHttpClient)
            
            // Memory cache configuration
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)  // 25% of available memory
                    .strongReferencesEnabled(true)
                    .build()
            }
            
            // Disk cache configuration
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024)  // 50 MB
                    .build()
            }
            
            // Cache policies
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .respectCacheHeaders(false)  // Ignore cache headers for reliability
            
            // Animation
            .crossfade(true)
            .crossfade(300)  // 300ms fade duration
            
            // Precision
            .precision(coil.size.Precision.EXACT)
            
            // Apply debug logger in debug builds
            .apply {
                if (BuildConfig.DEBUG) {
                    Timber.d("[ImageLoading] Debug logger enabled")
                    logger(DebugLogger())
                }
            }
            
            // Build the ImageLoader
            .build()
    }
    
    /**
     * Provides disk cache directory path
     */
    @Provides
    @Singleton
    fun provideCacheDir(@ApplicationContext context: Context): java.io.File {
        return context.cacheDir.resolve("image_cache").apply {
            if (!exists()) {
                mkdirs()
                Timber.d("[ImageLoading] Created cache directory")
            }
        }
    }
    
    /**
     * Get cache size
     */
    fun getCacheSizeInMB(@ApplicationContext context: Context): Int {
        val cacheDir = provideCacheDir(context)
        return try {
            (cacheDir.listFiles()?.sumOf { it.length() } ?: 0L / (1024 * 1024)).toInt()
        } catch (e: Exception) {
            Timber.e(e, "[ImageLoading] Error calculating cache size")
            0
        }
    }
    
    /**
     * Clear image cache
     */
    fun clearImageCache(@ApplicationContext context: Context) {
        try {
            Timber.d("[ImageLoading] Clearing image cache")
            val cacheDir = provideCacheDir(context)
            cacheDir.deleteRecursively()
            cacheDir.mkdirs()
            Timber.d("[ImageLoading] Image cache cleared")
        } catch (e: Exception) {
            Timber.e(e, "[ImageLoading] Error clearing cache")
        }
    }
}
