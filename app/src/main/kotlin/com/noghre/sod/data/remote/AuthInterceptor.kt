package com.noghre.sod.data.remote

import android.util.Log
import com.noghre.sod.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

/**
 * OkHttp interceptor for automatic token injection and refresh.
 * 
 * Responsibilities:
 * - Automatically inject access token in Authorization header
 * - Skip auth for login/register/refresh endpoints
 * - Automatically refresh expired tokens
 * - Handle 401 responses by refreshing token and retrying request
 * - Thread-safe token refresh with synchronization
 */
class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val apiService: ApiService
) : Interceptor {

    private val isRefreshing = AtomicBoolean(false)
    private val tokenRefreshLock = Any()

    companion object {
        private const val TAG = "AuthInterceptor"
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "

        // Endpoints that don't require authentication
        private val PUBLIC_ENDPOINTS = listOf(
            "auth/login",
            "auth/register",
            "auth/refresh",
            "auth/reset-password",
            "auth/verify-email"
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val path = request.url.encodedPath

        // Skip auth for public endpoints
        if (isPublicEndpoint(path)) {
            return chain.proceed(request)
        }

        // Add authorization header if token exists
        val accessToken = tokenManager.getAccessToken()
        if (accessToken != null) {
            request = request.withBearerToken(accessToken)
        }

        // Proceed with original request
        var response = chain.proceed(request)

        // Handle 401 Unauthorized - try to refresh token and retry
        if (response.code == 401 && !isPublicEndpoint(path)) {
            // Try to refresh the token
            val refreshedToken = refreshToken()

            if (refreshedToken != null) {
                // Retry original request with new token
                request = request.withBearerToken(refreshedToken)
                response.close()
                response = chain.proceed(request)
            } else {
                // Token refresh failed - clear tokens and return 401
                tokenManager.clearTokens()
                Log.w(TAG, "Token refresh failed. Cleared tokens.")
            }
        }

        return response
    }

    /**
     * Refresh the access token using the refresh token.
     * Thread-safe with synchronization to prevent multiple simultaneous refresh requests.
     *
     * @return New access token if refresh successful, null otherwise
     */
    private fun refreshToken(): String? {
        // Check if refresh is already in progress
        if (isRefreshing.getAndSet(true)) {
            // Wait for refresh to complete
            synchronized(tokenRefreshLock) {
                return tokenManager.getAccessToken()
            }
        }

        return try {
            synchronized(tokenRefreshLock) {
                val refreshToken = tokenManager.getRefreshToken()
                    ?: return null.also { Log.w(TAG, "No refresh token available") }

                // Make synchronous API call within coroutine context
                val response = runBlocking {
                    apiService.refreshToken(
                        RefreshTokenRequest(refreshToken = refreshToken)
                    )
                }

                if (response.isSuccessful) {
                    val authResponse = response.body()?.data
                    if (authResponse != null) {
                        tokenManager.saveTokens(
                            accessToken = authResponse.accessToken,
                            refreshToken = authResponse.refreshToken,
                            expiresInSeconds = authResponse.expiresIn
                        )
                        Log.d(TAG, "Token refreshed successfully")
                        return authResponse.accessToken
                    }
                } else {
                    Log.e(TAG, "Token refresh failed: ${response.code()} - ${response.message()}")
                }
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "Token refresh error: Network error", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Token refresh error: Unexpected error", e)
            null
        } finally {
            isRefreshing.set(false)
        }
    }

    /**
     * Check if the endpoint is public (doesn't require authentication).
     */
    private fun isPublicEndpoint(path: String): Boolean {
        return PUBLIC_ENDPOINTS.any { path.contains(it, ignoreCase = true) }
    }

    /**
     * Add Bearer token to request Authorization header.
     */
    private fun Request.withBearerToken(token: String): Request {
        return this.newBuilder()
            .header(AUTHORIZATION_HEADER, "$BEARER_PREFIX$token")
            .build()
    }
}

/**
 * Logging interceptor for debugging API requests and responses.
 */
class LoggingInterceptor : Interceptor {
    companion object {
        private const val TAG = "ApiLogging"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        Log.d(TAG, "---> REQUEST")
        Log.d(TAG, "Method: ${request.method}")
        Log.d(TAG, "URL: ${request.url}")
        Log.d(TAG, "Headers:")
        request.headers.forEach { (name, value) ->
            // Mask sensitive headers
            val displayValue = if (name.equals("Authorization", ignoreCase = true)) {
                "Bearer ${value.substringAfter("Bearer ").take(20)}..."
            } else {
                value
            }
            Log.d(TAG, "  $name: $displayValue")
        }

        val startTime = System.currentTimeMillis()
        val response = chain.proceed(request)
        val duration = System.currentTimeMillis() - startTime

        Log.d(TAG, "<--- RESPONSE")
        Log.d(TAG, "Status: ${response.code}")
        Log.d(TAG, "Duration: ${duration}ms")
        Log.d(TAG, "Headers:")
        response.headers.forEach { (name, value) ->
            Log.d(TAG, "  $name: $value")
        }

        return response
    }
}

/**
 * Retry interceptor for automatic retry on network failures.
 */
class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val initialDelayMs: Long = 1000,
    private val backoffMultiplier: Double = 2.0,
    private val maxDelayMs: Long = 10000
) : Interceptor {

    companion object {
        private const val TAG = "RetryInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var retryCount = 0
        var delayMs = initialDelayMs
        var lastException: IOException? = null

        while (retryCount < maxRetries) {
            try {
                return chain.proceed(chain.request())
            } catch (e: IOException) {
                lastException = e
                retryCount++

                if (retryCount >= maxRetries) {
                    Log.e(TAG, "Max retries ($maxRetries) reached. Giving up.")
                    throw e
                }

                Log.w(TAG, "Request failed (attempt $retryCount/$maxRetries). Retrying in ${delayMs}ms...")
                Thread.sleep(delayMs)

                // Calculate next delay with exponential backoff
                delayMs = (delayMs * backoffMultiplier).toLong().coerceAtMost(maxDelayMs)
            }
        }

        throw lastException ?: IOException("Unknown error after $maxRetries retries")
    }
}
