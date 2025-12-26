package com.noghre.sod.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Standard error response format from backend.
 * All error responses should follow this structure for consistent error handling.
 *
 * @author Yaser
 * @version 1.0.0
 */
data class ErrorResponseDto(
    @SerializedName("success")
    val success: Boolean = false,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("error_code")
    val errorCode: String? = null,
    
    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null,
    
    @SerializedName("timestamp")
    val timestamp: String? = null,
    
    @SerializedName("path")
    val path: String? = null,
    
    @SerializedName("status")
    val status: Int? = null,
    
    @SerializedName("details")
    val details: Map<String, Any>? = null
) {
    /**
     * Get user-friendly error message.
     * Prefers specific field errors over general message.
     */
    fun getUserMessage(): String {
        return when {
            !errors.isNullOrEmpty() -> {
                // Combine all field errors into single message
                errors.values
                    .flatten()
                    .take(3)
                    .joinToString(" | ")
            }
            !message.isNullOrBlank() -> message
            else -> "خطای نامشخص رخ داده است"
        }
    }
    
    /**
     * Check if this is a validation error (4xx status).
     */
    fun isValidationError(): Boolean {
        return status != null && status in 400..499
    }
    
    /**
     * Check if this is a server error (5xx status).
     */
    fun isServerError(): Boolean {
        return status != null && status in 500..599
    }
    
    /**
     * Get specific field error if available.
     */
    fun getFieldError(field: String): String? {
        return errors?.get(field)?.firstOrNull()
    }
}

/**
 * HTTP Status codes for error handling.
 */
object HttpStatusCode {
    // 4xx Client Errors
    const val BAD_REQUEST = 400
    const val UNAUTHORIZED = 401
    const val FORBIDDEN = 403
    const val NOT_FOUND = 404
    const val CONFLICT = 409
    const val UNPROCESSABLE_ENTITY = 422
    const val TOO_MANY_REQUESTS = 429
    
    // 5xx Server Errors
    const val INTERNAL_SERVER_ERROR = 500
    const val SERVICE_UNAVAILABLE = 503
    const val GATEWAY_TIMEOUT = 504
}

/**
 * Application-level error codes.
 */
object AppErrorCode {
    const val NETWORK_ERROR = "NETWORK_ERROR"
    const val TIMEOUT_ERROR = "TIMEOUT_ERROR"
    const val PARSING_ERROR = "PARSING_ERROR"
    const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"
    const val TOKEN_EXPIRED = "TOKEN_EXPIRED"
    const val INSUFFICIENT_BALANCE = "INSUFFICIENT_BALANCE"
    const val PRODUCT_OUT_OF_STOCK = "PRODUCT_OUT_OF_STOCK"
    const val INVALID_COUPON = "INVALID_COUPON"
    const val PAYMENT_FAILED = "PAYMENT_FAILED"
    const val UNKNOWN_ERROR = "UNKNOWN_ERROR"
}
