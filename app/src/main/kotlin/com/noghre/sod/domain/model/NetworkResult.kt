package com.noghre.sod.domain.model

import java.io.IOException

/**
 * Type-safe network result wrapper
 * 
 * نتیجه شبکه امن:
 * ✅ Success: داده با موفقیت دریافت
 * ❌ Error: خطا با نوع و پیام
 * ⏳ Loading: در حال دریافت
 */
sealed class NetworkResult<out T> {
    /**
     * موفقیت‌آمیز دریافت داده
     */
    data class Success<T>(
        val data: T,
        val statusCode: Int = 200,
        val message: String? = null
    ) : NetworkResult<T>()

    /**
     * خطا در شبکه یا سرور
     */
    data class Error(
        val exception: Throwable,
        val errorType: ErrorType,
        val message: String? = null,
        val statusCode: Int? = null
    ) : NetworkResult<Nothing>()

    /**
     * در حال دریافت
     */
    object Loading : NetworkResult<Nothing>()
}

/**
 * انواع خطاهای ممکن
 */
enum class ErrorType {
    /** اتصال اینترنت قطع */
    NETWORK_ERROR,
    
    /** درخواست منقضی شد */
    TIMEOUT_ERROR,
    
    /** خطای سرور (5xx) */
    SERVER_ERROR,
    
    /** خطای کالینت (4xx) */
    CLIENT_ERROR,
    
    /** عدم احراز هویت (401) */
    UNAUTHORIZED,
    
    /** عدم دسترسی (403) */
    FORBIDDEN,
    
    /** یافت نشد (404) */
    NOT_FOUND,
    
    /** خطای پرداخت */
    PAYMENT_FAILED,
    
    /** خطای تایید صحت داده */
    VALIDATION_ERROR,
    
    /** خطای نامشخص */
    UNKNOWN
}

/**
 * Extension function برای دسترسی امن به داده
 */
fun <T> NetworkResult<T>.getOrNull(): T? = when (this) {
    is NetworkResult.Success -> this.data
    is NetworkResult.Error -> null
    is NetworkResult.Loading -> null
}

/**
 * Extension function برای دسترسی امن به خطا
 */
fun <T> NetworkResult<T>.getErrorOrNull(): NetworkResult.Error? = when (this) {
    is NetworkResult.Error -> this
    else -> null
}

/**
 * Extension function برای بررسی موفقیت
 */
fun <T> NetworkResult<T>.isSuccess(): Boolean = this is NetworkResult.Success

/**
 * Extension function برای بررسی خطا
 */
fun <T> NetworkResult<T>.isError(): Boolean = this is NetworkResult.Error

/**
 * Extension function برای بررسی loading
 */
fun <T> NetworkResult<T>.isLoading(): Boolean = this is NetworkResult.Loading

/**
 * Extension function برای تبدیل داده (map)
 */
inline fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> = when (this) {
    is NetworkResult.Success -> NetworkResult.Success(
        data = transform(this.data),
        statusCode = this.statusCode,
        message = this.message
    )
    is NetworkResult.Error -> NetworkResult.Error(
        exception = this.exception,
        errorType = this.errorType,
        message = this.message,
        statusCode = this.statusCode
    )
    is NetworkResult.Loading -> NetworkResult.Loading
}

/**
 * Extension function برای اجرای عملیات (fold)
 */
inline fun <T, R> NetworkResult<T>.fold(
    onLoading: () -> R,
    onSuccess: (T) -> R,
    onError: (NetworkResult.Error) -> R
): R = when (this) {
    is NetworkResult.Loading -> onLoading()
    is NetworkResult.Success -> onSuccess(this.data)
    is NetworkResult.Error -> onError(this)
}

/**
 * Helper function برای safe API calls
 */
suspend inline fun <T> safeApiCall(
    apiCall: suspend () -> T
): NetworkResult<T> = try {
    val result = apiCall()
    NetworkResult.Success(result)
} catch (e: IOException) {
    NetworkResult.Error(
        exception = e,
        errorType = ErrorType.NETWORK_ERROR,
        message = "خطای اتصال اینترنت. لطفا دوباره سعی کنید."
    )
} catch (e: Exception) {
    NetworkResult.Error(
        exception = e,
        errorType = ErrorType.UNKNOWN,
        message = e.localizedMessage ?: "خطای نامشخص"
    )
}

/**
 * Helper function برای دریافت پیام خطا انسان‌پسند
 */
fun ErrorType.getLocalizedMessage(): String = when (this) {
    ErrorType.NETWORK_ERROR -> "اتصال اینترنت قطع است."
    ErrorType.TIMEOUT_ERROR -> "درخواست زمان خود را سپری کرد. دوباره سعی کنید."
    ErrorType.SERVER_ERROR -> "خطای سرور. لطفا بعداً سعی کنید."
    ErrorType.CLIENT_ERROR -> "درخواست نامعتبر است."
    ErrorType.UNAUTHORIZED -> "نیاز به ورود مجدد دارید."
    ErrorType.FORBIDDEN -> "شما دسترسی ندارید."
    ErrorType.NOT_FOUND -> "مورد جستجو شده یافت نشد."
    ErrorType.PAYMENT_FAILED -> "خطا در پرداخت. لطفا دوباره سعی کنید."
    ErrorType.VALIDATION_ERROR -> "داده‌های ارسالی معتبر نیستند."
    ErrorType.UNKNOWN -> "خطای نامشخص رخ داد."
}
