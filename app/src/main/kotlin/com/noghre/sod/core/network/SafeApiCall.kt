package com.noghre.sod.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

/**
 * Safe API call wrapper that handles all types of network errors.
 * Converts exceptions to NetworkResult types for better error handling.
 *
 * Usage:
 * ```
 * val result = safeApiCall { apiService.getProducts() }
 * when (result) {
 *     is NetworkResult.Success -> updateUi(result.data)
 *     is NetworkResult.Error -> showError(result.message)
 *     ...
 * }
 * ```
 */
suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> Response<T>
): NetworkResult<T> = withContext(Dispatchers.IO) {
    try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkResult.Success(body)
            } else {
                Timber.w("SafeApiCall: Empty response body")
                // Treated as Empty result instead of Error
                NetworkResult.Empty("پاسخ سرور خالی بود.")
            }
        } else {
            val code = response.code()
            val message = response.message()
            Timber.e("SafeApiCall: HTTP Error: $code - $message")
            
            // Re-throw as HttpException to be handled by NetworkErrorHandler
            throw retrofit2.HttpException(response)
        }
    } catch (e: Exception) {
        // Delegate all exception handling to NetworkErrorHandler
        NetworkErrorHandler.handleException(e)
    }
}

/**
 * Safe API call with retry logic.
 *
 * Automatically retries on network/timeout errors with exponential backoff.
 *
 * @param maxRetries Maximum number of retries (default: 3)
 * @param initialDelayMs Initial delay in milliseconds (default: 1000)
 * @param apiCall The suspend function to execute
 */
suspend inline fun <T> safeApiCallWithRetry(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    crossinline apiCall: suspend () -> Response<T>
): NetworkResult<T> = withContext(Dispatchers.IO) {
    var currentDelay = initialDelayMs
    var lastResult: NetworkResult<T>? = null

    repeat(maxRetries + 1) { attempt ->
        val result = safeApiCall(apiCall)

        if (result is NetworkResult.Success || result is NetworkResult.Empty) {
            return@withContext result
        }
        
        lastResult = result

        // If it's the last attempt, don't wait
        if (attempt == maxRetries) return@repeat

        // Check if error is retryable
        if (result is NetworkResult.Error) {
            val isRetryable = when (result.errorType) {
                ErrorType.NETWORK, 
                ErrorType.TIMEOUT, 
                ErrorType.SERVER_ERROR -> true
                else -> false
            }

            if (!isRetryable) {
                return@withContext result
            }
        }

        Timber.d("Retry attempt ${attempt + 1}/$maxRetries after ${currentDelay}ms")
        delay(currentDelay)
        currentDelay *= 2 // Exponential backoff
    }

    // Return the last error result, or unknown if null
    lastResult ?: NetworkResult.Error(
        exception = Exception("Max retries exceeded"),
        message = "تلاش‌های مکرر برای اتصال با شکست مواجه شد."
    )
}
