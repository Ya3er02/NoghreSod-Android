package com.noghre.sod

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Application class for NoghreSod (سود نقره).
 * 
 * Initializes:
 * - Hilt dependency injection framework
 * - Timber logging (debug/production trees)
 * - Firebase Crashlytics for crash reporting
 * - Coil image loading with optimizations for jewelry photos
 * - WorkManager for background task scheduling
 */
@HiltAndroidApp
class NoghreSodApplication : Application(), ImageLoaderFactory, Configuration.Provider {
    
    @Inject
    lateinit var workManagerConfiguration: Configuration
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.i("NoghreSod App running in DEBUG mode")
        } else {
            // Production: Log only warnings/errors via Crashlytics
            Timber.plant(CrashlyticsTree())
            Timber.i("NoghreSod App running in RELEASE mode")
        }
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Configure Crashlytics
        FirebaseCrashlytics.getInstance().apply {
            setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
            setCustomKey("app_version", BuildConfig.VERSION_NAME)
            setCustomKey("app_version_code", BuildConfig.VERSION_CODE)
        }
        
        Timber.d("NoghreSod Application initialized successfully")
    }
    
    /**
     * Configure Coil ImageLoader for high-resolution jewelry product photos.
     * 
     * Optimizations:
     * - Large memory cache (25% of available RAM) for rapid scrolling
     * - Large disk cache (250 MB) for persistent caching
     * - Aggressive bitmap pooling for high-resolution images
     * - Crossfade animations for smooth image transitions
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)  // 25% of available RAM
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(250L * 1024 * 1024)  // 250 MB
                    .build()
            }
            .crossfade(true)
            .build()
    }
    
    /**
     * Provide WorkManager configuration for background sync tasks.
     */
    override fun getWorkManagerConfiguration(): Configuration {
        return workManagerConfiguration
    }
}

/**
 * Custom Timber tree for production logging to Crashlytics.
 * Only logs warnings, errors, and assert-level messages.
 */
class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Only log WARNING and ERROR level messages
        if (priority < android.util.Log.WARN) {
            return
        }
        
        FirebaseCrashlytics.getInstance().apply {
            setCustomKey("log_tag", tag ?: "NoghreSod")
            setCustomKey("log_message", message)
            
            if (t != null) {
                recordException(t)
            } else {
                recordException(Exception(message))
            }
        }
    }
}