package com.noghre.sod.data.remote.exception

/**
 * Sealed class for API-specific exceptions.
 * Provides type-safe exception handling for different error scenarios.
 */
sealed class ApiException(message: String = "") : Exception(message) {

    /**
     * Network connectivity error.
     */
    data class NetworkException(override val message: String) : ApiException(message)

    /**
     * HTTP error response.
     */
    data class HttpException(
        val code: Int,
        val errorBody: String?,
        override val message: String = "HTTP Error $code"
    ) : ApiException(message)

    /**
     * Unauthorized access (401).
     */
    data class UnauthorizedException(
        override val message: String = "Unauthorized. Please login again."
    ) : ApiException(message)

    /**
     * Server error (5xx).
     */
    data class ServerException(
        override val message: String = "Server error. Please try again later."
    ) : ApiException(message)

    /**
     * Request timeout.
     */
    data class TimeoutException(
        override val message: String = "Request timeout. Please check your connection."
    ) : ApiException(message)

    /**
     * Validation error with field details.
     */
    data class ValidationException(
        val errors: Map<String, String> = emptyMap(),
        override val message: String = "Validation failed"
    ) : ApiException(message)

    /**
     * Parsing error.
     */
    data class ParseException(
        override val message: String = "Failed to parse response"
    ) : ApiException(message)

    /**
     * Unknown error.
     */
    data class UnknownException(
        override val message: String = "An unexpected error occurred"
    ) : ApiException(message)
}
