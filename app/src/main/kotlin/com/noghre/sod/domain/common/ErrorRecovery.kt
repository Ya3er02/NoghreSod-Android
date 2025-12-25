package com.noghre.sod.domain.common

import com.noghre.sod.presentation.common.UiState
import kotlin.math.pow

/**
 * Error Recovery Strategies for different scenarios
 * 
 * Implements multiple recovery patterns:
 * - Auto retry with exponential backoff
 * - Manual retry for user actions
 * - Fallback to cached data
 * - Queue for offline operations
 * - Graceful degradation
 * 
 * @since 1.0.0
 */
sealed class ErrorRecovery {
    
    /**
     * Automatic retry with exponential backoff
     * Best for network errors
     */
    data class AutoRetry(
        val maxRetries: Int = 3,
        val currentRetry: Int = 0,
        val backoffMs: Long = 1000,
        val backoffMultiplier: Float = 2f,
        val retryableErrors: Set<String> = setOf(
            "NETWORK_ERROR",
            "TIMEOUT_ERROR",
            "SERVICE_UNAVAILABLE",
            "BAD_GATEWAY"
        )
    ) : ErrorRecovery() {
        
        fun shouldRetry(error: AppError): Boolean {
            return currentRetry < maxRetries && error.code in retryableErrors
        }
        
        fun nextDelay(): Long {
            return (backoffMs * backoffMultiplier.pow(currentRetry)).toLong()
        }
        
        fun incrementRetry(): AutoRetry {
            return copy(currentRetry = currentRetry + 1)
        }
    }
    
    /**
     * Manual retry - user must click retry button
     */
    data class ManualRetry(
        val message: String,
        val retryAction: suspend () -> Unit
    ) : ErrorRecovery()
    
    /**
     * Use cached/stale data as fallback
     */
    data class UseCachedData<T>(
        val cachedData: T,
        val cacheAge: Long,
        val maxCacheAge: Long = 3600000 // 1 hour
    ) : ErrorRecovery() {
        
        fun isCacheTooOld(): Boolean = cacheAge > maxCacheAge
    }
    
    /**
     * Use default value
     */
    data class UseDefault<T>(
        val defaultValue: T,
        val reason: String
    ) : ErrorRecovery()
    
    /**
     * Queue operation for later (offline-first)
     */
    data class QueueForLater(
        val queueId: String,
        val willRetryAt: Long,
        val maxRetries: Int = 3
    ) : ErrorRecovery() {
        
        fun shouldRemoveFromQueue(): Boolean {
            return System.currentTimeMillis() > willRetryAt
        }
    }
    
    /**
     * Degrade service gracefully
     */
    data class DegradeGracefully(
        val degradedMode: String,
        val limitations: List<String>,
        val fallbackData: Any? = null
    ) : ErrorRecovery()
    
    /**
     * No recovery possible
     */
    data class NoRecovery(
        val reason: String
    ) : ErrorRecovery()
}

/**
 * Result sealed class with recovery context
 */
sealed class Result<out T> {
    
    data class Success<T>(
        val data: T,
        val metadata: ResultMetadata = ResultMetadata()
    ) : Result<T>()
    
    data class Error<T>(
        val error: AppError,
        val data: T? = null,
        val recovery: ErrorRecovery? = null,
        val metadata: ResultMetadata = ResultMetadata()
    ) : Result<T>()
    
    data class Loading<T>(
        val data: T? = null,
        val progress: Float? = null
    ) : Result<T>()
    
    /**
     * Partial success for batch operations
     */
    data class PartialSuccess<T>(
        val successData: List<T>,
        val errors: List<AppError>,
        val metadata: ResultMetadata = ResultMetadata()
    ) : Result<List<T>>()
}

/**
 * Metadata for tracking and debugging
 */
data class ResultMetadata(
    val timestamp: Long = System.currentTimeMillis(),
    val source: DataSource = DataSource.NETWORK,
    val retryCount: Int = 0,
    val cacheAge: Long? = null,
    val requestId: String = java.util.UUID.randomUUID().toString(),
    val duration: Long = 0 // milliseconds
)

enum class DataSource {
    NETWORK,      // From API
    CACHE,        // From cache
    DATABASE,     // From local database
    MEMORY,       // From memory cache
    FALLBACK      // From fallback strategy
}

/**
 * AppError with multiple levels and recovery info
 */
sealed class AppError : Exception() {
    abstract val userMessage: String
    abstract val code: String
    abstract val severity: ErrorSeverity
    abstract val metadata: ErrorMetadata
    
    data class NetworkError(
        val exception: java.io.IOException,
        override val userMessage: String = "خطا در اتصال به اینترنت",
        override val code: String = "NETWORK_ERROR",
        override val severity: ErrorSeverity = ErrorSeverity.RECOVERABLE,
        override val metadata: ErrorMetadata = ErrorMetadata(),
        val canUseCache: Boolean = true,
        val suggestedRetryDelay: Long = 2000
    ) : AppError()
    
    data class ServerError(
        val statusCode: Int,
        val serverMessage: String?,
        override val code: String,
        override val userMessage: String,
        override val severity: ErrorSeverity = ErrorSeverity.RECOVERABLE,
        override val metadata: ErrorMetadata = ErrorMetadata(),
        val canRetry: Boolean = statusCode in 500..599,
        val suggestedAction: String? = null
    ) : AppError()
    
    data class AuthError(
        val reason: AuthErrorReason,
        override val userMessage: String = "خطا در احراز هویت",
        override val code: String = "AUTH_ERROR",
        override val severity: ErrorSeverity = ErrorSeverity.CRITICAL,
        override val metadata: ErrorMetadata = ErrorMetadata(),
        val requiresLogin: Boolean = true,
        val canRefreshToken: Boolean = false
    ) : AppError()
    
    data class BusinessError(
        val businessCode: String,
        override val userMessage: String,
        override val code: String = "BUSINESS_ERROR",
        override val severity: ErrorSeverity = ErrorSeverity.WARNING,
        override val metadata: ErrorMetadata = ErrorMetadata(),
        val fieldErrors: Map<String, String> = emptyMap()
    ) : AppError()
    
    data class TimeoutError(
        val exception: java.net.SocketTimeoutException,
        override val userMessage: String = "درخواست منقضی شد",
        override val code: String = "TIMEOUT_ERROR",
        override val severity: ErrorSeverity = ErrorSeverity.RECOVERABLE,
        override val metadata: ErrorMetadata = ErrorMetadata(),
        val timeoutMs: Long = 30000
    ) : AppError()
    
    data class ValidationError(
        val field: String,
        override val userMessage: String,
        override val code: String = "VALIDATION_ERROR",
        override val severity: ErrorSeverity = ErrorSeverity.WARNING,
        override val metadata: ErrorMetadata = ErrorMetadata()
    ) : AppError()
    
    data class UnknownError(
        val exception: Exception,
        override val userMessage: String = "خطای نامشخص رخ داد",
        override val code: String = "UNKNOWN_ERROR",
        override val severity: ErrorSeverity = ErrorSeverity.CRITICAL,
        override val metadata: ErrorMetadata = ErrorMetadata()
    ) : AppError()
}

enum class ErrorSeverity {
    INFO,           // Log only
    WARNING,        // Show warning
    RECOVERABLE,    // Can retry
    CRITICAL,       // Needs immediate action
    FATAL           // Crash app
}

data class ErrorMetadata(
    val timestamp: Long = System.currentTimeMillis(),
    val endpoint: String? = null,
    val requestId: String? = null,
    val userId: String? = null,
    val stackTrace: String? = null,
    val additionalData: Map<String, Any> = emptyMap()
)

enum class AuthErrorReason {
    INVALID_CREDENTIALS,
    TOKEN_EXPIRED,
    TOKEN_INVALID,
    SESSION_EXPIRED,
    INSUFFICIENT_PERMISSIONS,
    ACCOUNT_LOCKED,
    ACCOUNT_NOT_VERIFIED
}
