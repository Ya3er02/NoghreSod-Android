package com.noghre.sod.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Singleton

/**
 * Dependency injection module for Coil image loading.
 * Provides optimized image loader with caching strategies.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    /**
     * Provides configured ImageLoader with memory and disk caching.
     */
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            // Memory cache: 50MB
            .memoryCache(
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            )
            // Disk cache: 200MB
            .diskCache(
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(200L * 1024L * 1024L) // 200MB
                    .build()
            )
            // Use OkHttp client for network requests
            .okHttpClient(okHttpClient)
            // Enable crossfade animation
            .crossfade(true)
            // Log image loading
            .apply {
                Timber.d("ImageLoader configured with memory (50MB) and disk (200MB) cache")
            }
            .build()
    }
}
