package com.noghre.sod.domain

import timber.log.Timber

/**
 * A generic sealed class that represents the result of an operation.
 * Used for consistent error handling throughout the domain layer.
 *
 * States:
 * - Success: Operation completed with data
 * - Error: Operation failed with exception
 * - Loading: Operation in progress
 *
 * Benefits:
 * - Type-safe result handling
 * - Functional composition with map, onSuccess, etc.
 * - Null safety
 * - Consistent error handling
 *
 * @author Yaser
 * @version 1.0.0
 */
sealed class Result<out T> {
    /**
     * Success result with data.
     *
     * @param data The operation result
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Error result with exception and optional message.
     *
     * @param exception The exception that occurred
     * @param message Optional custom error message
     */
    data class Error(
        val exception: Exception,
        val message: String? = exception.message
    ) : Result<Nothing>()

    /**
     * Loading state for operations in progress.
     * Used to show loading indicators in UI.
     */
    object Loading : Result<Nothing>()

    /**
     * Returns true if this is a Success result.
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Returns true if this is an Error result.
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Returns true if this is a Loading state.
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * Returns the data if Success, null otherwise.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Returns the exception if Error, null otherwise.
     */
    fun exceptionOrNull(): Exception? = when (this) {
        is Error -> exception
        else -> null
    }
}

// ============== EXTENSION FUNCTIONS ==============

/**
 * Map Success data to another type.
 * Returns Error or Loading unchanged.
 *
 * @param transform Function to transform the data
 * @return Result with transformed data or original error/loading
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return try {
        when (this) {
            is Result.Success -> Result.Success(transform(data))
            is Result.Error -> Result.Error(exception, message)
            is Result.Loading -> Result.Loading
        }
    } catch (e: Exception) {
        Timber.e(e, "Error during map transformation")
        Result.Error(e, "Transformation failed")
    }
}

/**
 * Flat map Success data to another Result.
 * Useful for chaining operations that return Result.
 *
 * @param transform Function that returns a Result
 * @return Flattened result
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return try {
        when (this) {
            is Result.Success -> transform(data)
            is Result.Error -> Result.Error(exception, message)
            is Result.Loading -> Result.Loading
        }
    } catch (e: Exception) {
        Timber.e(e, "Error during flatMap transformation")
        Result.Error(e, "FlatMap failed")
    }
}

/**
 * Handle Success result with a callback.
 * Allows chaining multiple handlers.
 *
 * @param action Function to execute if Success
 * @return Same Result for chaining
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        try {
            action(data)
        } catch (e: Exception) {
            Timber.e(e, "Error in onSuccess handler")
        }
    }
    return this
}

/**
 * Handle Error result with a callback.
 * Allows chaining multiple handlers.
 *
 * @param action Function to execute if Error
 * @return Same Result for chaining
 */
inline fun <T> Result<T>.onError(action: (Exception) -> Unit): Result<T> {
    if (this is Result.Error) {
        try {
            action(exception)
        } catch (e: Exception) {
            Timber.e(e, "Error in onError handler")
        }
    }
    return this
}

/**
 * Handle Loading state with a callback.
 * Allows chaining multiple handlers.
 *
 * @param action Function to execute if Loading
 * @return Same Result for chaining
 */
inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) {
        try {
            action()
        } catch (e: Exception) {
            Timber.e(e, "Error in onLoading handler")
        }
    }
    return this
}

/**
 * Get data or a default value.
 * Useful for providing fallbacks.
 *
 * @param default Default value if not Success
 * @return Data if Success, default otherwise
 */
fun <T> Result<T>.getOrDefault(default: T): T {
    return (this as? Result.Success)?.data ?: default
}

/**
 * Get data or throw exception if Error.
 * Useful for recovering from errors.
 *
 * @return Data if Success or Loading
 * @throws Exception If Error
 */
fun <T> Result<T>.getOrThrow(): T {
    return when (this) {
        is Result.Success -> data
        is Result.Error -> throw exception
        is Result.Loading -> throw IllegalStateException("Result is still loading")
    }
}

/**
 * Fold result into a single value.
 * Handles all three cases (Success, Error, Loading).
 *
 * @param onSuccess Function called if Success
 * @param onError Function called if Error
 * @param onLoading Function called if Loading
 * @return Result of the appropriate function
 */
inline fun <T, R> Result<T>.fold(
    onSuccess: (T) -> R,
    onError: (Exception) -> R,
    onLoading: () -> R
): R {
    return try {
        when (this) {
            is Result.Success -> onSuccess(data)
            is Result.Error -> onError(exception)
            is Result.Loading -> onLoading()
        }
    } catch (e: Exception) {
        Timber.e(e, "Error during fold operation")
        onError(e)
    }
}

/**
 * Combine two Results into one.
 * Both must be Success for result to be Success.
 *
 * @param other Another Result
 * @param combiner Function to combine both data
 * @return Combined Result
 */
inline fun <T, U, R> Result<T>.combine(
    other: Result<U>,
    combiner: (T, U) -> R
): Result<R> {
    return when {
        this is Result.Success && other is Result.Success ->
            Result.Success(combiner(data, other.data))
        this is Result.Error -> Result.Error(exception, message)
        other is Result.Error -> Result.Error(other.exception, other.message)
        else -> Result.Loading
    }
}

/**
 * Convert Result to Kotlin Result type.
 * Useful for interoperability.
 *
 * @return Kotlin Result
 */
fun <T> Result<T>.toKotlinResult(): kotlin.Result<T> {
    return when (this) {
        is Result.Success -> kotlin.Result.success(data)
        is Result.Error -> kotlin.Result.failure(exception)
        is Result.Loading -> kotlin.Result.failure(IllegalStateException("Loading"))
    }
}
