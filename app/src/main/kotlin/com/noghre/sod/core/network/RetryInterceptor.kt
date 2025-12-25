package com.noghre.sod.core.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.math.pow

/**
 * OkHttp interceptor that automatically retries failed requests with exponential backoff.
 * Only retries on specific network errors and timeout, not on server errors.
 */
class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val initialDelayMs: Long = 1000,
    private val backoffMultiplier: Double = 2.0
) : Interceptor {

    private companion object {
        const val TAG = "RetryInterceptor"
        // HTTP status codes that should NOT be retried
        val UNRETRYABLE_STATUS_CODES = setOf(
            400, // Bad Request
            401, // Unauthorized
            403, // Forbidden
            404, // Not Found
            405, // Method Not Allowed
            406, // Not Acceptable
            409, // Conflict
            410, // Gone
            422, // Unprocessable Entity
            451  // Unavailable For Legal Reasons
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        var request = originalRequest
        var response: Response? = null
        var exception: Exception? = null

        for (attempt in 1..maxRetries) {
            try {
                response = chain.proceed(request)

                // Successfully got response
                if (response.isSuccessful) {
                    Log.d(TAG, "Request successful on attempt $attempt")
                    return response
                }

                // Check if we should retry this response
                if (!shouldRetry(response.code(), attempt)) {
                    Log.d(TAG, "Not retrying due to status code ${response.code()}")
                    return response
                }

                Log.w(TAG, "Request failed with status ${response.code()}, retrying...")
                response.close()

            } catch (e: Exception) {
                exception = e
                
                // Check if error is retryable
                if (!isRetryableException(e)) {
                    Log.e(TAG, "Non-retryable exception: ${e.javaClass.simpleName}", e)
                    throw e
                }

                Log.w(TAG, "Request failed with exception: ${e.message}, retrying...", e)
            }

            // Don't delay after the last attempt
            if (attempt < maxRetries) {
                val delayMs = calculateDelay(attempt)
                Log.d(TAG, "Waiting ${delayMs}ms before retry attempt ${attempt + 1}")
                Thread.sleep(delayMs)
            }
        }

        // All retries exhausted
        return response ?: throw exception ?: Exception("Request failed after $maxRetries attempts")
    }

    /**
     * Determine if response should be retried based on status code.
     */
    private fun shouldRetry(statusCode: Int, attempt: Int): Boolean {
        // Don't retry unretryable status codes
        if (statusCode in UNRETRYABLE_STATUS_CODES) {
            return false
        }

        // Retry on server errors (5xx)
        if (statusCode >= 500) {
            return attempt < maxRetries
        }

        return false
    }

    /**
     * Check if exception is retryable.
     */
    private fun isRetryableException(exception: Exception): Boolean {
        return when (exception) {
            is java.net.SocketTimeoutException -> true
            is java.net.ConnectException -> true
            is java.net.SocketException -> true
            is java.io.IOException -> true
            else -> false
        }
    }

    /**
     * Calculate delay with exponential backoff.
     * Formula: initialDelayMs * (backoffMultiplier ^ (attempt - 1))
     */
    private fun calculateDelay(attempt: Int): Long {
        val delayMs = initialDelayMs * backoffMultiplier.pow(attempt - 1).toLong()
        // Add jitter to prevent thundering herd
        val jitter = (Math.random() * 1000).toLong()
        return (delayMs + jitter).coerceAtMost(30_000) // Max 30 seconds
    }
}