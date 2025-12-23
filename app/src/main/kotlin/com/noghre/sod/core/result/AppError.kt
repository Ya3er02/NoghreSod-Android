package com.noghre.sod.core.result

import android.util.Log
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Sealed class representing different error types in the application.
 * Each error type has a user-friendly Persian message.
 */
sealed class AppError : Exception() {
    abstract val userMessage: String
    abstract val code: String

    /**
     * Network error when device has no internet connection.
     */
    data class NetworkError(
        val exception: IOException,
        override val userMessage: String = "خطا در اتصال به اینترنت. لطفا اتصال خود را بررسی کنید.",
        override val code: String = "NETWORK_ERROR"
    ) : AppError()

    /**
     * Timeout error when server doesn't respond in time.
     */
    data class TimeoutError(
        val exception: SocketTimeoutException,
        override val userMessage: String = "مهلت درخواست به پایان رسید. لطفا دوباره تلاش کنید.",
        override val code: String = "TIMEOUT_ERROR"
    ) : AppError()

    /**
     * Server error for 4xx and 5xx status codes.
     */
    data class ServerError(
        val statusCode: Int,
        val serverMessage: String?,
        override val code: String,
        override val userMessage: String
    ) : AppError() {
        companion object {
            fun from(exception: HttpException): ServerError {
                val statusCode = exception.code()
                val serverMessage = exception.response()?.errorBody()?.string()

                return when (statusCode) {
                    400 -> ServerError(
                        statusCode,
                        serverMessage,
                        "BAD_REQUEST",
                        "درخواست نامعتبر. لطفا اطلاعات خود را بررسی کنید."
                    )
                    401 -> ServerError(
                        statusCode,
                        serverMessage,
                        "UNAUTHORIZED",
                        "شناسایی اعتباری ناموفق. لطفا مجددا وارد شوید."
                    )
                    403 -> ServerError(
                        statusCode,
                        serverMessage,
                        "FORBIDDEN",
                        "شما اجازه دسترسی به این منبع را ندارید."
                    )
                    404 -> ServerError(
                        statusCode,
                        serverMessage,
                        "NOT_FOUND",
                        "منبع درخواستی یافت نشد."
                    )
                    409 -> ServerError(
                        statusCode,
                        serverMessage,
                        "CONFLICT",
                        "یک تعارض رخ داد. لطفا دوباره تلاش کنید."
                    )
                    422 -> ServerError(
                        statusCode,
                        serverMessage,
                        "UNPROCESSABLE_ENTITY",
                        "اطلاعات ارسال‌شده قابل‌پردازش نیستند."
                    )
                    500 -> ServerError(
                        statusCode,
                        serverMessage,
                        "INTERNAL_SERVER_ERROR",
                        "خطای سرور. لطفا بعدا دوباره تلاش کنید."
                    )
                    502, 503, 504 -> ServerError(
                        statusCode,
                        serverMessage,
                        "SERVICE_UNAVAILABLE",
                        "سرویس موقتا در دسترس نیست. لطفا بعدا دوباره تلاش کنید."
                    )
                    else -> ServerError(
                        statusCode,
                        serverMessage,
                        "UNKNOWN_HTTP_ERROR",
                        "خطای سرور (${statusCode}). لطفا بعدا دوباره تلاش کنید."
                    )
                }
            }
        }
    }

    /**
     * Authentication error for auth-related failures.
     */
    data class AuthError(
        val reason: String,
        override val userMessage: String = "خطای احراز هویت. لطفا مجددا وارد شوید.",
        override val code: String = "AUTH_ERROR"
    ) : AppError()

    /**
     * Token refresh error.
     */
    data class TokenRefreshError(
        val cause: Exception,
        override val userMessage: String = "ناموفق در تازه‌کردن اعتبار. لطفا مجددا وارد شوید.",
        override val code: String = "TOKEN_REFRESH_ERROR"
    ) : AppError()

    /**
     * Validation error for input validation failures.
     */
    data class ValidationError(
        val fieldErrors: Map<String, String>,
        override val code: String = "VALIDATION_ERROR",
        override val userMessage: String = buildValidationMessage(fieldErrors)
    ) : AppError() {
        companion object {
            private fun buildValidationMessage(errors: Map<String, String>): String {
                return if (errors.isEmpty()) {
                    "خطای اعتبارسنجی"
                } else {
                    "خطاهای اعتبارسنجی:\n" + errors.values.joinToString("\n")
                }
            }
        }
    }

    /**
     * Local database error.
     */
    data class DatabaseError(
        val exception: Exception,
        override val userMessage: String = "خطای پایگاه داده. لطفا دوباره تلاش کنید.",
        override val code: String = "DATABASE_ERROR"
    ) : AppError()

    /**
     * Unknown error for unexpected exceptions.
     */
    data class UnknownError(
        val exception: Throwable,
        override val userMessage: String = "خطای نامشخص رخ داد. لطفا دوباره تلاش کنید.",
        override val code: String = "UNKNOWN_ERROR"
    ) : AppError()
}

/**
 * Extension function to convert Throwable to AppError.
 */
fun Throwable.toAppError(): AppError {
    return when (this) {
        is AppError -> this
        is HttpException -> AppError.ServerError.from(this)
        is ConnectException -> AppError.NetworkError(this as IOException)
        is UnknownHostException -> AppError.NetworkError(this as IOException)
        is SocketTimeoutException -> AppError.TimeoutError(this)
        is IOException -> AppError.NetworkError(this)
        else -> {
            Log.e("AppError", "Unexpected error: ", this)
            AppError.UnknownError(this)
        }
    }
}
