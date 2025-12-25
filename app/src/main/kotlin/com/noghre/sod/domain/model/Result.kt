package com.noghre.sod.domain.model

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Sealed class representing the result of an asynchronous operation.
 * 
 * Provides type-safe handling of success, error, and loading states.
 * All exceptions are properly captured with stack traces and context.
 * 
 * @param T The type of data on success
 * 
 * @since 1.0.0
 */
seal class Result<out T> {
    
    /**
     * Represents successful completion with data.
     * 
     * @property data The result data
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * Represents an error state with comprehensive error information.
     * 
     * @property exception The underlying exception (never null)
     * @property message User-friendly error message
     * @property code Optional error code for categorization
     */
    data class Error<T>(
        val exception: Throwable,
        val message: String = exception.localizedMessage ?: "Unknown error occurred",
        val code: Int? = null
    ) : Result<T>() {
        
        /**
         * Get the root cause of the error.
         */
        fun getRootCause(): Throwable {
            var current: Throwable? = exception
            while (current?.cause != null) {
                current = current.cause
            }
            return current ?: exception
        }
        
        /**
         * Get the full stack trace as a string.
         */
        fun getStackTrace(): String {
            return exception.stackTraceToString()
        }
        
        /**
         * Check if this is a network-related error.
         */
        fun isNetworkError(): Boolean {
            return exception is IOException || 
                   exception is SocketTimeoutException ||
                   exception is UnknownHostException
        }
        
        /**
         * Check if this is a timeout error.
         */
        fun isTimeoutError(): Boolean {
            return exception is SocketTimeoutException
        }
    }
    
    /**
     * Represents loading state.
     */
    data class Loading<T>(val progress: Int = 0) : Result<T>()
    
    // ============== Helper Functions ==============
    
    /**
     * Execute a block of code if result is success.
     * 
     * Example:
     * ```
     * result.onSuccess { data ->
     *     println("Received: $data")
     * }
     * ```
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) {
            action(data)
        }
        return this
    }
    
    /**
     * Execute a block of code if result is error.
     * 
     * Example:
     * ```
     * result.onError { error ->
     *     Log.e("Error", error.message, error.exception)
     * }
     * ```
     */
    inline fun onError(action: (Error<T>) -> Unit): Result<T> {
        if (this is Error) {
            action(this)
        }
        return this
    }
    
    /**
     * Execute a block of code if result is loading.
     */
    inline fun onLoading(action: (Loading<T>) -> Unit): Result<T> {
        if (this is Loading) {
            action(this)
        }
        return this
    }
    
    /**
     * Transform success data to another type.
     * 
     * Example:
     * ```
     * val stringResult: Result<String> = result.map { products ->
     *     products.size.toString()
     * }
     * ```
     */
    inline fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(exception, message, code)
            is Loading -> Loading(progress)
        }
    }
    
    /**
     * Transform error or pass through success.
     */
    inline fun <R> recover(transform: (Error<T>) -> R): Result<R> {
        return when (this) {
            is Success -> Success(data as R)
            is Error -> Result.Success(transform(this))
            is Loading -> Loading(progress)
        }
    }
    
    /**
     * Get data or null if error/loading.
     */
    fun getOrNull(): T? {
        return (this as? Success)?.data
    }
    
    /**
     * Get exception or null if not error.
     */
    fun exceptionOrNull(): Throwable? {
        return (this as? Error)?.exception
    }
    
    /**
     * Check if this result is success.
     */
    fun isSuccess(): Boolean = this is Success
    
    /**
     * Check if this result is error.
     */
    fun isError(): Boolean = this is Error
    
    /**
     * Check if this result is loading.
     */
    fun isLoading(): Boolean = this is Loading
    
    /**
     * Get the data or throw the exception.
     * 
     * @throws Throwable The underlying exception if this is an error
     */
    fun getOrThrow(): T {
        return when (this) {
            is Success -> data
            is Error -> throw exception
            is Loading -> throw IllegalStateException("Cannot get data from Loading state")
        }
    }
}

/**
 * Extension function to safely execute a suspend function.
 * Returns a Result wrapping the success or error.
 * 
 * Example:
 * ```
 * val result = safeCall {
 *     repository.getProducts()
 * }
 * ```
 */
suspend inline fun <T> safeCall(
    crossinline block: suspend () -> T
): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e // Don't catch cancellation exceptions
    } catch (e: Exception) {
        Result.Error(
            exception = e,
            message = e.localizedMessage ?: "An error occurred"
        )
    }
}

/**
 * Extension function for synchronous safe execution.
 */
inline fun <T> safeCallSync(
    block: () -> T
): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(
            exception = e,
            message = e.localizedMessage ?: "An error occurred"
        )
    }
}

/**
 * Extension to flatten nested Results.
 * 
 * Example:
 * ```
 * val result: Result<Result<String>> = ...
 * val flattened: Result<String> = result.flatten()
 * ```
 */
fun <T> Result<Result<T>>.flatten(): Result<T> {
    return when (this) {
        is Result.Success -> data
        is Result.Error -> Result.Error(exception, message, code)
        is Result.Loading -> Result.Loading(progress)
    }
}
