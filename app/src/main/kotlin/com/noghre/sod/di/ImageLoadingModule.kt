package com.noghre.sod.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Hilt module for providing image loading dependencies.
 * Configures Coil ImageLoader with optimized caching and network settings.
 */
@Module
@InstallIn(SingletonComponent::class)
object ImageLoadingModule {
    
    /**
     * Provides a configured ImageLoader instance.
     * 
     * Configuration:
     * - Memory cache: 25% of app memory
     * - Disk cache: 512MB
     * - Crossfade animation: 300ms
     * - Uses OkHttpClient for network requests
     */
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader = ImageLoader.Builder(context)
        .okHttpClient(okHttpClient)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25) // Use 25% of app's available memory
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizeBytes(512L * 1024 * 1024) // 512MB disk cache
                .build()
        }
        .respectCacheHeaders(false) // Ignore server cache headers for better performance
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .crossfade(true)
        .crossfade(300) // 300ms crossfade animation
        .build()
}
