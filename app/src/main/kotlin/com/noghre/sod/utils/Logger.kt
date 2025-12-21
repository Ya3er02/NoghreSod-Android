package com.noghre.sod.utils

import timber.log.Timber

/**
 * Centralized logging utility.
 */
object Logger {
    fun d(message: String, tag: String = "NoghreSod") {
        Timber.tag(tag).d(message)
    }

    fun e(message: String, throwable: Throwable? = null, tag: String = "NoghreSod") {
        Timber.tag(tag).e(throwable, message)
    }

    fun w(message: String, tag: String = "NoghreSod") {
        Timber.tag(tag).w(message)
    }

    fun i(message: String, tag: String = "NoghreSod") {
        Timber.tag(tag).i(message)
    }
}
