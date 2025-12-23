package com.noghre.sod.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Network interceptor for logging request/response details
 * Logs timing, headers, and data size information
 */
class NetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestUrl = request.url
        val method = request.method

        Timber.d("→ Request: $method $requestUrl")

        val startTime = System.nanoTime()
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            Timber.e(e, "← Request failed: $method $requestUrl")
            throw e
        }

        val duration = (System.nanoTime() - startTime) / 1_000_000.0
        val responseSize = response.body?.contentLength() ?: 0

        Timber.d("← Response: ${response.code} (${duration.toLong()}ms) [${formatSize(responseSize)}]")

        return response
    }

    private fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
}