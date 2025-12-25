package com.noghre.sod.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Retry Interceptor with Exponential Backoff
 * 
 * Automatically retries failed requests with exponential backoff.
 * 
 * Retryable scenarios:
 * - Network errors (timeout, connection refused, unknown host)
 * - HTTP errors (408, 429, 500, 502, 503, 504)
 * 
 * Exponential backoff: 1s, 2s, 4s (with 500ms jitter)
 * 
 * @since 1.0.0
 */
class RetryInterceptor : Interceptor {
    
    companion object {
        private const val MAX_RETRIES = 3
        private const val BASE_DELAY = 1000L // 1 second
        private const val MAX_BACKOFF = 10_000L // 10 seconds
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastException: IOException? = null
        
        // Retry with exponential backoff
        for (attempt in 0 until MAX_RETRIES) {
            try {
                // Add delay before retry (except first attempt)
                if (attempt > 0) {
                    val delay = calculateBackoff(attempt)
                    Timber.d("🔄 Retry attempt $attempt after ${delay}ms delay")
                    Thread.sleep(delay)
                }
                
                val response = chain.proceed(request)
                
                // Check if response is retryable
                if (shouldRetry(response)) {
                    response.close()
                    lastException = IOException("Retryable HTTP ${response.code}")
                    Timber.w("🔄 HTTP ${response.code} is retryable, attempt ${attempt + 1}/$MAX_RETRIES")
                    continue
                }
                
                return response
                
            } catch (e: IOException) {
                lastException = e
                
                // Only retry on network errors
                if (!isRetryableException(e)) {
                    throw e
                }
                
                Timber.w(e, "🔄 Request failed (${e.javaClass.simpleName}), attempt ${attempt + 1}/$MAX_RETRIES")
            }
        }
        
        // All retries failed
        throw lastException ?: IOException("Max retries exceeded")
    }
    
    /**
     * Determine if HTTP response should be retried
     */
    private fun shouldRetry(response: Response): Boolean {
        return when (response.code) {
            408, // Request Timeout
            429, // Too Many Requests
            500, // Internal Server Error
            502, // Bad Gateway
            503, // Service Unavailable
            504  // Gateway Timeout
            -> true
            else -> false
        }
    }
    
    /**
     * Determine if exception is retryable
     */
    private fun isRetryableException(exception: IOException): Boolean {
        return when (exception) {
            is SocketTimeoutException,
            is ConnectException,
            is UnknownHostException
            -> true
            else -> false
        }
    }
    
    /**
     * Calculate exponential backoff with jitter
     * 
     * Formula: (2^attempt * baseDelay) + random(0-500)
     * 
     * Attempt 1: ~1000ms
     * Attempt 2: ~2000ms
     * Attempt 3: ~4000ms
     */
    private fun calculateBackoff(attempt: Int): Long {
        val exponentialDelay = BASE_DELAY * (1 shl attempt) // 2^attempt
        val jitter = (0..500).random()
        val totalDelay = exponentialDelay + jitter
        
        return totalDelay.coerceAtMost(MAX_BACKOFF)
    }
}
