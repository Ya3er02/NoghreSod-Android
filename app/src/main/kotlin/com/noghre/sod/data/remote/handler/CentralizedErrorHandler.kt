package com.noghre.sod.data.remote.handler

import com.noghre.sod.domain.model.AppException
import com.noghre.sod.domain.model.Result
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Centralized error handler for converting various exceptions into AppException types.
 * Provides standardized error messages and logging.
 */
object CentralizedErrorHandler {

    /**
     * Handle any exception and convert to AppException.
     */
    fun handleException(exception: Throwable): AppException {
        return when (exception) {
            is AppException -> exception
            is HttpException -> handleHttpException(exception)
            is SocketTimeoutException -> handleTimeoutException(exception)
            is UnknownHostException -> handleNetworkException(exception)
            is IOException -> handleNetworkException(exception)
            else -> handleUnknownException(exception)
        }
    }

    /**
     * Handle HTTP exceptions (4xx, 5xx).
     */
    private fun handleHttpException(exception: HttpException): AppException {
        Timber.e("HTTP Exception: ${exception.code()} - ${exception.message()}")
        
        return when (exception.code()) {
            // Client Errors (4xx)
            400 -> AppException.ValidationError("Bad request. Please check your input.")
            401 -> {
                Timber.w("Unauthorized - Token may have expired")
                AppException.AuthenticationError("Please login again")
            }
            403 -> AppException.AuthorizationError("You don't have permission to access this resource")
            404 -> AppException.ValidationError("Resource not found")
            409 -> AppException.ValidationError("Conflict - This item already exists")
            429 -> AppException.NetworkError(
                "Too many requests. Please try again later",
                code = exception.code()
            )
            // Server Errors (5xx)
            500 -> AppException.ServerError(
                code = 500,
                message = "Internal server error. Please try again later."
            )
            502 -> AppException.ServerError(
                code = 502,
                message = "Bad gateway. Please try again later."
            )
            503 -> AppException.ServerError(
                code = 503,
                message = "Service unavailable. Please try again later."
            )
            else -> AppException.ServerError(
                code = exception.code(),
                message = "Server error: ${exception.message()}"
            )
        }
    }

    /**
     * Handle timeout exceptions.
     */
    private fun handleTimeoutException(exception: SocketTimeoutException): AppException {
        Timber.e("Timeout Exception: ${exception.message}")
        return AppException.TimeoutError("Request timed out. Please check your internet connection and try again.")
    }

    /**
     * Handle network exceptions (no internet, DNS failures, etc).
     */
    private fun handleNetworkException(exception: IOException): AppException {
        Timber.e("Network Exception: ${exception.message}")
        return when {
            exception is UnknownHostException -> AppException.NetworkError(
                "Cannot connect to server. Please check your internet connection.",
                code = -1
            )
            else -> AppException.NetworkError(
                "Network error. Please check your internet connection.",
                code = -1,
                cause = exception
            )
        }
    }

    /**
     * Handle unknown exceptions.
     */
    private fun handleUnknownException(exception: Throwable): AppException {
        Timber.e(exception, "Unknown Exception")
        return AppException.UnknownError(
            "An unexpected error occurred: ${exception.message}",
            exception
        )
    }

    /**
     * Execute a suspend function with centralized error handling.
     */
    suspend inline fun <T> executeWithErrorHandling(
        crossinline block: suspend () -> T
    ): Result<T> {
        return try {
            val result = block()
            Result.Success(result)
        } catch (e: Throwable) {
            val appException = handleException(e)
            Result.Error(appException)
        }
    }

    /**
     * Execute a function with error handling (non-suspend).
     */
    inline fun <T> executeWithErrorHandlingSync(
        block: () -> T
    ): Result<T> {
        return try {
            val result = block()
            Result.Success(result)
        } catch (e: Throwable) {
            val appException = handleException(e)
            Result.Error(appException)
        }
    }
}

/**
 * Safe extension function for repositories.
 * Usage: val result = safe { apiService.getProducts() }
 */
suspend inline fun <T> safe(
    crossinline block: suspend () -> T
): Result<T> {
    return CentralizedErrorHandler.executeWithErrorHandling { block() }
}

/**
 * Safe extension for sync operations.
 * Usage: val result = safeSync { localDatabase.getProducts() }
 */
inline fun <T> safeSync(
    block: () -> T
): Result<T> {
    return CentralizedErrorHandler.executeWithErrorHandlingSync { block() }
}

/**
 * Extension function for Result to get user-friendly message.
 */
fun Result<*>.getUserMessage(): String {
    return when (this) {
        is Result.Success -> "Operation successful"
        is Result.Error -> this.exception.getUserMessage()
        is Result.Loading -> "Loading..."
    }
}

/**
 * Extension function for Result to get error code.
 */
fun Result<*>.getErrorCode(): Int? {
    return when (this) {
        is Result.Error -> {
            when (this.exception) {
                is AppException.ServerError -> this.exception.code
                is AppException.NetworkError -> this.exception.code
                else -> null
            }
        }
        else -> null
    }
}
