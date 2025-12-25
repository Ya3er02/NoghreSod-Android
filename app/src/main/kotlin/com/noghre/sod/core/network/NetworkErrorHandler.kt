package com.noghre.sod.core.network

import android.util.Log
import com.google.gson.Gson
import com.noghre.sod.data.remote.dto.response.ErrorResponseDto
import retrofit2.HttpException
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
    
    private const val TAG = "NetworkErrorHandler"
    private val gson = Gson()
    
    /**
     * Convert any exception to NetworkResult.Error
     * 
     * @param exception Throwable to handle
     * @return NetworkResult.Error with appropriate type and message
     */
    fun <T> handleException(exception: Throwable): NetworkResult<T> {
        Log.e(TAG, "🚨 Network error: ${exception::class.simpleName}", exception)
        
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
                    message = "حال اتصال به خرجی رساند. لطفا دوباره تلاش کنید."
                )
            }
            
            // SSL/Certificate errors
            is SSLHandshakeException -> {
                NetworkResult.Error(
                    exception = exception,
                    errorType = ErrorType.SSL_ERROR,
                    message = "خطای امنیتی در اتصال. ابتدا تاريخ را بررسی کنید."
                )
            }
            
            is SSLPeerUnverifiedException -> {
                NetworkResult.Error(
                    exception = exception,
                    errorType = ErrorType.SSL_ERROR,
                    message = "گواهینامه سرور صحیح نیست. با ڤشتیبانی تماس کنید."
                )
            }
            
            // HTTP errors
            is HttpException -> handleHttpException(exception)
            
            // Other IO errors
            is IOException -> {
                NetworkResult.Error(
                    exception = exception,
                    errorType = ErrorType.NETWORK,
                    message = "خطای کمونیکاسیون
"
                )
            }
            
            // Default
            else -> {
                NetworkResult.Error(
                    exception = exception,
                    errorType = ErrorType.UNKNOWN,
                    message = exception.message ?: "خطای نامعلوم."
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
            Log.d(TAG, "Could not parse error body: ${e.message}")
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
            400 -> "درخواست نامعتبر."
            401 -> "لطفا دوباره وارد شوید."
            403 -> "این بخش را وصول ندارید."
            404 -> "محتوای انتخاب شده يافت نشد."
            422 -> "اطلاعات وارد وار ات اعتبار است."
            429 -> "خيلی درخواست. فعلا لطفا برای انجام گرفته های بیشتر متناظر شويد."
            
            // Server errors
            500 -> "خطای سرور. لطفا بعداً تلاش کنید."
            502 -> "ترافيک زیاد روطه. لطفا بعداً تلاش کنید."
            503 -> "سرويس اینطوقت هستاند. لطفا بعداً دوباره تلاش کنید."
            504 -> "زمان درخواست به اته اىو رساند."
            in 500..599 -> "خطای سرور ($code)."
            
            // Unknown
            else -> "خطای ($code)."
        }
    }
}
