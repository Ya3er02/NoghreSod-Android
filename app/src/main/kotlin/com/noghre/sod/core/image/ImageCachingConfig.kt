package com.noghre.sod.core.image

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient

// ============================================
// 📸 Coil Image Loader Configuration
// ============================================

@Module
@InstallIn(SingletonComponent::class)
object ImageCachingModule {
    
    /**
     * Configure Coil ImageLoader with memory and disk caching
     */
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            // Memory Cache Configuration
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // 25% of app memory
                    .strongReferencesEnabled(true)
                    .build()
            }
            // Disk Cache Configuration
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB
                    .build()
            }
            // Cache policies
            .respectCacheHeaders(false)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            // Network client
            .okHttpClient(okHttpClient)
            // Additional settings
            .crossfade(true)
            .allowHardware(true) // Use hardware acceleration
            .build()
    }
}

// ============================================
// 📸 Image Request Builder Helper
// ============================================

fun buildImageRequest(
    context: Context,
    imageUrl: String,
    placeholderDrawable: Int? = null,
    errorDrawable: Int? = null,
    cornerRadiusDp: Int = 8,
    contentDescription: String = ""
): ImageRequest {
    return ImageRequest.Builder(context)
        .data(imageUrl)
        .crossfade(true)
        .crossfadeDurationMillis(300)
        // Apply rounded corners transformation
        .transformations(
            RoundedCornersTransformation(
                topLeftPercent = cornerRadiusDp / 100f,
                topRightPercent = cornerRadiusDp / 100f,
                bottomLeftPercent = cornerRadiusDp / 100f,
                bottomRightPercent = cornerRadiusDp / 100f
            )
        )
        // Cache policies
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        // Placeholders
        .apply {
            if (placeholderDrawable != null) {
                placeholder(placeholderDrawable)
            }
            if (errorDrawable != null) {
                error(errorDrawable)
            }
        }
        .build()
}

// ============================================
// 💫 Product Image Loading Optimization
// ============================================

data class ImageLoadingConfig(
    val maxWidth: Int = 300,
    val maxHeight: Int = 300,
    val cornerRadiusDp: Int = 8,
    val enableMemoryCache: Boolean = true,
    val enableDiskCache: Boolean = true,
    val crossfadeDurationMs: Int = 300
)

fun buildProductImageRequest(
    context: Context,
    imageUrl: String,
    config: ImageLoadingConfig = ImageLoadingConfig()
): ImageRequest {
    return ImageRequest.Builder(context)
        .data(imageUrl)
        .size(
            coil.size.Size(
                config.maxWidth,
                config.maxHeight
            )
        )
        .crossfade(config.crossfadeDurationMs)
        .transformations(
            RoundedCornersTransformation(
                topLeftPercent = config.cornerRadiusDp / 100f,
                topRightPercent = config.cornerRadiusDp / 100f,
                bottomLeftPercent = config.cornerRadiusDp / 100f,
                bottomRightPercent = config.cornerRadiusDp / 100f
            )
        )
        .memoryCachePolicy(
            if (config.enableMemoryCache) CachePolicy.ENABLED else CachePolicy.DISABLED
        )
        .diskCachePolicy(
            if (config.enableDiskCache) CachePolicy.ENABLED else CachePolicy.DISABLED
        )
        .build()
}

// ============================================
// 🏢 Cache Clearing Utilities
// ============================================

suspend fun clearImageCache(
    context: Context,
    imageLoader: ImageLoader
) {
    imageLoader.memoryCache?.clear()
    imageLoader.diskCache?.clear()
}

suspend fun getImageCacheSize(context: Context): Long {
    val cacheDir = context.cacheDir.resolve("image_cache")
    return if (cacheDir.exists()) {
        cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
    } else {
        0L
    }
}
