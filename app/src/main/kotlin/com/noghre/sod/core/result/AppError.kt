package com.noghre.sod.core.result

import java.io.IOException

/**
 * Sealed class representing different types of errors that can occur in the application.
 * Each error type includes Persian localized error messages.
 *
 * This class provides a single point for error handling and localization throughout the app.
 */
sealed class AppError : Throwable() {
    /**
     * Network-related errors.
     */
    sealed class NetworkError : AppError() {
        object NoConnection : NetworkError() {
            override fun getMessage(): String = "اتصال اینترنت برقرار نیست"
            override fun toString(): String = "NoConnection: ${getMessage()}"
        }

        object Timeout : NetworkError() {
            override fun getMessage(): String = "درخواست به مدت زمان مشخصی پاسخ نداد. لطفاً دوباره سعی کنید"
            override fun toString(): String = "Timeout: ${getMessage()}"
        }

        object SSLError : NetworkError() {
            override fun getMessage(): String = "خطا در برقراری اتصال ایمن. لطفاً بعداً سعی کنید"
            override fun toString(): String = "SSLError: ${getMessage()}"
        }

        data class DNSError(val cause: String = "") : NetworkError() {
            override fun getMessage(): String = "نام سرور پیدا نشد. لطفاً اتصال اینترنت خود را بررسی کنید"
            override fun toString(): String = "DNSError: ${getMessage()}"
        }
    }

    /**
     * Server-related errors (4xx, 5xx responses).
     */
    sealed class ServerError : AppError() {
        data class BadRequest(
            val code: Int = 400,
            val message: String = "درخواست نامعتبر است"
        ) : ServerError() {
            override fun getMessage(): String = message
            override fun toString(): String = "BadRequest($code): $message"
        }

        data class Unauthorized(
            val code: Int = 401,
            val message: String = "شما وارد نشده‌اید. لطفاً وارد شوید"
        ) : ServerError() {
            override fun getMessage(): String = message
            override fun toString(): String = "Unauthorized($code): $message"
        }

        data class Forbidden(
            val code: Int = 403,
            val message: String = "شما اجازه دسترسی به این منابع را ندارید"
        ) : ServerError() {
            override fun getMessage(): String = message
            override fun toString(): String = "Forbidden($code): $message"
        }

        data class NotFound(
            val code: Int = 404,
            val message: String = "منبع درخواستی پیدا نشد"
        ) : ServerError() {
            override fun getMessage(): String = message
            override fun toString(): String = "NotFound($code): $message"
        }

        data class Conflict(
            val code: Int = 409,
            val message: String = "این آیتم قبلاً وجود دارد"
        ) : ServerError() {
            override fun getMessage(): String = message
            override fun toString(): String = "Conflict($code): $message"
        }

        data class ValidationError(
            val code: Int = 422,
            val message: String = "داده‌های ارسالی معتبر نیستند",
            val fieldErrors: Map<String, String> = emptyMap()
        ) : ServerError() {
            override fun getMessage(): String = message
            override fun toString(): String = "ValidationError($code): $message - Fields: $fieldErrors"
        }

        data class InternalServerError(
            val code: Int = 500,
            val message: String = "خطای سرور. لطفاً بعداً سعی کنید"
        ) : ServerError() {
            override fun getMessage(): String = message
            override fun toString(): String = "InternalServerError($code): $message"
        }

        data class ServiceUnavailable(
            val code: Int = 503,
            val message: String = "سرویس موقتاً در دسترس نیست. لطفاً بعداً سعی کنید"
        ) : ServerError() {
            override fun getMessage(): String = message
            override fun toString(): String = "ServiceUnavailable($code): $message"
        }

        data class UnknownServerError(
            val code: Int,
            val message: String = "خطای سرور نامعلوم"
        ) : ServerError() {
            override fun getMessage(): String = message
            override fun toString(): String = "UnknownServerError($code): $message"
        }
    }

    /**
     * Authentication-related errors.
     */
    sealed class AuthError : AppError() {
        object TokenExpired : AuthError() {
            override fun getMessage(): String = "نشست شما منقضی شده است. لطفاً دوباره وارد شوید"
            override fun toString(): String = "TokenExpired: ${getMessage()}"
        }

        object InvalidCredentials : AuthError() {
            override fun getMessage(): String = "نام کاربری یا رمز عبور نادرست است"
            override fun toString(): String = "InvalidCredentials: ${getMessage()}"
        }

        object RefreshFailed : AuthError() {
            override fun getMessage(): String = "نمی‌توان نشست شما را بازیابی کنید. لطفاً دوباره وارد شوید"
            override fun toString(): String = "RefreshFailed: ${getMessage()}"
        }

        data class Generic(val message: String) : AuthError() {
            override fun getMessage(): String = message
            override fun toString(): String = "AuthError: $message"
        }
    }

    /**
     * Validation errors with field information.
     */
    data class ValidationError(
        val fields: Map<String, String> = emptyMap(),
        val message: String = "اطلاعات ارسالی معتبر نیستند"
    ) : AppError() {
        override fun getMessage(): String = message
        override fun toString(): String = "ValidationError: $message - Fields: $fields"
    }

    /**
     * Database-related errors.
     */
    data class DatabaseError(
        val message: String = "خطای پایگاه داده رخ داد",
        val cause: Throwable? = null
    ) : AppError() {
        override fun getMessage(): String = message
        override fun toString(): String = "DatabaseError: $message"
    }

    /**
     * Parse/Serialization errors.
     */
    data class ParseError(
        val message: String = "خطا در پردازش داده‌ها",
        val cause: Throwable? = null
    ) : AppError() {
        override fun getMessage(): String = message
        override fun toString(): String = "ParseError: $message"
    }

    /**
     * Cache-related errors.
     */
    data class CacheError(
        val message: String = "خطا در دسترسی به کش",
        val cause: Throwable? = null
    ) : AppError() {
        override fun getMessage(): String = message
        override fun toString(): String = "CacheError: $message"
    }

    /**
     * File-related errors.
     */
    data class FileError(
        val message: String = "خطا در دسترسی به فایل",
        val cause: Throwable? = null
    ) : AppError() {
        override fun getMessage(): String = message
        override fun toString(): String = "FileError: $message"
    }

    /**
     * Permission-related errors.
     */
    data class PermissionError(
        val message: String = "دسترسی رد شد",
        val cause: Throwable? = null
    ) : AppError() {
        override fun getMessage(): String = message
        override fun toString(): String = "PermissionError: $message"
    }

    /**
     * Unknown/Unexpected errors.
     */
    data class UnknownError(
        val message: String = "خطای نامعلوم رخ داد",
        val cause: Throwable? = null
    ) : AppError() {
        override fun getMessage(): String = message
        override fun toString(): String = "UnknownError: $message"
    }

    /**
     * Returns a user-friendly message in Persian.
     */
    abstract override fun getMessage(): String
}

/**
 * Extension function to convert a Throwable to AppError.
 */
fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    is IOException -> when {
        this.message?.contains("timeout", ignoreCase = true) == true ->
            AppError.NetworkError.Timeout
        this.message?.contains("unable to resolve", ignoreCase = true) == true ->
            AppError.NetworkError.DNSError(this.message ?: "")
        else -> AppError.NetworkError.NoConnection
    }
    else -> AppError.UnknownError(
        message = this.message ?: "خطای نامعلوم رخ داد",
        cause = this
    )
}

/**
 * Extension function to get a user-friendly error message.
 */
fun AppError.toUserMessage(): String = this.getMessage()
