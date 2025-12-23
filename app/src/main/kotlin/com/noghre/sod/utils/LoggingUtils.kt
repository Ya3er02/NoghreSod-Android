package com.noghre.sod.utils

import android.util.Log
import com.noghre.sod.BuildConfig

object LoggingUtils {

    private const val TAG = "NoghreSod"

    fun log(message: String, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    fun logError(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, throwable)
        }
    }

    fun logWarning(message: String, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message)
        }
    }

    fun logInfo(message: String, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }

    fun logVerbose(message: String, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message)
        }
    }
}
