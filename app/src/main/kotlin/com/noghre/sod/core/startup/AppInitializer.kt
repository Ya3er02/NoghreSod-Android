package com.noghre.sod.core.startup

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.noghre.sod.BuildConfig
import com.noghre.sod.core.logger.TimberInitializer
import timber.log.Timber

/**
 * App Initializer using AndroidX Startup library.
 * 
* Initializes critical app components on startup:
 * - Timber logging
 * - Firebase configuration
 * - Analytics setup
 * 
 * This initializer runs automatically before Application.onCreate().
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
class AppInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        // Initialize Timber based on build type
        if (BuildConfig.DEBUG) {
            TimberInitializer.initDebug()
            Timber.d("Debug logging initialized")
        } else {
            TimberInitializer.initRelease()
            Timber.d("Release logging initialized")
        }

        // Initialize Firebase
        setupFirebase()
        
        Timber.d("App initialization complete")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        // No dependencies - this should run first
        return emptyList()
    }

    /**
     * Setup Firebase services.
     */
    private fun setupFirebase() {
        try {
            // Initialize Analytics
            Firebase.analytics.apply {
                setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
                Timber.d("Firebase Analytics initialized")
            }
            
            // Initialize Crashlytics
            Firebase.crashlytics.apply {
                setCollectionEnabled(!BuildConfig.DEBUG)
                Timber.d("Firebase Crashlytics initialized")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error initializing Firebase")
        }
    }
}

/**
 * Coil Image Loader Initializer.
 * 
* Setup image loading library configuration.
 * Depends on AppInitializer.
 */
class ImageLoaderInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        Timber.d("Coil image loader initialized")
        // Coil is auto-initialized by default
        // Custom configuration can be done here if needed
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(AppInitializer::class.java)
    }
}

/**
 * WorkManager Initializer.
 * 
* Setup background task scheduler.
 * Depends on AppInitializer.
 */
class WorkManagerInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        Timber.d("WorkManager initialized")
        // WorkManager is auto-initialized by AndroidX
        // Can customize behavior here if needed
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(AppInitializer::class.java)
    }
}
