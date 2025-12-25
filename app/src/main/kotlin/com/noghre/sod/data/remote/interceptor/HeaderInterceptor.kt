package com.noghre.sod.data.remote.interceptor

import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import com.noghre.sod.BuildConfig
import java.util.*

/**
 * Header Interceptor
 * 
 * Adds common headers to all requests:
 * - Content-Type
 * - Accept
 * - User-Agent
 * - Request ID (for tracking)
 * - Accept-Language
 * - Device info
 * 
 * @since 1.0.0
 */
class HeaderInterceptor : Interceptor {
    
    companion object {
        private const val TAG = "HeaderInterceptor"
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val requestWithHeaders = originalRequest.newBuilder()
            // Standard headers
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            
            // User-Agent
            .header(
                "User-Agent",
                "NoghreSod/${BuildConfig.VERSION_NAME} " +
                "(Android ${Build.VERSION.RELEASE}; " +
                "${Build.MANUFACTURER} ${Build.MODEL})"
            )
            
            // Request ID for request tracing
            .header("X-Request-ID", generateRequestId())
            
            // Accept Language (Persian/English)
            .header("Accept-Language", getAcceptLanguage())
            
            // Device info
            .header("X-Device-ID", getDeviceId())
            .header("X-App-Version", BuildConfig.VERSION_NAME)
            .header("X-API-Version", "v1")
            
            // Security headers
            .header("X-Requested-With", "XMLHttpRequest")
            
            .build()
        
        return chain.proceed(requestWithHeaders)
    }
    
    /**
     * Generate unique request ID for tracking
     */
    private fun generateRequestId(): String {
        return UUID.randomUUID().toString()
    }
    
    /**
     * Get Accept-Language header based on device locale
     */
    private fun getAcceptLanguage(): String {
        val locale = Locale.getDefault()
        val language = locale.language
        val country = locale.country
        return if (country.isNotEmpty()) {
            "$language-$country"
        } else {
            language
        }
    }
    
    /**
     * Get device ID (should be generated once and stored)
     */
    private fun getDeviceId(): String {
        return Build.DEVICE // Placeholder - use SecurePreferences in production
    }
}
