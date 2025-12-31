package com.noghre.sod.data.error

import com.noghre.sod.core.ui.ErrorState
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Handles exceptions and maps them to appropriate ErrorState.
 *
 * Provides centralized exception handling for:
 * - Network errors (IOException, SocketTimeoutException, UnknownHostException)
 * - HTTP errors (4xx, 5xx responses)
 * - Data parsing errors
 * - Unknown/unexpected errors
 *
 * Usage:
 * ```
 * try {
 *     // API call or data operation
 * } catch (e: Exception) {
 *     val errorState = ExceptionHandler.handle(e)
 *     // UI layer will handle based on errorState
 * }
 * ```
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
object ExceptionHandler {

    /**
     * Map exception to ErrorState for UI display.
     *
     * @param exception The exception to handle
     * @param context Optional context for better error messages
     * @return ErrorState representing the exception
     */
    fun handle(exception: Exception, context: String = ""): ErrorState {
        Timber.e(exception, "Exception handled: $context")

        return when (exception) {
            // ============ NETWORK ERRORS ============
            is SocketTimeoutException ->
                ErrorState.Timeout(
                    message = "Request timeout. Please try again."
                )

            is UnknownHostException, is IOException ->
                ErrorState.NetworkError(
                    message = "No internet connection. Please check your network.",
                    retryable = true
                )

            // ============ HTTP ERRORS ============
            is HttpException -> handleHttpException(exception)

            // ============ UNKNOWN ERRORS ============
            else -> ErrorState.Unknown(
                throwable = exception,
                message = exception.message ?: "An unknown error occurred"
            )
        }
    }

    /**
     * Map HTTP status codes to specific error states.
     *
     * @param exception HttpException from Retrofit
     * @return ErrorState specific to HTTP error code
     */
    private fun handleHttpException(exception: HttpException): ErrorState {
        val code = exception.code()
        val message = exception.message()
        val responseBody = exception.response()?.errorBody()?.string() ?: ""

        return when (code) {
            // ============ CLIENT ERRORS (4xx) ============
            400 -> ErrorState.ServerError(
                code = code,
                message = "Bad request. Please check your input.",
                retryable = false
            )

            401 -> ErrorState.Unauthorized(
                message = "Your session has expired. Please log in again."
            )

            403 -> ErrorState.Forbidden(
                message = "You don't have permission to perform this action."
            )

            404 -> ErrorState.NotFound(
                message = "The requested resource was not found."
            )

            409 -> ErrorState.ServerError(
                code = code,
                message = "Conflict: Resource already exists or has been modified.",
                retryable = false
            )

            422 -> ErrorState.ServerError(
                code = code,
                message = "Validation failed. Please check your input.",
                retryable = false
            )

            429 -> ErrorState.ServerError(
                code = code,
                message = "Too many requests. Please wait a moment and try again.",
                retryable = true
            )

            // ============ SERVER ERRORS (5xx) ============
            500 -> ErrorState.ServerError(
                code = code,
                message = "Server error. Please try again later.",
                retryable = true
            )

            502, 503, 504 -> ErrorState.ServerError(
                code = code,
                message = "Service temporarily unavailable. Please try again later.",
                retryable = true
            )

            // ============ UNKNOWN HTTP ERROR ============
            else -> ErrorState.ServerError(
                code = code,
                message = message.takeIf { it.isNotEmpty() } ?: "HTTP Error $code",
                retryable = code >= 500
            )
        }
    }

    /**
     * Check if error is retryable.
     *
     * @param errorState The error state
     * @return true if operation can be retried
     */
    fun isRetryable(errorState: ErrorState): Boolean = when (errorState) {
        is ErrorState.NetworkError -> errorState.retryable
        is ErrorState.ServerError -> errorState.retryable
        is ErrorState.Timeout -> true
        is ErrorState.ValidationError -> false
        is ErrorState.Unauthorized -> false
        is ErrorState.Forbidden -> false
        is ErrorState.NotFound -> false
        is ErrorState.Unknown -> false
    }

    /**
     * Get retry delay in milliseconds based on error type.
     *
     * @param errorState The error state
     * @param attemptCount Current attempt count (for exponential backoff)
     * @return Delay in milliseconds
     */
    fun getRetryDelay(errorState: ErrorState, attemptCount: Int = 0): Long {
        return when (errorState) {
            is ErrorState.NetworkError -> 1000L * (attemptCount + 1)
            is ErrorState.Timeout -> 500L * (attemptCount + 1)
            is ErrorState.ServerError -> when (errorState.code) {
                429 -> 5000L * (attemptCount + 1)  // Rate limited - wait longer
                500, 502, 503, 504 -> 2000L * (attemptCount + 1)  // Server error
                else -> 1000L * (attemptCount + 1)
            }
            else -> 0L
        }
    }

    /**
     * Get user-friendly error message.
     *
     * @param errorState The error state
     * @return Message suitable for display to user
     */
    fun getUserMessage(errorState: ErrorState): String = when (errorState) {
        is ErrorState.NetworkError -> "بدون اتصال اینترنت. لطفا اتصال خود را بررسی کنید."
        is ErrorState.ServerError -> when (errorState.code) {
            500 -> "خطای سرور. لطفا دوباره تلاش کنید."
            502, 503, 504 -> "سرویس موقتا در دسترس نیست. لطفا بعد از چند لحظه دوباره تلاش کنید."
            429 -> "درخواست‌های بیش از حد. لطفا کمی صبر کنید."
            else -> errorState.message
        }
        is ErrorState.Unauthorized -> "لطفا مجدد وارد شوید."
        is ErrorState.Forbidden -> "شما دسترسی به این منبع را ندارید."
        is ErrorState.NotFound -> "منبع یافت نشد."
        is ErrorState.Timeout -> "زمان درخواست تمام شد. لطفا دوباره تلاش کنید."
        is ErrorState.ValidationError -> "لطفا خطاهای فرم را تصحیح کنید و دوباره تلاش کنید."
        is ErrorState.Unknown -> errorState.message
    }
}
