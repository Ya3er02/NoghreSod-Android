package com.noghre.sod

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main application class for Noghresod e-commerce app.
 * Initializes all third-party libraries and logging.
 *
 * @author Yaser
 * @version 1.0.0
 */
@HiltAndroidApp
class NoghreSodApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber logging
        initializeLogging()
        
        // Initialize Firebase Crashlytics
        initializeCrashlytics()
        
        // Log app startup
        Timber.i("NoghreSod app started - Version: ${BuildConfig.VERSION_NAME}")
    }

    /**
     * Initialize Timber logging with environment-specific configuration.
     * Uses DebugTree for development and CrashlyticsTree for production.
     */
    private fun initializeLogging() {
        if (BuildConfig.DEBUG) {
            // Development: Plant DebugTree with full logging
            Timber.plant(Timber.DebugTree())
            Timber.d("Debug logging enabled")
        } else {
            // Production: Log to Crashlytics and custom error handler
            Timber.plant(CrashlyticsTree())
            Timber.plant(ReleaseTree())
        }
    }

    /**
     * Initialize Firebase Crashlytics with proper configuration.
     */
    private fun initializeCrashlytics() {
        if (!BuildConfig.DEBUG && BuildConfig.ENABLE_CRASHLYTICS) {
            try {
                val crashlytics = FirebaseCrashlytics.getInstance()
                // Set user identifier if available
                // crashlytics.setUserId(getCurrentUserId())
                // Set custom keys for better debugging
                crashlytics.setCustomKey("app_version", BuildConfig.VERSION_NAME)
                crashlytics.setCustomKey("app_flavor", BuildConfig.FLAVOR)
                crashlytics.setCustomKey("api_environment", BuildConfig.API_BASE_URL)
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize Crashlytics")
            }
        }
    }
}

/**
 * Custom Timber tree for production logging.
 * Logs ERROR and WARN level messages to Crashlytics.
 * 
 * @author Yaser
 * @version 1.0.0
 */
private class CrashlyticsTree : Timber.Tree() {
    
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        // Only log ERROR and WARN level messages
        if (priority < Log.WARN) {
            return
        }
        
        val crashlytics = FirebaseCrashlytics.getInstance()
        
        when {
            t != null -> {
                // Log exception with message
                crashlytics.recordException(
                    Exception("$tag: $message", t)
                )
            }
            priority >= Log.ERROR -> {
                // Log error message
                crashlytics.recordException(
                    Exception("$tag: $message")
                )
            }
            else -> {
                // Log warning as message
                val fullMessage = buildString {
                    if (tag != null) append("[$tag] ")
                    append(message)
                }
                crashlytics.log(fullMessage)
            }
        }
    }
}

/**
 * Custom Timber tree for production release mode.
 * Logs messages for monitoring and debugging in production.
 * 
 * @author Yaser
 * @version 1.0.0
 */
private class ReleaseTree : Timber.Tree() {
    
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        // Only log ERROR level messages
        if (priority != Log.ERROR) {
            return
        }
        
        try {
            val fullMessage = buildString {
                if (tag != null) append("[$tag] ")
                append(message)
                if (t != null) append("\n${t.stackTraceToString()}")
            }
            
            // Log to Crashlytics
            FirebaseCrashlytics.getInstance().log(fullMessage)
            
            // Optionally: Send to remote logging service
            // RemoteLogger.logError(fullMessage, t)
        } catch (e: Exception) {
            Log.e("ReleaseTree", "Error logging message", e)
        }
    }
}
