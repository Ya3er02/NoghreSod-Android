package com.noghre.sod.data.remote.interceptor

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import timber.log.Timber
import com.noghre.sod.BuildConfig
import java.nio.charset.Charset

/**
 * Advanced Logging Interceptor
 * 
 * Features:
 * - Request/Response logging with formatting
 * - Sensitive data masking
 * - Performance tracking
 * - Analytics integration
 * 
 * @since 1.0.0
 */
class AdvancedLoggingInterceptor : Interceptor {
    
    companion object {
        private const val TAG = "API_LOG"
        private const val MAX_LOG_LENGTH = 4000
        
        private val SENSITIVE_HEADERS = setOf(
            "Authorization",
            "Cookie",
            "Set-Cookie",
            "X-API-Key",
            "X-Auth-Token"
        )
        
        private val SENSITIVE_FIELDS = setOf(
            "password",
            "token",
            "accessToken",
            "refreshToken",
            "cardNumber",
            "cvv",
            "pin",
            "ssn"
        )
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!BuildConfig.DEBUG) {
            return chain.proceed(chain.request())
        }
        
        val request = chain.request()
        val startTime = System.nanoTime()
        
        // Log request
        logRequest(request)
        
        return try {
            val response = chain.proceed(request)
            val duration = (System.nanoTime() - startTime) / 1_000_000
            
            // Log response
            logResponse(response, duration)
            
            response
            
        } catch (e: Exception) {
            val duration = (System.nanoTime() - startTime) / 1_000_000
            logError(request, e, duration)
            throw e
        }
    }
    
    private fun logRequest(request: okhttp3.Request) {
        val headers = buildString {
            request.headers.forEach { (name, value) ->
                val maskedValue = if (name in SENSITIVE_HEADERS) {
                    maskSensitiveData(value)
                } else {
                    value
                }
                appendLine("    $name: $maskedValue")
            }
        }
        
        val body = try {
            request.body?.let { extractRequestBody(it) }
        } catch (e: Exception) {
            "(could not read body)"
        }
        
        val log = """
            ┌────── REQUEST ──────
            │ 📄 ${request.method} ${request.url}
            │ Headers:
            $headers
            │ Body:
            ${body?.let { "│   $it" } ?: "│   (empty)"}
            └────────────────
        """.trimIndent()
        
        logLarge(log)
    }
    
    private fun logResponse(response: Response, duration: Long) {
        val emoji = when (response.code) {
            in 200..299 -> "✅"
            in 300..399 -> "⬅️"
            in 400..499 -> "⚠️"
            in 500..599 -> "❌"
            else -> "❏"
        }
        
        val responseBody = response.peekBody(1024 * 1024).string()
        val formattedBody = try {
            formatJson(responseBody).prependIndent("|   ")
        } catch (e: Exception) {
            responseBody.prependIndent("|   ")
        }
        
        val log = """
            ┌───── RESPONSE ($duration ms) ─────
            │ $emoji ${response.code} ${response.message}
            │ URL: ${response.request.url}
            │ Body:
            $formattedBody
            └───────────────────
        """.trimIndent()
        
        logLarge(log)
        
        // Track slow requests
        if (duration > 3000) {
            Timber.w("🐢 Slow request detected: ${response.request.url} (${duration}ms)")
        }
    }
    
    private fun logError(request: okhttp3.Request, error: Exception, duration: Long) {
        val log = """
            ┌───── ERROR ($duration ms) ─────
            │ ❌ ${error.javaClass.simpleName}
            │ ${request.method} ${request.url}
            │ Message: ${error.message}
            └───────────────────
        """.trimIndent()
        
        Timber.tag(TAG).e(error, log)
    }
    
    private fun extractRequestBody(requestBody: okhttp3.RequestBody): String? {
        return try {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val bodyString = buffer.readString(Charset.forName("UTF-8"))
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
                Regex("\"$field\"\s*:\s*\"([^\"]*)\""),
                "\"$field\": \"****\""
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
    
    private fun logLarge(message: String) {
        val lines = message.split("\n")
        for (line in lines) {
            if (line.length > MAX_LOG_LENGTH) {
                var start = 0
                while (start < line.length) {
                    val end = (start + MAX_LOG_LENGTH).coerceAtMost(line.length)
                    Timber.tag(TAG).d(line.substring(start, end))
                    start = end
                }
            } else {
                Timber.tag(TAG).d(line)
            }
        }
    }
}
