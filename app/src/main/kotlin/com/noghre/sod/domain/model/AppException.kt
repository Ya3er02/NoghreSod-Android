package com.noghre.sod.domain.model

import timber.log.Timber

/**
 * Sealed class for app-wide exception handling.
 * Provides type-safe exception management across layers.
 */
sealed class AppException : Exception() {
    /**
     * Network-related exceptions.
     */
    data class NetworkError(
        override val message: String,
        val code: Int = -1,
        val cause: Throwable? = null
    ) : AppException() {
        init {
            Timber.e("NetworkError($code): $message")
        }
    }

    /**
     * Server-side exceptions.
     */
    data class ServerError(
        val code: Int,
        override val message: String,
        val errorBody: String? = null
    ) : AppException() {
        init {
            Timber.e("ServerError($code): $message")
        }
    }

    /**
     * Validation exceptions for input validation.
     */
    data class ValidationError(
        override val message: String,
        val field: String? = null
    ) : AppException() {
        init {
            Timber.e("ValidationError($field): $message")
        }
    }

    /**
     * Database-related exceptions.
     */
    data class DatabaseError(
        override val message: String,
        val cause: Throwable? = null
    ) : AppException() {
        init {
            Timber.e("DatabaseError: $message")
        }
    }

    /**
     * Authentication exceptions.
     */
    data class AuthenticationError(
        override val message: String = "Authentication failed"
    ) : AppException() {
        init {
            Timber.e("AuthenticationError: $message")
        }
    }

    /**
     * Authorization exceptions.
     */
    data class AuthorizationError(
        override val message: String = "Not authorized"
    ) : AppException() {
        init {
            Timber.e("AuthorizationError: $message")
        }
    }

    /**
     * Timeout exceptions.
     */
    data class TimeoutError(
        override val message: String = "Request timeout"
    ) : AppException() {
        init {
            Timber.e("TimeoutError: $message")
        }
    }

    /**
     * Unknown/unexpected exceptions.
     */
    data class UnknownError(
        override val message: String = "Unknown error occurred",
        val throwable: Throwable? = null
    ) : AppException() {
        init {
            Timber.e(throwable, "UnknownError: $message")
        }
    }

    /**
     * Get user-friendly error message.
     */
    fun getUserMessage(): String = when (this) {
        is NetworkError -> "شبکه در دسترس نیست"
        is ServerError -> "سرور موقتاً در دسترس نیست"
        is ValidationError -> "لطفاً ورودی‌های خود را بررسی کنید"
        is DatabaseError -> "خطای پایگاه داده"
        is AuthenticationError -> "لطفاً دوباره وارد شوید"
        is AuthorizationError -> "شما اجازه دسترسی ندارید"
        is TimeoutError -> "درخواست زمان بیشتری طول کشید"
        is UnknownError -> "خطای نامشخص رخ داد"
    }
}
