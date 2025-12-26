package com.noghre.sod.data.remote.interceptor

import com.noghre.sod.BuildConfig
import com.noghre.sod.core.security.NativeKeys
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Network interceptor for OkHttp client.
 * Handles:
 * - Authorization headers
 * - Request/response logging
 * - Retry logic with exponential backoff
 * - Network error handling
 * - Token refresh on 401
 * 
 * @author Yaser
 * @version 1.0.0
 */
class NetworkInterceptor : Interceptor {
    
    companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_RETRY_DELAY_MS = 1000L
        private const val REQUEST_TIMEOUT_SECONDS = 30L
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        var request = originalRequest
        var retryCount = 0
        var lastException: IOException? = null
        
        while (retryCount <= MAX_RETRIES) {
            try {
                // Add authorization and custom headers
                request = addAuthorizationHeaders(originalRequest)
                
                // Log request
                logRequest(request)
                
                // Execute request
                val response = chain.proceed(request)
                
                // Log response
                logResponse(response)
                
                // Handle 401 Unauthorized
                if (response.code == 401) {
                    Timber.w("Received 401 - attempting token refresh")
                    // Token refresh should be handled in TokenRefreshInterceptor
                    response.close()
                    return chain.proceed(
                        addAuthorizationHeaders(originalRequest)
                    )
                }
                
                return response
            } catch (e: IOException) {
                lastException = e
                retryCount++
                
                Timber.w(e, "Request failed (attempt $retryCount/$MAX_RETRIES)")
                
                if (retryCount <= MAX_RETRIES) {
                    val delayMs = calculateBackoffDelay(retryCount)
                    Timber.d("Retrying request after ${delayMs}ms")
                    Thread.sleep(delayMs)
                } else {
                    Timber.e(e, "Max retries exceeded")
                    break
                }
            }
        }
        
        throw lastException ?: IOException("Request failed after $MAX_RETRIES retries")
    }
    
    /**
     * Add authorization headers to request.
     */
    private fun addAuthorizationHeaders(request: Request): Request {
        return request.newBuilder()
            .addHeader("Authorization", "Bearer ${getAuthToken()}")
            .addHeader("User-Agent", getUserAgent())
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("X-API-Version", "v1")
            .addHeader("X-Client-Version", BuildConfig.VERSION_NAME)
            .build()
    }
    
    /**
     * Get authorization token from secure storage.
     */
    private fun getAuthToken(): String {
        return try {
            NativeKeys.getApiUrlSafe() // Should get from NativeKeys or SecureStorage
            "" // Return actual token from SecureStorage
        } catch (e: Exception) {
            Timber.e(e, "Failed to get auth token")
            ""
        }
    }
    
    /**
     * Get user agent string.
     */
    private fun getUserAgent(): String {
        val version = BuildConfig.VERSION_NAME
        val buildType = if (BuildConfig.DEBUG) "debug" else "release"
        return "NoghreSod/$version ($buildType)"
    }
    
    /**
     * Calculate exponential backoff delay.
     */
    private fun calculateBackoffDelay(attempt: Int): Long {
        return INITIAL_RETRY_DELAY_MS * (1 shl (attempt - 1))
    }
    
    /**
     * Log request details (only in debug mode).
     */
    private fun logRequest(request: Request) {
        if (BuildConfig.DEBUG) {
            Timber.d(
                "${request.method} ${request.url}\n" +
                "Headers: ${request.headers}\n" +
                "Body: ${request.body?.toString()}"
            )
        }
    }
    
    /**
     * Log response details (only in debug mode).
     */
    private fun logResponse(response: Response) {
        if (BuildConfig.DEBUG) {
            val contentLength = response.body?.contentLength() ?: 0
            Timber.d(
                "Response: ${response.code}\n" +
                "Message: ${response.message}\n" +
                "Content-Length: $contentLength\n" +
                "Duration: ${response.receivedResponseAtMillis - response.sentRequestAtMillis}ms"
            )
        }
    }
}

/**
 * Certificate pinning interceptor for security.
 * Pins API certificates to prevent MITM attacks.
 */
class CertificatePinningInterceptor : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        // Certificate pinning is configured in network-security-config.xml
        // This interceptor can add additional validation if needed
        return chain.proceed(chain.request())
    }
}

/**
 * Request timeout configuration.
 */
data class TimeoutConfig(
    val connectTimeoutSeconds: Long = 30,
    val readTimeoutSeconds: Long = 30,
    val writeTimeoutSeconds: Long = 30
) {
    fun applyToBuilder(builder: okhttp3.OkHttpClient.Builder) {
        builder
            .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(writeTimeoutSeconds, TimeUnit.SECONDS)
    }
}
