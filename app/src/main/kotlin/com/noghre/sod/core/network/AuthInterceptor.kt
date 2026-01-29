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
 * - Adds auth token to all requests.
 * - Handles token refresh on 401 unauthorized.
 * - Thread-safe and robust error handling.
 *
 * @author NoghreSod Team
 * @version 1.1.0
 */
class AuthInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val context: Context,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        var request = originalRequest

        try {
            // Get auth token synchronously (since Interceptor is blocking)
            // Ideally, PreferencesManager should provide a synchronous way or we use runBlocking
            val token = runBlocking {
                try {
                    preferencesManager.getAuthToken()
                } catch (e: Exception) {
                    Timber.e(e, "Error reading auth token")
                    null
                }
            }

            // Add token to request if available
            if (!token.isNullOrBlank()) {
                request = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("Content-Type", "application/json")
                    .build()
            }
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error in AuthInterceptor setup")
        }

        return try {
            // Execute request
            val response = chain.proceed(request)

            // Handle 401 unauthorized
            if (response.code == 401) {
                Timber.w("Unauthorized (401) - Token may be expired")
                
                // Close response body before retrying to avoid leakage
                response.close()

                // Try to refresh token
                // NOTE: synchronized to prevent multiple threads refreshing at once
                synchronized(this) {
                    val refreshed = refreshToken()
                    if (refreshed) {
                        Timber.d("Token refreshed, retrying request")

                        // Get new token and retry
                        val newToken = runBlocking { preferencesManager.getAuthToken() }
                        
                        if (!newToken.isNullOrBlank()) {
                            val retryRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer $newToken")
                                .header("Content-Type", "application/json")
                                .build()
                            return chain.proceed(retryRequest)
                        }
                    }
                }
                
                // If refresh failed or logic not implemented, return original response (re-executed or new error)
                // Since we closed original response, we technically need to re-execute or return a new 401 response
                // For simplicity here, we assume if refresh failed, the user needs to login again.
                // Re-executing original to get a fresh 401 response is safest if we closed it.
                return chain.proceed(request)
            }

            response
        } catch (e: Exception) {
            Timber.e(e, "Network request failed in AuthInterceptor: ${e.message}")
            throw e
        }
    }

    /**
     * Attempt to refresh authentication token.
     */
    private fun refreshToken(): Boolean {
        return try {
            Timber.d("Attempting to refresh token")

            // TODO: Implement actual token refresh logic here.
            // Call AuthRepository to refresh token using refresh_token.
            // For now, return false to indicate refresh is not fully implemented/failed.
            // Returning true blindly causes infinite loops if refresh actually fails.
            
            false 
        } catch (e: Exception) {
            Timber.e(e, "Token refresh failed: ${e.message}")
            false
        }
    }
}
