package com.noghre.sod.data.remote

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
from com.noghre.sod.data.local.TokenManager

/**
 * OkHttp interceptor that automatically handles JWT token injection and refresh.
 * Features:
 * - Adds Authorization header to requests
 * - Skips auth for login/register endpoints
 * - Detects and refreshes expired tokens
 * - Retries failed requests after token refresh
 * - Synchronizes token refresh to avoid race conditions
 * - Clears tokens on permanent refresh failure
 */
class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val apiService: ApiService
) : Interceptor {

    private val TAG = "AuthInterceptor"
    private val tokenLock = Object() // For synchronizing token refresh

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        // Skip adding auth header for login/register endpoints
        if (shouldSkipAuth(url)) {
            return chain.proceed(originalRequest)
        }

        // Check if token needs refresh before making request
        if (!tokenManager.hasValidAccessToken()) {
            Log.d(TAG, "Token expired, attempting refresh")
            if (!refreshTokenSynchronized()) {
                // Refresh failed, proceed with expired token and let the 401 handling below deal with it
                Log.e(TAG, "Token refresh failed")
            }
        }

        // Add authorization header
        val token = tokenManager.getAccessToken()
        val requestWithAuth = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        var response = chain.proceed(requestWithAuth)

        // Handle 401 response (unauthorized)
        if (response.code == 401 && token != null) {
            Log.d(TAG, "Received 401 Unauthorized, attempting token refresh")
            response.close()

            // Try to refresh token
            if (refreshTokenSynchronized()) {
                // Get new token and retry original request
                val newToken = tokenManager.getAccessToken()
                if (newToken != null) {
                    Log.d(TAG, "Token refreshed successfully, retrying request")
                    val retryRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                    response = chain.proceed(retryRequest)
                }
            } else {
                // Token refresh failed, clear tokens and let request fail
                Log.e(TAG, "Token refresh failed after 401")
                tokenManager.clearTokens()
            }
        }

        return response
    }

    /**
     * Refreshes the access token synchronously.
     * Uses a lock to prevent multiple simultaneous refresh attempts.
     *
     * @return true if refresh was successful, false otherwise
     */
    private fun refreshTokenSynchronized(): Boolean {
        synchronized(tokenLock) {
            // Double-check if token was already refreshed by another thread
            if (tokenManager.hasValidAccessToken()) {
                Log.d(TAG, "Token already refreshed by another thread")
                return true
            }

            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken == null) {
                Log.e(TAG, "No refresh token available")
                return false
            }

            return try {
                // Synchronously call refresh endpoint
                val request = RefreshTokenRequest(refreshToken)
                val response = runBlocking {
                    apiService.refreshToken(request)
                }

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.d(TAG, "Token refreshed successfully")
                    tokenManager.updateAccessToken(
                        body.accessToken,
                        body.expiresIn
                    )
                    true
                } else {
                    Log.e(TAG, "Token refresh failed: ${response.code()}")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Token refresh exception: ", e)
                false
            }
        }
    }

    /**
     * Determines if this endpoint should skip authentication.
     *
     * @param url The request URL
     * @return true if auth should be skipped
     */
    private fun shouldSkipAuth(url: String): Boolean {
        val skipEndpoints = listOf(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/auth/forgot-password",
            "/auth/reset-password"
        )
        return skipEndpoints.any { url.contains(it) }
    }
}

/**
 * Request body for token refresh endpoint.
 */
data class RefreshTokenRequest(
    val refreshToken: String
)

/**
 * Response from token refresh endpoint.
 */
data class RefreshTokenResponse(
    val accessToken: String,
    val expiresIn: Long
)
