package com.noghre.sod.data.model

/**
 * Sealed class representing the result of a network operation.
 * Provides a type-safe way to handle success, error, and loading states.
 *
 * Usage:
 * ```
 * when (result) {
 *     is NetworkResult.Success -> { /* handle data */ }
 *     is NetworkResult.Error -> { /* handle error */ }
 *     is NetworkResult.Loading -> { /* show loading */ }
 * }
 * ```
 *
 * @param T The type of data returned on success
 */
sealed class NetworkResult<T> {
    /**
     * Represents a successful network operation.
     * @param data The returned data from the API
     * @param message Optional success message
     */
    data class Success<T>(
        val data: T,
        val message: String? = null
    ) : NetworkResult<T>()

    /**
     * Represents a failed network operation.
     * @param code HTTP error code
     * @param message Human-readable error message
     * @param exception The underlying exception if available
     */
    data class Error<T>(
        val code: Int = -1,
        val message: String = "Unknown error occurred",
        val exception: Throwable? = null
    ) : NetworkResult<T>()

    /**
     * Represents an ongoing network operation.
     */
    class Loading<T> : NetworkResult<T>()

    /**
     * Check if the result is successful.
     * @return true if this is a Success instance
     */
    fun isSuccessful(): Boolean = this is Success

    /**
     * Check if the result is an error.
     * @return true if this is an Error instance
     */
    fun isError(): Boolean = this is Error

    /**
     * Check if the result is loading.
     * @return true if this is a Loading instance
     */
    fun isLoading(): Boolean = this is Loading

    /**
     * Get the data if available.
     * @return data if this is Success, null otherwise
     */
    fun getDataOrNull(): T? = (this as? Success)?.data

    /**
     * Get the error message if available.
     * @return error message if this is Error, null otherwise
     */
    fun getErrorMessageOrNull(): String? = (this as? Error)?.message

    /**
     * Apply a transformation function to the success data.
     * @param transform Function to transform the data
     * @return A new NetworkResult with transformed data, or this if not successful
     */
    inline fun <R> map(transform: (T) -> R): NetworkResult<R> {
        return when (this) {
            is Success -> Success(transform(data), message)
            is Error -> Error(code, message, exception)
            is Loading -> Loading()
        }
    }

    /**
     * Apply a transformation function if the result is an error.
     * @param transform Function to handle the error
     */
    inline fun onError(transform: (Error<T>) -> Unit) {
        if (this is Error) {
            transform(this)
        }
    }

    /**
     * Apply a transformation function if the result is successful.
     * @param transform Function to handle the success
     */
    inline fun onSuccess(transform: (Success<T>) -> Unit) {
        if (this is Success) {
            transform(this)
        }
    }

    /**
     * Apply a transformation function if the result is loading.
     * @param transform Function to handle the loading state
     */
    inline fun onLoading(transform: (Loading<T>) -> Unit) {
        if (this is Loading) {
            transform(this)
        }
    }
}
