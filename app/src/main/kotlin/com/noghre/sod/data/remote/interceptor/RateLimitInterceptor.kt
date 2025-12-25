package com.noghre.sod.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

/**
 * Rate Limit Interceptor
 * 
 * Handles rate limiting from server responses:
 * - Parses X-RateLimit-* headers
 * - Warns when approaching limits
 * - Logs 429 Too Many Requests errors
 * 
 * @since 1.0.0
 */
class RateLimitInterceptor : Interceptor {
    
    private val requestCounts = ConcurrentHashMap<String, RateLimitInfo>()
    
    data class RateLimitInfo(
        val remaining: Int,
        val limit: Int,
        val resetTime: Long
    )
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        handleRateLimitHeaders(response)
        return response
    }
    
    private fun handleRateLimitHeaders(response: Response) {
        val remaining = response.header("X-RateLimit-Remaining")?.toIntOrNull()
        val limit = response.header("X-RateLimit-Limit")?.toIntOrNull()
        val reset = response.header("X-RateLimit-Reset")?.toLongOrNull()
        
        if (remaining != null && limit != null && reset != null) {
            val rateLimitInfo = RateLimitInfo(
                remaining = remaining,
                limit = limit,
                resetTime = reset * 1000
            )
            
            val endpoint = response.request.url.encodedPath
            requestCounts[endpoint] = rateLimitInfo
            
            // Warn if approaching limit (less than 10% remaining)
            if (remaining < limit * 0.1) {
                Timber.w("⚠️ Rate limit warning for $endpoint: $remaining/$limit remaining")
            }
        }
        
        // Handle 429 Too Many Requests
        if (response.code == 429) {
            val retryAfter = response.header("Retry-After")?.toLongOrNull()
            Timber.e("❌ Rate limit exceeded! Retry after: ${retryAfter ?: 60}s")
        }
    }
    
    /**
     * Check if we can make a request to the endpoint
     */
    suspend fun canMakeRequest(endpoint: String): Boolean {
        val info = requestCounts[endpoint] ?: return true
        
        // If reset time has passed, remove from tracking
        if (System.currentTimeMillis() > info.resetTime) {
            requestCounts.remove(endpoint)
            return true
        }
        
        return info.remaining > 0
    }
}
