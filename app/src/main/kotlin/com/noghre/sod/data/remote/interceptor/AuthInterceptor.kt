package com.noghre.sod.data.remote.interceptor

import com.noghre.sod.data.local.prefs.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * OkHttp Interceptor for adding authentication token to requests.
 * Automatically includes Bearer token in Authorization header for authenticated endpoints.
 */
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    private val authExemptEndpoints = listOf(
        "auth/login",
        "auth/register",
        "auth/verify-otp",
        "auth/refresh"
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url.toString()

        // Skip adding token for auth endpoints
        if (authExemptEndpoints.any { requestUrl.contains(it) }) {
            return chain.proceed(originalRequest)
        }

        val token = tokenManager.getAccessToken()
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
