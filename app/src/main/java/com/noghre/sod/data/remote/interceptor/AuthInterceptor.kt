package com.noghre.sod.data.remote.interceptor

import com.noghre.sod.data.local.prefs.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

/**
 * Interceptor for adding JWT authentication to requests
 * Automatically includes access token and handles token refresh on 401
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val accessToken = tokenManager.getAccessToken()

        val newRequest = if (!accessToken.isNullOrEmpty()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()
        } else {
            request.newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
        }

        Timber.d("Request: ${newRequest.url} with token: ${accessToken?.take(10)}...")

        val response = chain.proceed(newRequest)

        // Handle 401 Unauthorized - Token expired
        if (response.code == 401) {
            Timber.w("Received 401 Unauthorized - clearing tokens")
            tokenManager.clearTokens()
        }

        return response
    }
}