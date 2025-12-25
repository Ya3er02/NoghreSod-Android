package com.noghresod.network.interceptor

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import okhttp3.*
import okhttp3.internal.http.promisesBody
import okio.Buffer
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.math.pow

// ==================== Advanced Logging Interceptor ====================

class AdvancedLoggingInterceptor : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.nanoTime()
        
        logRequest(request)
        
        return try {
            val response = chain.proceed(request)
            val endTime = System.nanoTime()
            val duration = (endTime - startTime) / 1_000_000 // ms
            
            logResponse(response, duration)
            response
            
        } catch (e: Exception) {
            val endTime = System.nanoTime()
            val duration = (endTime - startTime) / 1_000_000
            
            logError(request, e, duration)
            throw e
        }
    }
    
    private fun logRequest(request: Request) {
        if (!com.noghresod.BuildConfig.DEBUG) return
        
        val headers = buildString {
            request.headers.forEach { (name, value) ->
                val maskedValue = if (name in SENSITIVE_HEADERS) {
                    maskSensitiveData(value)
                } else {
                    value
                }
                appendLine("  $name: $maskedValue")
            }
        }
        
        val body = request.body?.let { extractRequestBody(it) }
        
        Timber.tag("API_REQUEST").d("""
            ┌────── Request ──────
            │ ${request.method} ${request.url}
            │ Headers:
            $headers
            │ Body: ${body?.let { "\n  $it" } ?: "(empty)"}
            └────────────────────
        """.trimIndent())
    }
    
    private fun logResponse(response: Response, duration: Long) {
        if (!com.noghresod.BuildConfig.DEBUG) return
        
        val emoji = when (response.code) {
            in 200..299 -> "✅"
            in 300..399 -> "↪️"
            in 400..499 -> "⚠️"
            in 500..599 -> "❌"
            else -> "❓"
        }
        
        val responseBody = response.peekBody(1024 * 1024).string()
        
        Timber.tag("API_RESPONSE").d("""
            ┌────── Response ($duration ms) ──────
            │ $emoji ${response.code} ${response.message}
            │ URL: ${response.request.url}
            │ Body:\n${formatJson(responseBody).prependIndent("  ")}
            └────────────────────
        """.trimIndent())
    }
    
    private fun logError(request: Request, error: Exception, duration: Long) {
        Timber.tag("API_ERROR").e(error, """
            ┌────── Error ($duration ms) ──────
            │ ❌ ${error.javaClass.simpleName}
            │ ${request.method} ${request.url}
            │ Message: ${error.message}
            └────────────────────
        """.trimIndent())
    }
    
    private fun extractRequestBody(requestBody: RequestBody): String? {
        return try {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val bodyString = buffer.readUtf8()
            maskSensitiveFields(bodyString)
        } catch (e: Exception) {
            "(could not read body)"
        }
    }
    
    private fun maskSensitiveData(data: String): String {
        return if (data.length > 8) {
            "${data.take(4)}...${data.takeLast(4)}"
        } else {
            "****"
        }
    }
    
    private fun maskSensitiveFields(json: String): String {
        var masked = json
        SENSITIVE_FIELDS.forEach { field ->
            masked = masked.replace(
                Regex(""""$field"\s*:\s*"([^"]*)""""),
                """"$field": "****""""
            )
        }
        return masked
    }
    
    private fun formatJson(json: String): String {
        return try {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonElement = gson.fromJson(json, JsonElement::class.java)
            gson.toJson(jsonElement)
        } catch (e: Exception) {
            json
        }
    }
    
    companion object {
        private val SENSITIVE_HEADERS = setOf(
            "Authorization",
            "Cookie",
            "Set-Cookie",
            "X-API-Key"
        )
        
        private val SENSITIVE_FIELDS = setOf(
            "password",
            "token",
            "accessToken",
            "refreshToken",
            "cardNumber",
            "cvv",
            "pin"
        )
    }
}

// ==================== Rate Limit Interceptor ====================

data class RateLimitInfo(
    val remaining: Int,
    val limit: Int,
    val resetTime: Long
)

class RateLimitInterceptor : Interceptor {
    
    private val requestCounts = ConcurrentHashMap<String, RateLimitInfo>()
    
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
            
            if (remaining < limit * 0.1) {
                Timber.w("⚠️ Rate limit warning for $endpoint: $remaining/$limit remaining")
            }
        }
    }
}

// ==================== Retry Interceptor ====================

class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val initialDelay: Long = 1000 // 1 second
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var lastException: IOException? = null
        
        for (attempt in 0 until maxRetries) {
            try {
                response = chain.proceed(request)
                
                // Retry on 5xx or specific 4xx errors
                if (response.code in 500..599 || response.code in RETRYABLE_4xx_CODES) {
                    val delay = calculateBackoffDelay(attempt)
                    Timber.d("Retrying request (attempt ${attempt + 1}/$maxRetries) after ${delay}ms")
                    Thread.sleep(delay)
                    response.close()
                    continue
                }
                
                return response
                
            } catch (e: IOException) {
                lastException = e
                if (attempt < maxRetries - 1) {
                    val delay = calculateBackoffDelay(attempt)
                    Timber.w(e, "Request failed, retrying after ${delay}ms")
                    Thread.sleep(delay)
                }
            }
        }
        
        // Return last response or throw exception
        return response ?: throw lastException ?: IOException("Request failed after $maxRetries attempts")
    }
    
    private fun calculateBackoffDelay(attempt: Int): Long {
        // Exponential backoff: 1s, 2s, 4s, ...
        return initialDelay * 2.0.pow(attempt).toLong()
    }
    
    companion object {
        private val RETRYABLE_4xx_CODES = setOf(
            408, // Request Timeout
            429  // Too Many Requests
        )
    }
}

// ==================== Token Refresh Interceptor ====================

interface TokenProvider {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun updateTokens(accessToken: String, refreshToken: String)
}

class TokenRefreshInterceptor(
    private val tokenProvider: TokenProvider,
    private val apiService: okhttp3.Call.Factory // Retrofit instance
) : Interceptor {
    
    private var isRefreshing = false
    private val refreshLock = Object()
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Add auth token if available
        val token = tokenProvider.getAccessToken()
        val requestWithAuth = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        val response = chain.proceed(requestWithAuth)
        
        // If unauthorized, try to refresh token
        if (response.code == 401) {
            return synchronized(refreshLock) {
                if (isRefreshing) {
                    // Wait for refresh to complete
                    return@synchronized chain.proceed(requestWithAuth)
                }
                
                isRefreshing = true
                try {
                    val refreshToken = tokenProvider.getRefreshToken()
                    if (refreshToken != null) {
                        // Attempt token refresh
                        val newTokens = refreshAccessToken(refreshToken)
                        if (newTokens != null) {
                            tokenProvider.updateTokens(newTokens.first, newTokens.second)
                            
                            // Retry original request with new token
                            val newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer ${newTokens.first}")
                                .build()
                            
                            response.close()
                            return@synchronized chain.proceed(newRequest)
                        }
                    }
                } finally {
                    isRefreshing = false
                }
                
                response
            }
        }
        
        return response
    }
    
    private fun refreshAccessToken(refreshToken: String): Pair<String, String>? {
        return try {
            // Implementation depends on your actual API
            // This is a placeholder
            Timber.d("Attempting to refresh token...")
            null
        } catch (e: Exception) {
            Timber.e(e, "Token refresh failed")
            null
        }
    }
}
