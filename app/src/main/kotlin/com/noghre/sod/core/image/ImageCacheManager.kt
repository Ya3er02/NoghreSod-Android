package com.noghre.sod.core.image

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages image caching with Glide.
 * Handles cache sizes, invalidation, and memory management.
 */
@Singleton
class ImageCacheManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private companion object {
        // Cache sizes in MB
        const val DISK_CACHE_SIZE = 250 // 250MB
        const val MEMORY_CACHE_SIZE_MB = 50 // 50MB
        
        // Cache folder name
        const val CACHE_FOLDER = "glide-cache"
    }

    /**
     * Get current cache size in MB.
     */
    suspend fun getCacheSizeInMB(): Float = withContext(dispatcher) {
        val cacheDir = context.cacheDir
        var size = 0L

        cacheDir.walk().forEach { file ->
            if (file.isFile) {
                size += file.length()
            }
        }

        (size / (1024 * 1024)).toFloat()
    }

    /**
     * Clear all image cache.
     */
    suspend fun clearCache() = withContext(dispatcher) {
        try {
            Glide.get(context).clearDiskCache()
            Glide.get(context).clearMemory()
        } catch (e: Exception) {
            // Log error
            e.printStackTrace()
        }
    }

    /**
     * Clear memory cache only.
     */
    suspend fun clearMemoryCache() = withContext(dispatcher) {
        try {
            Glide.get(context).clearMemory()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Trim memory on low memory condition.
     */
    suspend fun trimMemory(level: Int) = withContext(dispatcher) {
        try {
            when (level) {
                android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
                android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> {
                    Glide.get(context).clearMemory()
                }
                android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
                android.content.ComponentCallbacks2.TRIM_MEMORY_MODERATE -> {
                    Glide.get(context).trimMemory(level)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Preload image for faster display.
     */
    suspend fun preloadImage(imageUrl: String) = withContext(dispatcher) {
        try {
            Glide.with(context)
                .load(imageUrl)
                .submit()
                .get()
        } catch (e: Exception) {
            // Preload failed, will load on demand
            e.printStackTrace()
        }
    }

    /**
     * Preload multiple images.
     */
    suspend fun preloadImages(imageUrls: List<String>) = withContext(dispatcher) {
        imageUrls.forEach { url ->
            preloadImage(url)
        }
    }
}

/**
 * Image cache configuration for Glide module.
 * Should be registered in @GlideModule.
 */
object ImageCacheConfig {
    /**
     * Get disk cache factory for external storage.
     */
    fun getExternalDiskCacheFactory(): ExternalPreferredCacheDiskCacheFactory {
        return ExternalPreferredCacheDiskCacheFactory(
            CACHE_FOLDER,
            (250 * 1024 * 1024).toLong() // 250MB
        )
    }

    /**
     * Get disk cache factory for internal storage.
     */
    fun getInternalDiskCacheFactory(): InternalCacheDiskCacheFactory {
        return InternalCacheDiskCacheFactory(
            (250 * 1024 * 1024).toLong() // 250MB
        )
    }

    /**
     * Memory cache size in MB.
     */
    const val MEMORY_CACHE_SIZE_MB = 50
}

// Add to build.gradle.kts
/*
dependencies {
    implementation("com.github.bumptech.glide:glide:4.15.1")
    kapt("com.github.bumptech.glide:compiler:4.15.1")
}
*/