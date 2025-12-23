package com.noghre.sod.core.result

/**
 * Sealed class representing the result of an asynchronous operation.
 * Supports three states: Loading, Success, and Error.
 *
 * @param T The type of data being loaded
 */
sealed class Result<out T> {

    /**
     * Represents a successful operation with data.
     *
     * @param data The resulting data from the operation
     */
    data class Success<T>(
        val data: T
    ) : Result<T>()

    /**
     * Represents an error state.
     *
     * @param error The error that occurred
     * @param data Optional data that was emitted before the error (for offline-first pattern)
     */
    data class Error<T>(
        val error: AppError,
        val data: T? = null
    ) : Result<T>()

    /**
     * Represents a loading state.
     *
     * @param data Optional data that can be emitted while loading (for offline-first pattern)
     */
    data class Loading<T>(
        val data: T? = null
    ) : Result<T>()

    /**
     * Maps the success data to a different type.
     *
     * @param transform Function to transform the data
     * @return A new Result with the transformed data
     */
    inline fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(error, data?.let { transform(it) })
            is Loading -> Loading(data?.let { transform(it) })
        }
    }

    /**
     * Applies a function that returns another Result to the current data.
     *
     * @param transform Function that returns a Result
     * @return The result of the transform
     */
    suspend inline fun <R> flatMap(transform: suspend (T) -> Result<R>): Result<R> {
        return when (this) {
            is Success -> transform(data)
            is Error -> Error(error)
            is Loading -> Loading()
        }
    }

    /**
     * Gets the data from any state.
     *
     * @return The data if available, null otherwise
     */
    fun getOrNull(): T? {
        return when (this) {
            is Success -> data
            is Error -> data
            is Loading -> data
        }
    }

    /**
     * Gets the error from this result.
     *
     * @return The error if this is an Error state, null otherwise
     */
    fun getErrorOrNull(): AppError? {
        return when (this) {
            is Error -> error
            else -> null
        }
    }

    /**
     * Checks if this result is a success.
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Checks if this result is an error.
     */
    fun isError(): Boolean = this is Error

    /**
     * Checks if this result is loading.
     */
    fun isLoading(): Boolean = this is Loading

    /**
     * Executes a block if this result is a success.
     *
     * @param block The block to execute with the success data
     * @return This result for chaining
     */
    inline fun onSuccess(block: (T) -> Unit): Result<T> {
        if (this is Success) {
            block(data)
        }
        return this
    }

    /**
     * Executes a block if this result is an error.
     *
     * @param block The block to execute with the error
     * @return This result for chaining
     */
    inline fun onError(block: (AppError) -> Unit): Result<T> {
        if (this is Error) {
            block(error)
        }
        return this
    }

    /**
     * Executes a block if this result is loading.
     *
     * @param block The block to execute
     * @return This result for chaining
     */
    inline fun onLoading(block: () -> Unit): Result<T> {
        if (this is Loading) {
            block()
        }
        return this
    }

    /**
     * Executes a block regardless of the result state.
     *
     * @param block The block to execute
     * @return This result for chaining
     */
    inline fun <R> fold(
        onLoading: () -> R,
        onError: (error: AppError, data: T?) -> R,
        onSuccess: (data: T) -> R
    ): R {
        return when (this) {
            is Loading -> onLoading()
            is Error -> onError(error, data)
            is Success -> onSuccess(data)
        }
    }

    companion object {
        /**
         * Creates a success result.
         */
        fun <T> success(data: T): Result<T> = Success(data)

        /**
         * Creates an error result.
         */
        fun <T> error(error: AppError, data: T? = null): Result<T> = Error(error, data)

        /**
         * Creates a loading result.
         */
        fun <T> loading(data: T? = null): Result<T> = Loading(data)
    }
}
