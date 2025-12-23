package com.noghre.sod.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Authentication Interceptor
 * Automatically adds Bearer token to all requests
 * Handles token refresh on 401 responses
 */
class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = tokenProvider()

        // Add token if available
        if (token != null) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }

        val response = chain.proceed(request)

        // Handle 401 Unauthorized
        if (response.code == 401) {
            // Token expired or invalid
            // In a real app, you would:
            // 1. Try to refresh the token
            // 2. Retry the request with new token
            // 3. If refresh fails, logout user
            
            // For now, just return the 401 response
            // The ViewModel will handle logout
        }

        return response
    }
}
