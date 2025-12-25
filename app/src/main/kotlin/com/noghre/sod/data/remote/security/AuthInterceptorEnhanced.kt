package com.noghre.sod.data.remote.security

import com.noghre.sod.data.local.security.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enhanced Auth Interceptor for handling JWT token-based authentication.
 * Automatically adds Authorization header and refreshes tokens when expired.
 */
@Singleton
class AuthInterceptorEnhanced @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth for specific endpoints (login, register, etc.)
        if (isPublicEndpoint(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }

        // Get current access token
        val accessToken = tokenManager.getAccessToken()

        if (accessToken.isNullOrBlank()) {
            Timber.w("No access token available")
            return chain.proceed(originalRequest) // Continue without auth
        }

        // Add Authorization header
        val authenticatedRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("X-App-Version", getAppVersion())
            .addHeader("X-Client-Type", "android")
            .build()

        var response = chain.proceed(authenticatedRequest)

        // Handle token refresh if 401 Unauthorized
        if (response.code == 401) {
            Timber.d("Received 401, attempting token refresh")
            response.close()

            val newAccessToken = tokenManager.refreshAccessToken()
            if (!newAccessToken.isNullOrBlank()) {
                // Retry request with new token
                val retryRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $newAccessToken")
                    .addHeader("X-App-Version", getAppVersion())
                    .addHeader("X-Client-Type", "android")
                    .build()

                response = chain.proceed(retryRequest)
                Timber.d("Token refreshed and request retried")
            } else {
                Timber.e("Failed to refresh token")
                // Could trigger logout here
            }
        }

        return response
    }

    /**
     * Check if endpoint is public (doesn't require authentication).
     */
    private fun isPublicEndpoint(path: String): Boolean {
        return path.contains("/auth/login") ||
                path.contains("/auth/register") ||
                path.contains("/auth/forgot-password") ||
                path.contains("/products") && path.contains("GET")
    }

    /**
     * Get app version from BuildConfig.
     */
    private fun getAppVersion(): String {
        return try {
            "1.0.0" // Replace with actual BuildConfig.VERSION_NAME
        } catch (e: Exception) {
            Timber.e(e, "Failed to get app version")
            "1.0.0"
        }
    }
}

/**
 * Logging Interceptor for HTTP request/response logging.
 */
@Singleton
class LoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val startTime = System.currentTimeMillis()
        val request = chain.request()

        // Log request
        Timber.d("""
            ➜ Request
            Method: ${request.method}
            URL: ${request.url}
            Headers: ${request.headers}
        """)

        request.body?.let { body ->
            Timber.d("Request Body: ${body.contentLength()} bytes")
        }

        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            Timber.e(e, "Network request failed")
            throw e
        }

        val duration = System.currentTimeMillis() - startTime

        // Log response
        Timber.d("""
            ← Response
            Status Code: ${response.code}
            Duration: ${duration}ms
            Content-Type: ${response.header("content-type")}
        """)

        return response
    }
}

/**
 * Error Recovery Interceptor for handling specific error codes.
 */
@Singleton
class ErrorRecoveryInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        when (response.code) {
            429 -> { // Too Many Requests
                Timber.w("Rate limited: ${response.header("Retry-After")}")
            }
            500, 502, 503, 504 -> { // Server Errors
                Timber.e("Server error ${response.code}")
            }
            401 -> { // Unauthorized
                Timber.w("Unauthorized access")
            }
            403 -> { // Forbidden
                Timber.w("Access forbidden")
            }
        }

        return response
    }
}
