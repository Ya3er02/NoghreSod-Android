package com.noghre.sod.core.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber

/**
 * HTTP logging interceptor that redacts sensitive information.
 * 
 * Redacts:
 * - Authorization tokens (Bearer eyJ...)
 * - Payment card numbers
 * - CVV codes
 * - Passwords and PII
 * - API keys
 * 
 * Logs to Timber instead of raw Logcat to avoid exposure in bug reports.
 */
class SensitiveDataRedactingInterceptor : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)
        
        // Log request (redacted)
        logRequest(originalRequest)
        
        // Log response (redacted)
        logResponse(response)
        
        return response
    }
    
    private fun logRequest(request: Request) {
        val method = request.method
        val url = request.url
        val headers = redactHeaders(request.headers.toMultimap())
        
        val headerString = headers.entries.joinToString(", ") { (name, values) ->
            "$name: ${values.joinToString("; ")}"
        }
        
        Timber.tag("HTTP_REQUEST").d(
            "→ $method $url\nHeaders: $headerString"
        )
    }
    
    private fun logResponse(response: Response) {
        val code = response.code
        val message = response.message
        val url = response.request.url
        
        Timber.tag("HTTP_RESPONSE").d(
            "← $code $message ($url)"
        )
    }
    
    /**
     * Redact sensitive header values.
     */
    private fun redactHeaders(headers: Map<String, List<String>>): Map<String, List<String>> {
        val sensitive = setOf(
            "authorization",
            "cookie",
            "set-cookie",
            "x-auth-token",
            "x-api-key",
            "x-secret-key"
        )
        
        return headers.mapValues { (name, values) ->
            if (name.lowercase() in sensitive) {
                listOf("[REDACTED]")
            } else {
                values
            }
        }
    }
    
    /**
     * Redact sensitive patterns in request/response body.
     */
    fun redactBodySensitiveData(body: String): String {
        return body
            // Redact Authorization Bearer tokens
            .replace(
                Regex("(?i)(Authorization:\\s*Bearer\\s+)[\\w\\-\\.]+"),
                "$1[REDACTED]"
            )
            // Redact JWT tokens (starts with eyJ)
            .replace(
                Regex("eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+"),
                "[JWT_REDACTED]"
            )
            // Redact card numbers (4111 1111 1111 1111 or 4111111111111111)
            .replace(
                Regex("\"?cardNumber\"?\\s*[=:]{1}\\s*\"?[0-9\\s-]{13,19}\"?"),
                "cardNumber: \"[CARD_REDACTED]\""
            )
            // Redact CVV/CVC codes
            .replace(
                Regex("(?i)(\"?cvv\"?|\"?cvc\"?|\"?cve\"?)\\s*[=:]{1}\\s*\"?[0-9]{3,4}\"?"),
                "cvv: \"[REDACTED]\""
            )
            // Redact passwords
            .replace(
                Regex("(?i)(\"?password\"?)\\s*[=:]{1}\\s*\"?[^\"\\s,}]*\"?"),
                "password: \"[REDACTED]\""
            )
            // Redact Zarinpal merchant IDs
            .replace(
                Regex("(?i)(merchant_id|merchantid)\\s*[=:]{1}\\s*\"?[a-z0-9]+\"?"),
                "merchant_id: \"[REDACTED]\""
            )
            // Redact phone numbers (Iranian format)
            .replace(
                Regex("0?9[0-9]{9}|\\+98[0-9]{10}"),
                "[PHONE_REDACTED]"
            )
            // Redact email addresses
            .replace(
                Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"),
                "[EMAIL_REDACTED]"
            )
            // Redact Zarinpal Authority codes
            .replace(
                Regex("(?i)(Authority|authority)\\s*[=:]{1}\\s*\"?[A-Z0-9]{30,}\"?"),
                "Authority: \"[AUTHORITY_REDACTED]\""
            )
    }
}