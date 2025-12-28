package com.noghre.sod.core.error

import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

/**
 * 🌐 Global Exception Handler for Coroutines
 * 
 * Provides centralized error logging and classification.
 * Handles all uncaught exceptions in application coroutines.
 * 
 * Usage in ViewModel:
 * ```kotlin
 * @Inject
 * lateinit var exceptionHandler: GlobalExceptionHandler
 * 
 * private val scope = viewModelScope + exceptionHandler.handler
 * ```
 */
@Singleton
class GlobalExceptionHandler @Inject constructor() {
    
    /**
     * CoroutineExceptionHandler that logs all uncaught exceptions
     */
    val handler: CoroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Timber.e(throwable, "[COROUTINE_ERROR] Uncaught exception in context: ${context[kotlinx.coroutines.CoroutineName]}")
        // TODO: Send to crash reporting service (Firebase Crashlytics)
    }
    
    /**
     * Classifies throwable into application-specific error types
     * @param throwable The exception to classify
     * @return Classified AppError with user-friendly message
     */
    fun handleException(throwable: Throwable): AppError {
        Timber.w(throwable, "[ERROR_CLASSIFICATION] Classifying exception")
        
        return when (throwable) {
            is NetworkException -> AppError.Network(
                message = throwable.message,
                statusCode = throwable.statusCode
            )
            is DatabaseException -> AppError.Database(
                message = throwable.message,
                operation = throwable.operation
            )
            is AuthenticationException -> AppError.Authentication(
                message = throwable.message,
                reason = throwable.reason
            )
            is ValidationException -> AppError.Validation(
                message = throwable.message,
                field = throwable.field
            )
            is java.net.UnknownHostException -> AppError.Network(
                message = "عدم دسترسی به سرور",
                statusCode = null
            )
            is java.net.SocketTimeoutException -> AppError.Network(
                message = "زمان اتصال به سرور تمام شد",
                statusCode = null
            )
            is java.io.IOException -> AppError.Network(
                message = "خطا در ارتباط با سرور",
                statusCode = null
            )
            else -> AppError.Unknown(
                message = throwable.message ?: "خطای غیرمنتظره رخ داد",
                throwable = throwable
            )
        }
    }
}

/**
 * 🎯 Sealed class hierarchy for application errors
 * 
 * Each error type contains specific information for proper handling.
 */
sealed class AppError(open val message: String?) {
    
    /**
     * Network-related errors (API calls, connectivity)
     */
    data class Network(
        override val message: String?,
        val statusCode: Int? = null
    ) : AppError(message)
    
    /**
     * Database operation errors (Room, SQLite)
     */
    data class Database(
        override val message: String?,
        val operation: String? = null
    ) : AppError(message)
    
    /**
     * Authentication and authorization errors
     */
    data class Authentication(
        override val message: String?,
        val reason: AuthFailureReason = AuthFailureReason.UNKNOWN
    ) : AppError(message)
    
    /**
     * Input validation errors
     */
    data class Validation(
        override val message: String?,
        val field: String? = null
    ) : AppError(message)
    
    /**
     * Payment-related errors (gateway unavailability, processing failures)
     */
    data class Payment(
        override val message: String?
    ) : AppError(message)
    
    /**
     * Resource not found errors
     */
    data class NotFound(
        override val message: String?
    ) : AppError(message)
    
    /**
     * Unknown or unexpected errors
     */
    data class Unknown(
        override val message: String,
        val throwable: Throwable? = null
    ) : AppError(message)
    
    /**
     * Converts error to user-facing Persian message
     */
    fun toUserMessage(): String = when (this) {
        is Network -> when (statusCode) {
            400 -> "درخواست نامعتبر. لطفال اطلاعات را بررسی کنید."
            401 -> "لطفال دوباره وارد شوید."
            403 -> "دسترسی به این بخش مجاز نیست."
            404 -> "اطلاعات درخواستی یافت نشد."
            408 -> "زمان اتصال تمام شد. لطفال دوباره تلاش کنید."
            500, 502, 503 -> "خطا در سرور. لطفال بعدال تلاش کنید."
            in 500..599 -> "مشکل در سرور. لطفال کمی صبر کنید."
            else -> message ?: "خطا در برقراری ارتباط. اتصال اینترنت خود را بررسی کنید."
        }
        is Database -> "خطا در ذخیره‌سازی اطلاعات. ${operation ?: ""}"
        is Authentication -> when (reason) {
            AuthFailureReason.INVALID_CREDENTIALS -> "نام کاربری یا رمز عبور اشتباه است."
            AuthFailureReason.TOKEN_EXPIRED -> "نشست شما منقضی شده. لطفال دوباره وارد شوید."
            AuthFailureReason.ACCOUNT_LOCKED -> "حساب کاربری شما قفل شده است."
            AuthFailureReason.UNKNOWN -> message ?: "خطا در احراز هویت."
        }
        is Validation -> message ?: "اطلاعات وارد شده معتبر نیست. ${field ?: ""}"
        is Payment -> message ?: "خطا در پرداخت. لطفال دوباره تلاش کنید."
        is NotFound -> message ?: "مورد تلافی نت شد."
        is Unknown -> "خطای غیرمنتظره رخ داد. لطفال دوباره تلاش کنید."
    }
    
    /**
     * Gets HTTP status code if available
     */
    fun getStatusCode(): Int? = (this as? Network)?.statusCode
}

/**
 * 🔐 Reasons for authentication failure
 */
enum class AuthFailureReason {
    INVALID_CREDENTIALS,
    TOKEN_EXPIRED,
    ACCOUNT_LOCKED,
    UNKNOWN
}

// ===== Custom Exception Classes =====

/**
 * Network operation exception
 */
class NetworkException(
    message: String,
    val statusCode: Int? = null,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Database operation exception
 */
class DatabaseException(
    message: String,
    val operation: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Authentication exception
 */
class AuthenticationException(
    message: String,
    val reason: AuthFailureReason = AuthFailureReason.UNKNOWN,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Validation exception
 */
class ValidationException(
    message: String,
    val field: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)
