package com.noghre.sod.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import android.os.Build

/**
 * Header Interceptor
 * Adds default headers to all requests:
 * - Content-Type: application/json
 * - Accept-Language: fa-IR (Persian)
 * - User-Agent: NoghreSod/1.0
 */
class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val request = original.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Accept-Language", "fa-IR,fa;q=0.9,en;q=0.8")
            .header("User-Agent", buildUserAgent())
            .header("X-Client-Version", "1.0.0")
            .header("X-Platform", "android")
            .header("X-Device-Model", "${Build.MANUFACTURER} ${Build.MODEL}")
            .build()

        return chain.proceed(request)
    }

    private fun buildUserAgent(): String {
        return "NoghreSod/1.0 (Android ${Build.VERSION.RELEASE}; ${Build.MODEL})"
    }
}
