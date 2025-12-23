package com.noghre.sod

import android.app.Application
import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class NoghreSodApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        Firebase.analytics.logEvent("app_launch") {
            param("timestamp", System.currentTimeMillis())
        }

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        Timber.d("NoghreSodApp initialized - Version ${BuildConfig.VERSION_NAME}")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Handle locale and theme changes
    }

    /**
     * Custom Timber tree for crash reporting in production
     */
    private inner class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority >= android.util.Log.ERROR) {
                Firebase.crashlytics.recordException(t ?: Exception(message))
            }
        }
    }
}
