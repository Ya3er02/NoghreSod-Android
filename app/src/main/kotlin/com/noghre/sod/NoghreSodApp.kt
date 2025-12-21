package com.noghre.sod

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main Application class for NoghreSod Marketplace.
 * Initializes Hilt dependency injection and Timber logging.
 */
@HiltAndroidApp
class NoghreSodApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeLogging()
        initializeApplication()
    }

    /**
     * Initialize Timber logging for development and production.
     */
    private fun initializeLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("NoghreSodApp initialized in DEBUG mode")
        } else {
            // Plant release tree in production
            Timber.plant(CrashReportingTree())
            Timber.d("NoghreSodApp initialized in RELEASE mode")
        }
    }

    /**
     * Initialize application-wide configurations.
     */
    private fun initializeApplication() {
        Timber.d("Initializing NoghreSod Marketplace Application")
        // Add any global configuration here
    }

    /**
     * Custom Timber tree for crash reporting in production.
     */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(
            priority: Int,
            tag: String?,
            message: String,
            t: Throwable?
        ) {
            // Log only errors and warnings in production
            if (priority >= android.util.Log.WARN) {
                // Send to crash reporting service (Crashlytics, etc.)
                if (t != null) {
                    android.util.Log.e(tag, message, t)
                }
            }
        }
    }
}
