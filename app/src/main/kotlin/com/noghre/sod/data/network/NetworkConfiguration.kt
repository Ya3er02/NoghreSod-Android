package com.noghre.sod.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.pow

// ============================================
// 🚀 Retry Interceptor with Exponential Backoff
// ============================================

class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val initialDelay: Long = 1000L // 1 second
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var response: Response? = null
        var retryCount = 0
        var exception: IOException? = null
        
        while (retryCount <= maxRetries) {
            try {
                response = chain.proceed(request)
                
                // Check if response is successful
                if (response.isSuccessful) {
                    return response
                }
                
                // Check if should retry on specific status codes
                if (shouldRetry(response.code)) {
                    response.close()
                    
                    if (retryCount < maxRetries) {
                        retryCount++
                        val delay = calculateDelay(retryCount)
                        Thread.sleep(delay)
                        continue
                    }
                }
                
                return response
            } catch (e: IOException) {
                exception = e
                
                if (retryCount < maxRetries && isRetryableException(e)) {
                    retryCount++
                    val delay = calculateDelay(retryCount)
                    Thread.sleep(delay)
                } else {
                    throw e
                }
            }
        }
        
        return response ?: throw exception ?: IOException("Unknown error")
    }
    
    /**
     * Check if response status code should be retried
     */
    private fun shouldRetry(statusCode: Int): Boolean {
        return statusCode in listOf(
            408,  // Request Timeout
            429,  // Too Many Requests
            500,  // Internal Server Error
            502,  // Bad Gateway
            503,  // Service Unavailable
            504   // Gateway Timeout
        )
    }
    
    /**
     * Check if exception is retryable
     */
    private fun isRetryableException(e: Exception): Boolean {
        return e is IOException && !(e is HttpException)
    }
    
    /**
     * Calculate exponential backoff delay
     * Formula: initialDelay * 2^(retryCount-1)
     */
    private fun calculateDelay(retryCount: Int): Long {
        return (initialDelay * 2.0.pow(retryCount - 1)).toLong()
    }
}

// ============================================
// 💫 Safe API Call Wrapper
// ============================================

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Result<T> {
    return try {
        Result.success(apiCall())
    } catch (e: HttpException) {
        Result.failure(
            ApiException.HttpError(
                statusCode = e.code(),
                message = e.message.orEmpty()
            )
        )
    } catch (e: IOException) {
        Result.failure(
            ApiException.NetworkError(e.message.orEmpty())
        )
    } catch (e: Exception) {
        Result.failure(
            ApiException.UnknownError(e.message.orEmpty())
        )
    }
}

// ============================================
// 📎 API Exception Types
// ============================================

sealed class ApiException : Exception() {
    data class HttpError(
        val statusCode: Int,
        override val message: String
    ) : ApiException()
    
    data class NetworkError(
        override val message: String
    ) : ApiException()
    
    data class UnknownError(
        override val message: String
    ) : ApiException()
    
    object EmptyResponse : ApiException()
    object Unauthorized : ApiException()
}

// ============================================
// 🔍 Network Monitor
// ============================================

interface NetworkMonitor {
    val isOnline: Boolean
}

// ============================================
// 🔗 OkHttpClient Builder Configuration
// ============================================

fun createOkHttpClient(
    maxRetries: Int = 3,
    connectTimeout: Long = 30,
    readTimeout: Long = 30,
    writeTimeout: Long = 30
): OkHttpClient {
    return OkHttpClient.Builder()
        // Add retry interceptor
        .addInterceptor(RetryInterceptor(maxRetries))
        // Add logging interceptor (optional, remove in production)
        .addInterceptor(
            createLoggingInterceptor()
        )
        // Timeout configurations
        .connectTimeout(connectTimeout, TimeUnit.SECONDS)
        .readTimeout(readTimeout, TimeUnit.SECONDS)
        .writeTimeout(writeTimeout, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS) // Total timeout
        // Connection pool
        .connectionPool(okhttp3.ConnectionPool(5, 5, TimeUnit.MINUTES))
        .build()
}

/**
 * Create logging interceptor for debugging
 */
private fun createLoggingInterceptor(): okhttp3.logging.HttpLoggingInterceptor {
    return okhttp3.logging.HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        } else {
            okhttp3.logging.HttpLoggingInterceptor.Level.NONE
        }
    }
}
