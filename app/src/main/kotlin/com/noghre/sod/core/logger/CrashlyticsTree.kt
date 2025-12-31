package com.noghre.sod.core.logger

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Timber Tree implementation that logs errors and crashes to Firebase Crashlytics.
 *
 * Features:
 * - Automatic error/exception reporting
 * - Crash reporting
 * - Custom message logging
 * - Log level filtering
 *
 * Usage:
 * ```
 * Timber.plant(CrashlyticsTree())
 * ```
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
class CrashlyticsTree : Timber.Tree() {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        try {
            // Only log ERROR and higher priority to Crashlytics
            if (priority < android.util.Log.ERROR) {
                return
            }

            // Log custom message
            val logMessage = "[${priorityToString(priority)}] ${tag ?: ""}: $message"
            crashlytics.log(logMessage)

            // If there's an exception, record it
            if (t != null) {
                crashlytics.recordException(t)
            }
        } catch (e: Exception) {
            // Prevent logging errors from breaking the app
            android.util.Log.e("CrashlyticsTree", "Error logging to Crashlytics", e)
        }
    }

    /**
     * Convert Android log level to string.
     */
    private fun priorityToString(priority: Int): String = when (priority) {
        android.util.Log.VERBOSE -> "V"
        android.util.Log.DEBUG -> "D"
        android.util.Log.INFO -> "I"
        android.util.Log.WARN -> "W"
        android.util.Log.ERROR -> "E"
        android.util.Log.ASSERT -> "A"
        else -> priority.toString()
    }
}
