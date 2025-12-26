package com.noghre.sod.data.remote

import android.util.Log
import com.noghre.sod.data.model.NetworkResult
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Extension function to safely execute API calls with proper error handling.
 * Wraps network operations in try-catch and returns a NetworkResult.
 *
 * Usage:
 * ```
 * val result = safeApiCall { userService.getProfile() }
 * ```
 *
 * Handles:
 * - HttpException: HTTP errors (4xx, 5xx)
 * - SocketTimeoutException: Network timeouts
 * - IOException: Network connection errors
 * - Generic exceptions: Unexpected errors
 *
 * @param T The type of data returned by the API call
 * @param apiCall The suspend function that makes the actual API call
 * @return NetworkResult containing success data, error, or loading state
 */
suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> T
): NetworkResult<T> {
    return try {
        val response = apiCall.invoke()
        NetworkResult.Success(data = response)
    } catch (exception: Exception) {
        val errorMessage = handleApiException(exception)
        val errorCode = getErrorCode(exception)
        
        Log.e(
            "API_ERROR",
            "API call failed with code $errorCode: $errorMessage",
            exception
        )
        
        NetworkResult.Error(
            code = errorCode,
            message = errorMessage,
            exception = exception
        )
    }
}

/**
 * Get appropriate HTTP error code from exception.
 * @param exception The exception that occurred
 * @return HTTP error code, or -1 if not applicable
 */
private fun getErrorCode(exception: Exception): Int {
    return when (exception) {
        is HttpException -> exception.code()
        else -> -1
    }
}

/**
 * Convert exception to user-friendly error message.
 * @param exception The exception that occurred
 * @return Human-readable error message
 */
private fun handleApiException(exception: Exception): String {
    return when (exception) {
        // HTTP errors
        is HttpException -> {
            when (exception.code()) {
                400 -> "درخواست نامعتبر است"
                401 -> "احراز هویت ناموفق. لطفاً دوباره وارد شوید"
                403 -> "دسترسی رد شد"
                404 -> "منبع یافت نشد"
                409 -> "تضاد در درخواست"
                429 -> "بیش از حد درخواست. لطفاً بعداً دوباره سعی کنید"
                500 -> "خطای سرور داخلی"
                502 -> "دروازه نامعتبر"
                503 -> "سرویس در دسترس نیست"
                504 -> "سرویس غیرفعال است"
                else -> "خطای HTTP: ${exception.code()}"
            }
        }
        // Network timeouts
        is SocketTimeoutException -> "مهلت زمانی به پایان رسید. لطفاً اتصال اینترنت خود را بررسی کنید"
        // Connection errors
        is IOException -> "خطای شبکه. لطفاً اتصال اینترنت خود را بررسی کنید"
        // Unknown errors
        else -> "خطای نامعلوم: ${exception.localizedMessage ?: exception.message}"
    }
}

/**
 * Extension function to execute API call with exponential backoff retry logic.
 * Retries the API call on failure with increasing delays.
 *
 * Usage:
 * ```
 * val result = safeApiCallWithRetry(maxRetries = 3) { userService.getProfile() }
 * ```
 *
 * @param T The type of data returned by the API call
 * @param maxRetries Maximum number of retry attempts (default: 3)
 * @param initialDelay Initial delay in milliseconds before first retry (default: 1000)
 * @param apiCall The suspend function that makes the actual API call
 * @return NetworkResult containing success data or error
 */
suspend inline fun <T> safeApiCallWithRetry(
    maxRetries: Int = 3,
    initialDelay: Long = 1000,
    crossinline apiCall: suspend () -> T
): NetworkResult<T> {
    var lastException: Exception? = null
    var delay = initialDelay
    
    repeat(maxRetries) { attempt ->
        try {
            val response = apiCall.invoke()
            return NetworkResult.Success(data = response)
        } catch (exception: Exception) {
            lastException = exception
            
            // Don't retry on client errors (4xx)
            if (exception is HttpException && exception.code() in 400..499) {
                val errorMessage = handleApiException(exception)
                return NetworkResult.Error(
                    code = exception.code(),
                    message = errorMessage,
                    exception = exception
                )
            }
            
            // Wait before retrying (except on last attempt)
            if (attempt < maxRetries - 1) {
                Log.w("API_RETRY", "Retry attempt $attempt after ${delay}ms")
                kotlinx.coroutines.delay(delay)
                delay *= 2  // Exponential backoff
            }
        }
    }
    
    val errorMessage = handleApiException(lastException ?: Exception("Unknown error"))
    val errorCode = getErrorCode(lastException ?: Exception("Unknown error"))
    
    return NetworkResult.Error(
        code = errorCode,
        message = errorMessage,
        exception = lastException
    )
}
