package com.noghre.sod.core.network

import com.google.gson.Gson
import com.noghre.sod.data.remote.dto.response.ErrorResponseDto
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

/**
 * Network Error Handler
 *
 * Converts various network exceptions into standardized NetworkResult.Error
 * with appropriate ErrorType and user-friendly messages.
 *
 * Handles:
 * - Network connectivity issues
 * - SSL/Certificate errors
 * - HTTP errors (4xx, 5xx)
 * - Timeout exceptions
 * - Server validation errors
 *
 * @since 1.0.0
 */
object NetworkErrorHandler {

    private val gson = Gson()

    /**
     * Convert any exception to NetworkResult.Error
     *
     * @param exception Throwable to handle
     * @return NetworkResult.Error with appropriate type and message
     */
    fun <T> handleException(exception: Throwable): NetworkResult<T> {
        Timber.e(exception, "🚨 Network error: ${exception::class.simpleName}")

        return when (exception) {
            // Network connectivity errors
            is UnknownHostException,
            is ConnectException -> {
                NetworkResult.Error(
                    exception = exception,
                    errorType = ErrorType.NETWORK,
                    message = "لطفا اتصال اینترنت خود را بررسی کنید."
                )
            }

            // Timeout errors
            is SocketTimeoutException -> {
                NetworkResult.Error(
                    exception = exception,
                    errorType = ErrorType.TIMEOUT,
                    message = "زمان اتصال به پایان رسید. لطفا دوباره تلاش کنید."
                )
            }

            // SSL/Certificate errors
            is SSLHandshakeException -> {
                NetworkResult.Error(
                    exception = exception,
                    errorType = ErrorType.SSL_ERROR,
                    message = "خطای امنیتی در اتصال. لطفا تاریخ دستگاه را بررسی کنید."
                )
            }

            is SSLPeerUnverifiedException -> {
                NetworkResult.Error(
                    exception = exception,
                    errorType = ErrorType.SSL_ERROR,
                    message = "گواهینامه سرور معتبر نیست. لطفا با پشتیبانی تماس بگیرید."
                )
            }

            // HTTP errors
            is HttpException -> handleHttpException(exception)

            // Other IO errors
            is IOException -> {
                NetworkResult.Error(
                    exception = exception,
                    errorType = ErrorType.NETWORK,
                    message = "خطای ارتباطی با سرور."
                )
            }

            // Default
            else -> {
                NetworkResult.Error(
                    exception = exception,
                    errorType = ErrorType.UNKNOWN,
                    message = exception.message ?: "خطای نامشخص رخ داده است."
                )
            }
        }
    }

    /**
     * Handle HTTP exceptions (4xx, 5xx)
     */
    private fun handleHttpException(exception: HttpException): NetworkResult<Nothing> {
        val code = exception.code()
        val errorBody = exception.response()?.errorBody()?.string()

        // Try to parse error response
        val errorMessage = try {
            if (!errorBody.isNullOrEmpty()) {
                val errorResponse = gson.fromJson(errorBody, ErrorResponseDto::class.java)
                errorResponse.message
            } else {
                getDefaultErrorMessage(code)
            }
        } catch (e: Exception) {
            Timber.d("Could not parse error body: ${e.message}")
            getDefaultErrorMessage(code)
        }

        // Determine error type based on HTTP code
        val errorType = when (code) {
            401 -> ErrorType.UNAUTHORIZED
            403 -> ErrorType.FORBIDDEN
            404 -> ErrorType.NOT_FOUND
            422 -> ErrorType.VALIDATION_ERROR
            in 500..599 -> ErrorType.SERVER_ERROR
            else -> ErrorType.UNKNOWN
        }

        return NetworkResult.Error(
            exception = exception,
            errorType = errorType,
            code = code,
            message = errorMessage
        )
    }

    /**
     * Get user-friendly error message based on HTTP code
     */
    private fun getDefaultErrorMessage(code: Int): String {
        return when (code) {
            // Client errors
            400 -> "درخواست نامعتبر است."
            401 -> "لطفا مجددا وارد حساب کاربری شوید."
            403 -> "شما دسترسی به این بخش را ندارید."
            404 -> "محتوای مورد نظر یافت نشد."
            422 -> "اطلاعات وارد شده معتبر نیست."
            429 -> "تعداد درخواست‌ها بیش از حد مجاز است. لطفا کمی صبر کنید."

            // Server errors
            500 -> "خطای داخلی سرور. لطفا بعداً تلاش کنید."
            502 -> "خطای درگاه یا پاسخ نامعتبر از سرور."
            503 -> "سرویس در حال حاضر در دسترس نیست."
            504 -> "پاسخ از سمت سرور دریافت نشد."
            in 500..599 -> "خطای سرور ($code)."

            // Unknown
            else -> "خطای غیرمنتظره ($code)."
        }
    }
}
