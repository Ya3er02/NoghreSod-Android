package com.noghre.sod.domain.model.error

import kotlin.math.pow

/**
 * 🔨 Error Recovery Strategy
 * 
 * Handles automatic recovery, retry logic, fallback strategies,
 * and graceful degradation.
 * 
 * @since 1.0.0
 */
sealed class ErrorRecovery {
    
    /**
     * Automatic retry with exponential backoff
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
            "BAD_GATEWAY",
            "GATEWAY_TIMEOUT"
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
     * Manual retry - user must trigger retry
     */
    data class ManualRetry(
        val message: String,
        val retryAction: suspend () -> Unit
    ) : ErrorRecovery()
    
    /**
     * Use cached data as fallback
     */
    data class UseCachedData<T>(
        val cachedData: T,
        val cacheAge: Long,
        val isFresh: Boolean = cacheAge < 300000 // < 5 minutes
    ) : ErrorRecovery()
    
    /**
     * Use default value
     */
    data class UseDefault<T>(
        val defaultValue: T,
        val reason: String
    ) : ErrorRecovery()
    
    /**
     * Queue for later - offline operations
     */
    data class QueueForLater(
        val queueId: String,
        val willRetryAt: Long,
        val retryAttempts: Int = 0
    ) : ErrorRecovery()
    
    /**
     * Graceful degradation - reduce feature set
     */
    data class DegradeGracefully(
        val degradedMode: String,
        val limitations: List<String>,
        val affectedFeatures: List<String> = emptyList()
    ) : ErrorRecovery()
    
    /**
     * No recovery possible
     */
    data class NoRecovery(
        val reason: String,
        val suggestedAction: String? = null
    ) : ErrorRecovery()
}

/**
 * Data source tracking for results
 */
enum class DataSource {
    NETWORK,      // Fresh from API
    CACHE,        // From database/cache
    DATABASE,     // From local database
    MEMORY,       // From memory cache
    FALLBACK,     // From fallback strategy
    QUEUE         // From offline queue
}

/**
 * Result metadata for tracking
 */
data class ResultMetadata(
    val timestamp: Long = System.currentTimeMillis(),
    val source: DataSource = DataSource.NETWORK,
    val retryCount: Int = 0,
    val cacheAge: Long? = null,
    val requestId: String = java.util.UUID.randomUUID().toString(),
    val duration: Long = 0
)

/**
 * Enhanced Result sealed class with recovery
 */
sealed class Result<out T> {
    
    /**
     * Success with data
     */
    data class Success<T>(
        val data: T,
        val metadata: ResultMetadata = ResultMetadata()
    ) : Result<T>()
    
    /**
     * Error without data
     */
    data class Error<T>(
        val error: AppError,
        val data: T? = null,
        val recovery: ErrorRecovery? = null,
        val metadata: ResultMetadata = ResultMetadata()
    ) : Result<T>()
    
    /**
     * Loading state with optional progress
     */
    data class Loading<T>(
        val data: T? = null,
        val progress: Float? = null
    ) : Result<T>()
    
    /**
     * Partial success - some items succeeded, some failed
     */
    data class PartialSuccess<T>(
        val successData: List<T>,
        val errors: List<AppError>,
        val metadata: ResultMetadata = ResultMetadata()
    ) : Result<List<T>>()
}

// ==================== Extension Functions ====================

/**
 * Check if result is success
 */
fun <T> Result<T>.isSuccess(): Boolean {
    return this is Result.Success
}

/**
 * Check if result is error
 */
fun <T> Result<T>.isError(): Boolean {
    return this is Result.Error
}

/**
 * Check if result has data
 */
fun <T> Result<T>.hasData(): Boolean {
    return when (this) {
        is Result.Success -> true
        is Result.Error -> data != null
        is Result.PartialSuccess -> successData.isNotEmpty()
        else -> false
    }
}

/**
 * Get data if available
 */
fun <T> Result<T>.getDataOrNull(): T? {
    return when (this) {
        is Result.Success -> data
        is Result.Error -> data
        else -> null
    }
}

/**
 * Get data or throw exception
 */
fun <T> Result<T>.getDataOrThrow(): T {
    return when (this) {
        is Result.Success -> data
        is Result.Error -> data ?: throw this.error
        else -> throw IllegalStateException("No data available")
    }
}

/**
 * Get error if available
 */
fun <T> Result<T>.getErrorOrNull(): AppError? {
    return when (this) {
        is Result.Error -> error
        else -> null
    }
}

/**
 * Transform success data
 */
fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data), metadata)
        is Result.Error -> Result.Error(error, data?.let { transform(it) }, recovery, metadata)
        is Result.Loading -> Result.Loading(data?.let { transform(it) }, progress)
        is Result.PartialSuccess -> Result.PartialSuccess(
            successData.map { transform(it) },
            errors,
            metadata
        )
    }
}

/**
 * Fold result into value
 */
fun <T, R> Result<T>.fold(
    onSuccess: (T) -> R,
    onError: (AppError, T?) -> R,
    onLoading: () -> R = { throw IllegalStateException("Still loading") },
    onPartial: (List<T>, List<AppError>) -> R = { s, _ -> onSuccess(s as T) }
): R {
    return when (this) {
        is Result.Success -> onSuccess(data)
        is Result.Error -> onError(error, data)
        is Result.Loading -> onLoading()
        is Result.PartialSuccess -> onPartial(successData, errors)
    }
}

/**
 * Recover from error
 */
suspend fun <T> Result<T>.recover(block: suspend (AppError) -> T): Result<T> {
    return if (this is Result.Error) {
        try {
            Result.Success(block(error), metadata)
        } catch (e: Exception) {
            Result.Error(e.toAppError(), data)
        }
    } else {
        this
    }
}
