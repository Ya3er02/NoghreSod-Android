package com.noghre.sod.data.remote.interceptor

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import com.noghre.sod.data.local.SecurePreferences
import com.noghre.sod.domain.manager.TokenManager

/**
 * Authentication Interceptor
 * 
 * Automatically attaches JWT tokens to outgoing requests.
 * Handles 401 Unauthorized responses by refreshing tokens.
 * 
 * Flow:
 * 1. Attach access token to Authorization header
 * 2. If response is 401, attempt token refresh
 * 3. Retry request with new token
 * 4. If refresh fails, trigger logout
 * 
 * @since 1.0.0
 */
class AuthInterceptor(
    private val securePreferences: SecurePreferences,
    private val tokenManager: TokenManager
) : Interceptor {
    
    companion object {
        private const val TAG = "AuthInterceptor"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val TOKEN_PREFIX = "Bearer "
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth for public endpoints
        if (shouldSkipAuth(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }
        
        // Attach access token
        val accessToken = securePreferences.getAccessToken()
        val authenticatedRequest = if (!accessToken.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header(HEADER_AUTHORIZATION, "$TOKEN_PREFIX$accessToken")
                .build()
        } else {
            originalRequest
        }
        
        val response = chain.proceed(authenticatedRequest)
        
        // Handle 401 Unauthorized - attempt token refresh
        if (response.code == 401 && !accessToken.isNullOrEmpty()) {
            response.close()  // Close the failed response
            return handleTokenRefresh(chain, originalRequest)
        }
        
        return response
    }
    
    /**
     * Check if endpoint should skip authentication
     * 
     * Public endpoints that don't require tokens
     */
    private fun shouldSkipAuth(path: String): Boolean {
        return path.contains("/auth/login") ||
               path.contains("/auth/register") ||
               path.contains("/auth/request-otp") ||
               path.contains("/auth/verify-otp") ||
               path.contains("/auth/refresh-token") ||
               path.contains("/auth/forgot-password") ||
               path.contains("/products")  // Public product listing
    }
    
    /**
     * Handle 401 response by attempting token refresh
     * 
     * @param chain Interceptor chain
     * @param originalRequest Original request that got 401
     * @return Response with new token or error response
     */
    private fun handleTokenRefresh(
        chain: Interceptor.Chain,
        originalRequest: okhttp3.Request
    ): Response {
        return runBlocking {
            try {
                // Refresh token (thread-safe)
                val newToken = tokenManager.refreshTokenSynchronized()
                
                if (!newToken.isNullOrEmpty()) {
                    Log.d(TAG, "✅ Token refreshed successfully")
                    
                    // Retry original request with new token
                    val newRequest = originalRequest.newBuilder()
                        .header(HEADER_AUTHORIZATION, "$TOKEN_PREFIX$newToken")
                        .build()
                    
                    chain.proceed(newRequest)
                } else {
                    // Refresh failed - logout user
                    Log.w(TAG, "❌ Token refresh failed - triggering logout")
                    tokenManager.logoutUser()
                    
                    // Proceed with original request (will be 401 again)
                    chain.proceed(originalRequest)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "🚨 Error during token refresh: ${e.message}")
                tokenManager.logoutUser()
                chain.proceed(originalRequest)
            }
        }
    }
}
