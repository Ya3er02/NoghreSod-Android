package com.noghre.sod.domain.model

import timber.log.Timber

/**
 * Sealed class for handling operation results with type safety.
 * Replaces traditional try-catch with functional pattern.
 */
sealed class Result<out T> {
    /**
     * Successful result containing data.
     */
    data class Success<T>(
        val data: T,
        val cachedAt: Long = System.currentTimeMillis()
    ) : Result<T>() {
        init {
            Timber.d("Result.Success: ${data?.javaClass?.simpleName}")
        }
    }

    /**
     * Failed result containing exception.
     */
    data class Error(
        val exception: AppException
    ) : Result<Nothing>() {
        init {
            Timber.e("Result.Error: ${exception.message}")
        }
    }

    /**
     * Loading state.
     */
    object Loading : Result<Nothing>() {
        init {
            Timber.d("Result.Loading")
        }
    }

    /**
     * Check if result is successful.
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Check if result is error.
     */
    fun isError(): Boolean = this is Error

    /**
     * Check if result is loading.
     */
    fun isLoading(): Boolean = this is Loading

    /**
     * Get data or null.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Get exception or null.
     */
    fun getErrorOrNull(): AppException? = when (this) {
        is Error -> exception
        else -> null
    }

    /**
     * Map success value to another type.
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> {
            try {
                Success(transform(data))
            } catch (e: Exception) {
                Error(AppException.UnknownError(e.message ?: "", e))
            }
        }
        is Error -> this
        Loading -> Loading
    }

    /**
     * Flat map for chaining operations.
     */
    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Success -> {
            try {
                transform(data)
            } catch (e: Exception) {
                Error(AppException.UnknownError(e.message ?: "", e))
            }
        }
        is Error -> this
        Loading -> Loading
    }

    /**
     * Execute action on success.
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) {
            try {
                action(data)
            } catch (e: Exception) {
                Timber.e(e, "Error in onSuccess")
            }
        }
        return this
    }

    /**
     * Execute action on error.
     */
    inline fun onError(action: (AppException) -> Unit): Result<T> {
        if (this is Error) {
            try {
                action(exception)
            } catch (e: Exception) {
                Timber.e(e, "Error in onError")
            }
        }
        return this
    }

    /**
     * Execute action when loading.
     */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) {
            try {
                action()
            } catch (e: Exception) {
                Timber.e(e, "Error in onLoading")
            }
        }
        return this
    }

    /**
     * Get value or default.
     */
    fun getOrDefault(default: T): T = when (this) {
        is Success -> data
        else -> default
    }

    /**
     * Execute action on any result.
     */
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (AppException) -> R,
        onLoading: () -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(exception)
        Loading -> onLoading()
    }
}

/**
 * Extension function to safely execute suspend functions.
 */
suspend inline fun <T> executeResult(
    block: suspend () -> T
): Result<T> = try {
    Result.Success(block())
} catch (e: AppException) {
    Result.Error(e)
} catch (e: Exception) {
    Result.Error(AppException.UnknownError(e.message ?: "", e))
}
