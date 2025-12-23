package com.noghre.sod.data.remote.interceptor

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

/**
 * Interceptor for handling API errors and logging
 */
class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val request = chain.request()
            val response = chain.proceed(request)

            if (!response.isSuccessful) {
                Timber.e("API Error ${response.code}: ${response.message}")
                logErrorResponse(response)
            }
            response
        } catch (e: IOException) {
            Timber.e(e, "Network error")
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error")
            throw e
        }
    }

    private fun logErrorResponse(response: Response) {
        try {
            val body = response.peekBody(Long.MAX_VALUE).string()
            if (body.isNotEmpty()) {
                val json = Gson().fromJson(body, JsonObject::class.java)
                val message = json.get("message")?.asString ?: "Unknown error"
                Timber.e("Error message: $message")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse error response")
        }
    }
}