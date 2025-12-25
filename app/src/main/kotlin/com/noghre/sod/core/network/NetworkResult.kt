package com.noghre.sod.core.network

/**
 * Sealed class for handling network responses
 * 
 * Provides type-safe wrapper for API results:
 * - Success: Response with data
 * - Error: Detailed error information with type
 * - Loading: In-progress state
 * - Empty: No data found
 * 
 * Usage:
 * ```
 * when (result) {
 *     is NetworkResult.Success -> handleSuccess(result.data)
 *     is NetworkResult.Error -> handleError(result.errorType)
 *     is NetworkResult.Loading -> showLoading()
 *     is NetworkResult.Empty -> showEmptyState()
 * }
 * ```
 * 
 * @since 1.0.0
 */
sealed class NetworkResult<out T> {
    
    /**
     * Successful response with data
     */
    data class Success<T>(
        val data: T
    ) : NetworkResult<T>()
    
    /**
     * Error response with detailed information
     */
    data class Error(
        val exception: Throwable,
        val errorType: ErrorType = ErrorType.UNKNOWN,
        val code: Int? = null,
        val message: String? = null
    ) : NetworkResult<Nothing>()
    
    /**
     * Loading state (in-progress request)
     */
    object Loading : NetworkResult<Nothing>()
    
    /**
     * Empty state (successful response with no data)
     */
    data class Empty(
        val message: String = "داده‌ای یافت نشد"
    ) : NetworkResult<Nothing>()
    
    /**
     * Map success data to another type
     */
    inline fun <R> map(transform: (T) -> R): NetworkResult<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
            is Loading -> Loading
            is Empty -> this
        }
    }
    
    /**
     * Flat map (chain multiple API calls)
     */
    inline fun <R> flatMap(transform: (T) -> NetworkResult<R>): NetworkResult<R> {
        return when (this) {
            is Success -> transform(data)
            is Error -> this
            is Loading -> Loading
            is Empty -> this
        }
    }
    
    /**
     * Execute block if success
     */
    inline fun onSuccess(block: (T) -> Unit): NetworkResult<T> {
        if (this is Success) {
            block(data)
        }
        return this
    }
    
    /**
     * Execute block if error
     */
    inline fun onError(block: (Error) -> Unit): NetworkResult<T> {
        if (this is Error) {
            block(this)
        }
        return this
    }
    
    /**
     * Get data or null
     */
    fun getOrNull(): T? {
        return if (this is Success) data else null
    }
    
    /**
     * Get data or throw
     */
    fun getOrThrow(): T {
        return when (this) {
            is Success -> data
            is Error -> throw exception
            else -> throw IllegalStateException("Expected Success but got ${this::class.simpleName}")
        }
    }
}

/**
 * Comprehensive error types for different failure scenarios
 */
enum class ErrorType {
    /**
     * Network connectivity issue
     */
    NETWORK,
    
    /**
     * Request timeout
     */
    TIMEOUT,
    
    /**
     * 401 Unauthorized - Invalid/expired credentials
     */
    UNAUTHORIZED,
    
    /**
     * 403 Forbidden - Insufficient permissions
     */
    FORBIDDEN,
    
    /**
     * 404 Not Found
     */
    NOT_FOUND,
    
    /**
     * Server error (5xx)
     */
    SERVER_ERROR,
    
    /**
     * 422 Unprocessable Entity - Validation error
     */
    VALIDATION_ERROR,
    
    /**
     * SSL/TLS error (certificate pinning, etc.)
     */
    SSL_ERROR,
    
    /**
     * Unknown or unexpected error
     */
    UNKNOWN
}
