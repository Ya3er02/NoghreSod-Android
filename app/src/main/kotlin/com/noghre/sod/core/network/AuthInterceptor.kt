package com.noghre.sod.core.network

import android.content.Context
import com.noghre.sod.data.local.preferences.PreferencesManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

/**
 * Interceptor for handling authentication in network requests.
 * 
* Adds auth token to all requests.
 * Handles token refresh on 401 unauthorized.
 * Logs requests and responses.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
class AuthInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        var request = originalRequest

        try {
            // Get auth token
            val token = runBlocking {
                preferencesManager.getAuthToken()
            }

            // Add token to request if available
            if (!token.isNullOrBlank()) {
                request = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("Content-Type", "application/json")
                    .build()

                Timber.d("Adding auth token to request: ${originalRequest.url}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error adding auth token")
        }

        // Log request
        Timber.d("→ Request: ${request.method} ${request.url}")
        for ((name, value) in request.headers) {
            if (name.lowercase() != "authorization") {
                Timber.d("  Header: $name: $value")
            }
        }

        return try {
            // Execute request
            val response = chain.proceed(request)

            // Log response
            Timber.d("← Response: ${response.code} for ${response.request.url}")

            // Handle 401 unauthorized
            if (response.code == 401) {
                Timber.w("Unauthorized (401) - Token may be expired")

                // Try to refresh token
                val refreshed = refreshToken()
                if (refreshed) {
                    Timber.d("Token refreshed, retrying request")

                    // Get new token and retry
                    return try {
                        val newToken = runBlocking {
                            preferencesManager.getAuthToken()
                        }

                        val retryRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer $newToken")
                            .header("Content-Type", "application/json")
                            .build()

                        chain.proceed(retryRequest)
                    } catch (e: Exception) {
                        Timber.e(e, "Error retrying request with new token")
                        response
                    }
                } else {
                    Timber.w("Failed to refresh token")
                    response
                }
            } else {
                response
            }
        } catch (e: Exception) {
            Timber.e(e, "Network error: ${e.localizedMessage}")
            throw e
        }
    }

    /**
     * Attempt to refresh authentication token.
     */
    private fun refreshToken(): Boolean {
        return try {
            Timber.d("Attempting to refresh token")

            // This would call the API to refresh token
            // For now, we'll just return true as a placeholder
            // The actual implementation should be in AuthRepository

            true
        } catch (e: Exception) {
            Timber.e(e, "Token refresh failed: ${e.localizedMessage}")
            false
        }
    }
}

/**
 * Logging Interceptor for debugging.
 * 
* Logs detailed request and response information.
 */
class LoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.nanoTime()

        Timber.d(
            "Sending request: ${request.method} ${request.url}\n" +
                    "Headers: ${request.headers}"
        )

        return try {
            val response = chain.proceed(request)
            val elapsedTime = (System.nanoTime() - startTime) / 1_000_000

            Timber.d(
                "Received response: ${response.code}\n" +
                        "Elapsed time: ${elapsedTime}ms\n" +
                        "Headers: ${response.headers}"
            )

            response
        } catch (e: Exception) {
            val elapsedTime = (System.nanoTime() - startTime) / 1_000_000
            Timber.e("Request failed after ${elapsedTime}ms: ${e.message}")
            throw e
        }
    }
}
