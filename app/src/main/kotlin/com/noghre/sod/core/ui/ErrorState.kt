package com.noghre.sod.core.ui

/**
 * Sealed class representing different error states.
 *
 * Usage:
 * ```
 * val errorState = mutableStateOf<ErrorState?>(null)
 *
 * when (val error = errorState.value) {
 *     is ErrorState.NetworkError -> { /* Show network error */ }
 *     is ErrorState.ServerError -> { /* Show server error */ }
 *     is ErrorState.ValidationError -> { /* Show form errors */ }
 *     is ErrorState.Unauthorized -> { /* Navigate to login */ }
 *     null -> { /* No error */ }
 * }
 * ```
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
sealed class ErrorState {
    /**
     * Network connectivity error.
     */
    data class NetworkError(
        val message: String = "Network connection failed",
        val retryable: Boolean = true
    ) : ErrorState()

    /**
     * Server-side error (4xx, 5xx).
     */
    data class ServerError(
        val code: Int,
        val message: String,
        val retryable: Boolean = code >= 500
    ) : ErrorState()

    /**
     * Validation error (form validation, etc).
     */
    data class ValidationError(
        val errors: Map<String, String>
    ) : ErrorState()

    /**
     * Unauthorized access (401).
     */
    data class Unauthorized(
        val message: String = "Unauthorized access"
    ) : ErrorState()

    /**
     * Forbidden access (403).
     */
    data class Forbidden(
        val message: String = "Access forbidden"
    ) : ErrorState()

    /**
     * Resource not found (404).
     */
    data class NotFound(
        val message: String = "Resource not found"
    ) : ErrorState()

    /**
     * Generic unknown error.
     */
    data class Unknown(
        val throwable: Throwable,
        val message: String = throwable.message ?: "Unknown error occurred"
    ) : ErrorState()

    /**
     * Timeout error.
     */
    data class Timeout(
        val message: String = "Request timeout"
    ) : ErrorState()
}

/**
 * Extension to get user-friendly error message.
 */
val ErrorState.displayMessage: String
    get() = when (this) {
        is ErrorState.NetworkError -> "No internet connection. Please check your network."
        is ErrorState.ServerError -> when (code) {
            500 -> "Server error. Please try again later."
            502, 503, 504 -> "Service unavailable. Please try again later."
            else -> message
        }
        is ErrorState.ValidationError -> "Please fix the errors and try again."
        is ErrorState.Unauthorized -> "Your session has expired. Please log in again."
        is ErrorState.Forbidden -> "You don't have permission to access this resource."
        is ErrorState.NotFound -> "The requested resource was not found."
        is ErrorState.Unknown -> message
        is ErrorState.Timeout -> "Request took too long. Please try again."
    }

/**
 * Extension to check if error is retryable.
 */
val ErrorState.isRetryable: Boolean
    get() = when (this) {
        is ErrorState.NetworkError -> retryable
        is ErrorState.ServerError -> retryable
        is ErrorState.Timeout -> true
        is ErrorState.ValidationError -> false
        is ErrorState.Unauthorized -> false
        is ErrorState.Forbidden -> false
        is ErrorState.NotFound -> false
        is ErrorState.Unknown -> false
    }
