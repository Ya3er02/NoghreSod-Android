package com.noghre.sod

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application entry point for Noghresod Android app.
 * Initializes Hilt dependency injection and Timber logging.
 *
 * @author Yaser
 * @version 1.0.0
 */
@HiltAndroidApp
class NoghreSodApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        Timber.d("NoghreSod App started - Version: ${BuildConfig.APP_VERSION}")
    }

    /**
     * Custom Timber tree for production crash reporting.
     * Logs only errors to crash reporting service (e.g., Firebase Crashlytics).
     */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority >= android.util.Log.ERROR) {
                // Send to crash reporting service
                // Example: FirebaseCrashlytics.getInstance().recordException(t ?: Throwable(message))
                Timber.e(t, "Production Error: %s", message)
            }
        }
    }
}
