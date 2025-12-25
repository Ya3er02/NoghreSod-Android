package com.noghre.sod.data.remote.interceptor

import com.noghre.sod.domain.model.NetworkException
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * OkHttp interceptor for automatic retry on network failures.
 * Implements exponential backoff strategy.
 */
class RetryInterceptor(
    private val maxRetries: Int = MAX_RETRIES,
    private val initialDelayMs: Long = INITIAL_DELAY_MS
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var currentDelay = initialDelayMs
        var lastException: Exception? = null

        while (attempt <= maxRetries) {
            try {
                Timber.d("Network request attempt: ${attempt + 1}/$maxRetries")
                return chain.proceed(chain.request())
            } catch (e: Exception) {
                lastException = e
                attempt++

                // Don't retry if max retries reached
                if (attempt > maxRetries) {
                    Timber.e(e, "Max retries ($maxRetries) exceeded")
                    throw NetworkException(
                        message = "Network request failed after $maxRetries attempts",
                        cause = e
                    )
                }

                // Only retry specific exceptions
                if (!shouldRetry(e)) {
                    throw e
                }

                Timber.w("Network request failed, retrying in ${currentDelay}ms: ${e.message}")
                Thread.sleep(currentDelay)

                // Exponential backoff: delay * 2 for next attempt
                currentDelay = (currentDelay * 2).coerceAtMost(MAX_DELAY_MS)
            }
        }

        throw lastException ?: IOException("Unknown network error")
    }

    /**
     * Determine if the error should trigger a retry.
     */
    private fun shouldRetry(exception: Exception): Boolean {
        return when (exception) {
            // Retry on timeout
            is SocketTimeoutException -> {
                Timber.d("Socket timeout, retrying...")
                true
            }
            // Retry on connection errors
            is IOException -> {
                Timber.d("IO exception: ${exception.message}, retrying...")
                true
            }
            else -> {
                Timber.d("Non-retryable exception: ${exception.javaClass.simpleName}")
                false
            }
        }
    }

    companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_DELAY_MS = 500L
        private const val MAX_DELAY_MS = 5000L
    }
}

/**
 * Network exception for better error handling.
 */
class NetworkException(
    message: String,
    val isRetryable: Boolean = true,
    cause: Throwable? = null
) : Exception(message, cause)
