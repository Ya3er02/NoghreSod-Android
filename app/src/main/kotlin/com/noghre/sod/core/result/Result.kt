package com.noghre.sod.core.result

import com.noghre.sod.core.result.AppError

/**
 * Sealed class representing the result of an operation.
 * Used throughout the app to handle success, error, and loading states in a type-safe manner.
 *
 * Generic over T - the type of successful result data.
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation with data.
     * @param data The successful result data
     */
    data class Success<T>(
        val data: T
    ) : Result<T>()

    /**
     * Represents a failed operation with error information.
     * @param error The AppError containing error details
     * @param data Optional data that might be available (e.g., cached data on network error)
     */
    data class Error<T>(
        val error: AppError,
        val data: T? = null
    ) : Result<T>()

    /**
     * Represents an operation in progress.
     * @param data Optional data that might be available (e.g., cached data while loading new data)
     */
    data class Loading<T>(
        val data: T? = null
    ) : Result<T>()

    /**
     * Returns the data if this is a Success, otherwise returns null.
     */
    val dataOrNull: T?
        get() = when (this) {
            is Success -> data
            is Error -> data
            is Loading -> data
        }

    /**
     * Returns the error if this is an Error, otherwise returns null.
     */
    val errorOrNull: AppError?
        get() = (this as? Error)?.error

    /**
     * Applies the given function to the data if this is a Success.
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(error, data?.let(transform))
        is Loading -> Loading(data?.let(transform))
    }

    /**
     * Applies the given function to the data if this is a Success or Error.
     */
    inline fun <R> mapData(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(error, data?.let(transform))
        is Loading -> Loading(data?.let(transform))
    }

    /**
     * Applies the given function to the error if this is an Error.
     */
    inline fun <R> mapError(transform: (AppError) -> R): Result<T> = when (this) {
        is Success -> this
        is Error -> Error(transform(error) as AppError, data)
        is Loading -> this
    }

    /**
     * Calls the given function if this is a Success.
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Calls the given function if this is an Error.
     */
    inline fun onError(action: (AppError) -> Unit): Result<T> {
        if (this is Error) action(error)
        return this
    }

    /**
     * Calls the given function if this is Loading.
     */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }

    /**
     * Executes the appropriate action based on the result state.
     */
    inline fun fold(
        onSuccess: (T) -> Unit,
        onError: (AppError) -> Unit,
        onLoading: () -> Unit = {}
    ) {
        when (this) {
            is Success -> onSuccess(data)
            is Error -> onError(error)
            is Loading -> onLoading()
        }
    }

    /**
     * Checks if this is a Success result.
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Checks if this is an Error result.
     */
    fun isError(): Boolean = this is Error

    /**
     * Checks if this is a Loading result.
     */
    fun isLoading(): Boolean = this is Loading
}

/**
 * Extension function to convert a nullable value and error to a Result.
 */
fun <T> Result<T>?.asResult(): Result<T> = this ?: Result.Loading()

/**
 * Extension function to safely transform one Result type to another.
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
    is Result.Success -> transform(data)
    is Result.Error -> Result.Error(error, null)
    is Result.Loading -> Result.Loading(null)
}

/**
 * Extension function to combine multiple results.
 */
suspend inline fun <T1, T2, R> combineResults(
    result1: Result<T1>,
    result2: Result<T2>,
    transform: (T1, T2) -> R
): Result<R> {
    return when {
        result1 is Result.Error -> Result.Error(result1.error)
        result2 is Result.Error -> Result.Error(result2.error)
        result1 is Result.Loading || result2 is Result.Loading -> Result.Loading()
        result1 is Result.Success && result2 is Result.Success -> 
            Result.Success(transform(result1.data, result2.data))
        else -> Result.Error(AppError.UnknownError("Invalid result state"))
    }
}
