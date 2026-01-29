package com.noghre.sod.core.network

import com.noghre.sod.core.config.AppConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import timber.log.Timber
import java.nio.charset.Charset
import javax.inject.Inject

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
 * Respects [AppConfig.Logging.ENABLE_NETWORK_LOGGING] flag.
 */
class LoggingInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!AppConfig.Logging.ENABLE_NETWORK_LOGGING) {
            return chain.proceed(chain.request())
        }

        val originalRequest = chain.request()
        val startTime = System.nanoTime()

        // Log request (redacted)
        logRequest(originalRequest)

        val response: Response
        try {
            response = chain.proceed(originalRequest)
        } catch (e: Exception) {
            val elapsedTime = (System.nanoTime() - startTime) / 1_000_000
            Timber.tag("HTTP_ERR").e("Request failed after ${elapsedTime}ms: ${e.message}")
            throw e
        }

        val elapsedTime = (System.nanoTime() - startTime) / 1_000_000

        // Log response (redacted)
        return logResponse(response, elapsedTime)
    }

    private fun logRequest(request: Request) {
        val method = request.method
        val url = request.url
        val headers = redactHeaders(request.headers.toMultimap())

        val headerString = headers.entries.joinToString("\n") { (name, values) ->
            "  $name: ${values.joinToString("; ")}"
        }

        val requestBody = getRequestBody(request)

        Timber.tag("HTTP_REQ").d(
            "→ $method $url\n" +
                    "Headers:\n$headerString\n" +
                    "Body: $requestBody"
        )
    }

    private fun logResponse(response: Response, elapsedTimeMs: Long): Response {
        val code = response.code
        val message = response.message
        val url = response.request.url

        val responseBodyString = getResponseBody(response)
        val redactedBody = redactBodySensitiveData(responseBodyString ?: "")

        Timber.tag("HTTP_RES").d(
            "← $code $message ($url) - ${elapsedTimeMs}ms\n" +
                    "Body: $redactedBody"
        )

        // Re-create response because body was consumed
        return if (responseBodyString != null) {
            response.newBuilder()
                .body(responseBodyString.toResponseBody(response.body?.contentType()))
                .build()
        } else {
            response
        }
    }

    private fun getRequestBody(request: Request): String {
        try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body?.writeTo(buffer)
            val content = buffer.readString(Charset.forName("UTF-8"))
            return redactBodySensitiveData(content)
        } catch (e: Exception) {
            return "[Error reading request body]"
        }
    }

    private fun getResponseBody(response: Response): String? {
        try {
            val responseBody = response.body
            val source = responseBody?.source()
            source?.request(Long.MAX_VALUE) // Buffer the entire body
            val buffer = source?.buffer
            return buffer?.clone()?.readString(Charset.forName("UTF-8"))
        } catch (e: Exception) {
            return "[Error reading response body]"
        }
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
        if (body.isEmpty()) return ""
        
        return body
            // Redact Authorization Bearer tokens
            .replace(
                Regex("(?i)(Authorization:\\\\s*Bearer\\\\s+)[\\\\w\\\\-\\\\.]+", RegexOption.MULTILINE),
                "$1[REDACTED]"
            )
            // Redact JWT tokens (starts with eyJ)
            .replace(
                Regex("eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+"),
                "[JWT_REDACTED]"
            )
            // Redact card numbers (4111 1111 1111 1111 or 4111111111111111)
            .replace(
                Regex("\\\"?cardNumber\\\"?\\s*[=:]\\s*\\\"?[0-9\\s-]{13,19}\\\"?"),
                "cardNumber: \"[CARD_REDACTED]\""
            )
            // Redact CVV/CVC codes
            .replace(
                Regex("(?i)(\\\"?cvv\\\"?|\\\"?cvc\\\"?|\\\"?cve\\\"?)\\s*[=:]\\s*\\\"?[0-9]{3,4}\\\"?"),
                "cvv: \"[REDACTED]\""
            )
            // Redact passwords
            .replace(
                Regex("(?i)(\\\"?password\\\"?)\\s*[=:]\\s*\\\"?[^\\\"\\s,}]*\\\"?"),
                "password: \"[REDACTED]\""
            )
            // Redact Zarinpal merchant IDs
            .replace(
                Regex("(?i)(merchant_id|merchantid)\\s*[=:]\\s*\\\"?[a-z0-9]+\\\"?"),
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
                Regex("(?i)(Authority|authority)\\s*[=:]\\s*\\\"?[A-Z0-9]{30,}\\\"?"),
                "Authority: \"[AUTHORITY_REDACTED]\""
            )
    }
}
