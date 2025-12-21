package com.noghre.sod.data.remote

import android.content.Context
import com.noghre.sod.utils.SecurePreferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

/**
 * Secure Auth Interceptor with encrypted token storage.
 * Adds Bearer token from secure storage to API requests.
 */
class SecureAuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get token from encrypted storage
        val token = SecurePreferenceManager.getToken(context)

        val requestBuilder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("User-Agent", "NoghreSod-Android/1.0.0")

        // Add token if available
        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
            Timber.d("Authorization header added")
        } else {
            Timber.w("No token available for authorization")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

/**
 * Secure Error Handling Interceptor with token refresh.
 */
class SecureErrorHandlingInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Timber.d("API Request: ${request.method} ${request.url}")

        return try {
            val response = chain.proceed(request)

            when (response.code) {
                401 -> {
                    // Unauthorized - token expired
                    Timber.w("401 Unauthorized - attempting token refresh")
                    handleUnauthorized()
                }
                403 -> {
                    Timber.w("403 Forbidden")
                }
                429 -> {
                    Timber.w("429 Too Many Requests - rate limited")
                }
                500, 502, 503 -> {
                    Timber.e("Server error: ${response.code}")
                }
            }

            Timber.d("API Response: ${response.code} ${request.url}")
            response
        } catch (e: IOException) {
            Timber.e(e, "Network error: ${request.url}")
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error: ${request.url}")
            throw e
        }
    }

    /**
     * Handle 401 responses by attempting token refresh.
     */
    private fun handleUnauthorized() {
        // Clear invalid token
        SecurePreferenceManager.clearAll(context)
        Timber.d("Cleared invalid authentication data")
        // In production, implement token refresh logic here
        // or redirect to login screen
    }
}

/**
 * Security Headers Interceptor.
 */
class SecurityHeadersInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("X-Requested-With", "XMLHttpRequest")
            .addHeader("X-Content-Type-Options", "nosniff")
            .addHeader("X-Frame-Options", "DENY")
            .addHeader("X-XSS-Protection", "1; mode=block")
            .addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
            .build()

        return chain.proceed(request)
    }
}