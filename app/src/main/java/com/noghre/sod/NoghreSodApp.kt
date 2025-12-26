package com.noghre.sod

import android.app.Application
import android.os.Build
import android.os.StrictMode
import android.util.Log
import coil.Coil
import coil.ImageLoader
import coil.request.CachePolicy
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main Application class for Noghre Sod (Silver Jewelry Store) e-commerce app.
 *
 * Responsibilities:
 * - Initialize Timber logging for production and debug modes
 * - Enable StrictMode for development to detect performance issues
 * - Initialize Firebase and third-party libraries
 * - Configure Coil image loading with caching
 * - Set up crash reporting for production
 *
 * @author Yaser
 * @version 1.0.0
 */
@HiltAndroidApp
class NoghreSodApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Enable StrictMode in debug builds to detect performance issues
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
        
        // Initialize Timber logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
        
        Timber.d("NoghreSod App started - Version: ${BuildConfig.APP_VERSION}")
        
        // Initialize third-party libraries
        initializeLibraries()
    }
    
    /**
     * Custom Timber tree for production crash reporting.
     * Logs only errors and warnings to Firebase Crashlytics.
     * Prevents recursion by not calling Timber.e() internally.
     */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            // Don't log verbose/debug/info in production
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                return
            }
            
            // Log to Firebase Crashlytics
            try {
                val crashlytics = FirebaseCrashlytics.getInstance()
                
                // Set custom keys for debugging
                crashlytics.setCustomKey("priority", priority)
                tag?.let { crashlytics.setCustomKey("tag", it) }
                crashlytics.setCustomKey("message", message)
                
                // Record exception
                if (t != null) {
                    crashlytics.recordException(t)
                } else {
                    crashlytics.recordException(Exception(message))
                }
                
                // Also log to system for WARNING and ERROR
                when (priority) {
                    Log.WARN -> Log.w(tag, message, t)
                    Log.ERROR -> Log.e(tag, message, t)
                    Log.ASSERT -> Log.wtf(tag, message, t)
                }
            } catch (e: Exception) {
                // Fallback to system log if Crashlytics fails
                Log.e("CrashReportingTree", "Failed to log to Crashlytics", e)
                Log.e(tag, message, t)
            }
        }
    }
    
    /**
     * Enable StrictMode for debugging performance issues.
     * Only called in debug builds.
     *
     * Detects:
     * - Disk reads/writes on main thread
     * - Network calls on main thread
     * - Long method calls
     * - Leaked Closeable objects
     */
    private fun enableStrictMode() {
        // Thread policy detects main thread issues
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll() // Detect all violations
                .penaltyLog() // Log violations to LogCat
                .penaltyFlashScreen() // Visual indication of violations
                .build()
        )
        
        // VM policy detects application-wide issues
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll() // Detect all violations
                .penaltyLog() // Log violations to LogCat
                .build()
        )
        
        Timber.d("StrictMode enabled for development")
    }
    
    /**
     * Initialize third-party libraries.
     * Called once during app startup.
     */
    private fun initializeLibraries() {
        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
            Timber.d("Firebase initialized successfully")
            
            // Disable Crashlytics in debug for easier testing
            if (BuildConfig.DEBUG) {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            } else {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize Firebase")
        }
        
        // Initialize Coil image loading with caching
        try {
            val imageLoader = ImageLoader.Builder(this)
                .crossfade(true) // Fade-in animation
                .memoryCachePolicy(CachePolicy.ENABLED) // Cache images in memory
                .diskCachePolicy(CachePolicy.ENABLED) // Cache images on disk
                .build()
            Coil.setImageLoader(imageLoader)
            Timber.d("Coil image loader initialized")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize Coil")
        }
        
        // Set user properties for analytics (only in release)
        if (!BuildConfig.DEBUG) {
            try {
                FirebaseAnalytics.getInstance(this).apply {
                    setUserProperty("app_version", BuildConfig.APP_VERSION)
                    setUserProperty("build_type", "release")
                    setUserProperty("api_level", Build.VERSION.SDK_INT.toString())
                }
                Timber.d("Firebase Analytics configured")
            } catch (e: Exception) {
                Timber.e(e, "Failed to configure Firebase Analytics")
            }
        }
    }
}
