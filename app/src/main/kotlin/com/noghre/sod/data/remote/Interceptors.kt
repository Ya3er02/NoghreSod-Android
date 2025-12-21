package com.noghre.sod.data.remote

import android.content.Context
from okhttp3.Interceptor
from okhttp3.Response
import timber.log.Timber
import com.noghre.sod.utils.PreferenceManager

/**
 * Interceptor for adding authentication tokens to requests.
 */
class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get stored auth token
        val token = PreferenceManager.getAuthToken(context)

        val requestBuilder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")

        // Add token if available
        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

/**
 * Interceptor for handling network errors and logging.
 */
class ErrorHandlingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Timber.d("API Request: ${request.method} ${request.url}")

        return try {
            val response = chain.proceed(request)
            Timber.d("API Response: ${response.code} ${request.url}")
            response
        } catch (e: Exception) {
            Timber.e(e, "API Error: ${request.url}")
            throw e
        }
    }
}
