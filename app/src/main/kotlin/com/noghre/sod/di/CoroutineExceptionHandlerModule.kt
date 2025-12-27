package com.noghre.sod.di

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber

/**
 * Global exception handler for Kotlin Coroutines.
 * Handles uncaught exceptions in coroutine scope.
 *
 * Usage:
 * ```
 * viewModelScope.launch(exceptionHandler) {
 *     // Your coroutine code
 * }
 * ```
 *
 * @author Yaser
 * @version 1.0.0
 */
fun createCoroutineExceptionHandler() = CoroutineExceptionHandler { _, throwable ->
    handleCoroutineException(throwable)
}

/**
 * Central exception handling logic.
 * Logs errors, filters sensitive data, and reports crashes.
 */
private fun handleCoroutineException(throwable: Throwable) {
    when (throwable) {
        is NetworkException -> {
            Timber.e("Network Error: ${throwable.message}")
            // Handle network errors silently in production
        }
        is DatabaseException -> {
            Timber.e("Database Error: ${throwable.message}")
            // Notify user about local data issues
        }
        is AuthenticationException -> {
            Timber.e("Auth Error: Invalid credentials")
            // Trigger logout flow
        }
        is ValidationException -> {
            Timber.e("Validation Error: ${throwable.message}")
            // Show validation errors to user
        }
        else -> {
            Timber.e(throwable, "Unexpected Error")
            // Report to crash analytics
        }
    }
}

// Custom Exception Classes
open class NoghreSodException(message: String, cause: Throwable? = null) : 
    Exception(message, cause)

class NetworkException(message: String, cause: Throwable? = null) : 
    NoghreSodException(message, cause)

class DatabaseException(message: String, cause: Throwable? = null) : 
    NoghreSodException(message, cause)

class AuthenticationException(message: String, cause: Throwable? = null) : 
    NoghreSodException(message, cause)

class ValidationException(message: String, cause: Throwable? = null) : 
    NoghreSodException(message, cause)