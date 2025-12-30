package com.noghre.sod.core.logger

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Initializes Timber logging for the application.
 * 
* Sets up debug and release logging configurations.
 * Integrates with Firebase Crashlytics for crash reporting.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
object TimberInitializer {

    /**
     * Initialize Timber with debug configuration.
     * 
     * Logs to Logcat with tag and thread information.
     */
    fun initDebug() {
        Timber.plant(DebugTree())
    }

    /**
     * Initialize Timber with release configuration.
     * 
     * Logs only errors and crashes to Firebase Crashlytics.
     * Silent logging in normal operation.
     */
    fun initRelease() {
        Timber.plant(ReleaseTree())
    }

    /**
     * Debug tree for development builds.
     * 
     * Outputs formatted logs to Logcat with:
     * - Tag name
     * - Thread information
     * - Method name and line number
     */
    private class DebugTree : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String {
            return with(element) {
                "${fileName.substringBefore('.')}:$lineNumber:$methodName()"
            }
        }

        override fun log(
            priority: Int,
            tag: String?,
            message: String,
            t: Throwable?
        ) {
            // Add thread name to log
            val threadName = Thread.currentThread().name
            val finalMessage = "[$threadName] $message"
            
            super.log(priority, tag, finalMessage, t)
            
            // Also log to Crashlytics for non-debug builds if exception
            if (t != null && priority >= Log.ERROR) {
                FirebaseCrashlytics.getInstance().recordException(t)
            }
        }
    }

    /**
     * Release tree for production builds.
     * 
     * Silent in normal operation.
     * Logs errors and crashes to Firebase Crashlytics.
     */
    private class ReleaseTree : Timber.Tree() {
        override fun log(
            priority: Int,
            tag: String?,
            message: String,
            t: Throwable?
        ) {
            if (priority == Log.ERROR || priority == Log.WARN) {
                // Log errors and warnings to Crashlytics
                if (t != null) {
                    FirebaseCrashlytics.getInstance().recordException(t)
                } else {
                    // Log error message as exception
                    FirebaseCrashlytics.getInstance().log(
                        "[$priority] $tag: $message"
                    )
                }
            }
        }
    }
}
