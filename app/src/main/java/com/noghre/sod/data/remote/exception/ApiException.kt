package com.noghre.sod.data.remote.exception

/**
 * Custom exception for API-related errors
 */
sealed class ApiException(message: String = "", cause: Throwable? = null) : Exception(message, cause) {
    data class NetworkError(val errorMessage: String = "No internet connection") : ApiException(errorMessage)
    data class HttpError(val code: Int, val message: String = "") : ApiException("HTTP $code: $message")
    data class ValidationError(val fieldErrors: Map<String, String> = emptyMap()) : ApiException("Validation failed")
    data class ServerError(val message: String = "Internal server error") : ApiException(message)
    data class TimeoutError(val message: String = "Request timeout") : ApiException(message)
    data class ParseError(val message: String = "Failed to parse response") : ApiException(message)
    data class UnknownError(val message: String = "Unknown error occurred") : ApiException(message)
    data class Unauthorized(val message: String = "Unauthorized") : ApiException(message)
    data class Forbidden(val message: String = "Access forbidden") : ApiException(message)
    data class NotFound(val message: String = "Resource not found") : ApiException(message)
}