package com.noghre.sod.core.ui

/**
 * Sealed class representing UI state for screens.
 * Handles loading, success, error, and empty states.
 */
seal ed class UiState<out T> {
    /**
     * Initial state or idle state.
     */
    object Idle : UiState<Nothing>()

    /**
     * Data is being loaded.
     */
    object Loading : UiState<Nothing>()

    /**
     * Data loaded successfully.
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Error occurred during loading.
     */
    data class Error(
        val error: UiError,
        val canRetry: Boolean = true
    ) : UiState<Nothing>()

    /**
     * No data available.
     */
    object Empty : UiState<Nothing>()
}

/**
 * Represents different UI errors with user-friendly messages.
 */
seal ed class UiError {
    /**
     * Network connectivity issue.
     */
    data class NetworkError(
        val message: String = "No internet connection"
    ) : UiError()

    /**
     * Server returned an error.
     */
    data class ServerError(
        val code: Int,
        val message: String = "Server error occurred"
    ) : UiError()

    /**
     * Request timeout.
     */
    data class TimeoutError(
        val message: String = "Request timed out"
    ) : UiError()

    /**
     * Unknown or unexpected error.
     */
    data class UnknownError(
        val message: String = "Unknown error occurred"
    ) : UiError()

    /**
     * HTTP client error.
     */
    data class HttpError(
        val code: Int,
        val message: String
    ) : UiError()

    /**
     * Validation error.
     */
    data class ValidationError(
        val field: String,
        val message: String
    ) : UiError()

    /**
     * Get human-readable error message.
     */
    val message: String
        get() = when (this) {
            is NetworkError -> this.message
            is ServerError -> this.message
            is TimeoutError -> this.message
            is UnknownError -> this.message
            is HttpError -> this.message
            is ValidationError -> this.message
        }
}

/**
 * Extension function to map exceptions to UiErrors.
 */
fun Throwable.toUiError(): UiError = when (this) {
    is java.net.UnknownHostException -> UiError.NetworkError()
    is java.net.ConnectException -> UiError.NetworkError()
    is java.net.SocketTimeoutException -> UiError.TimeoutError()
    is java.io.IOException -> UiError.NetworkError(this.message ?: "Network error")
    is retrofit2.HttpException -> UiError.HttpError(
        code = this.code(),
        message = this.message()
    )
    else -> UiError.UnknownError(this.message ?: "Unknown error occurred")
}