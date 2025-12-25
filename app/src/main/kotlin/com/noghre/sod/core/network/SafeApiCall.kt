package com.noghre.sod.core.network

import android.util.Log
import com.noghre.sod.data.remote.dto.response.ResponseDto
import retrofit2.Response

/**
 * Safe API Call Extension Function
 * 
 * Wraps suspend API calls in try-catch and converts to NetworkResult.
 * Handles:
 * - Successful responses with data
 * - HTTP errors (4xx, 5xx)
 * - Network exceptions
 * - Empty/null data handling
 * 
 * Usage:
 * ```
 * val result = safeApiCall {
 *     apiService.getProducts(page = 1)
 * }
 * 
 * when (result) {
 *     is NetworkResult.Success -> handleSuccess(result.data)
 *     is NetworkResult.Error -> handleError(result.message)
 *     is NetworkResult.Loading -> showLoading()
 *     is NetworkResult.Empty -> showEmpty()
 * }
 * ```
 * 
 * @since 1.0.0
 */
suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> Response<ResponseDto<T>>
): NetworkResult<T> {
    return try {
        val response = apiCall()
        
        // Check if response was successful
        if (response.isSuccessful) {
            val body = response.body()
            
            when {
                // Response has success flag and data
                body?.success == true && body.data != null -> {
                    Log.d("SafeApiCall", "✅ API call successful")
                    NetworkResult.Success(body.data)
                }
                
                // Response has success flag but no data
                body?.success == true && body.data == null -> {
                    Log.w("SafeApiCall", "📄 Empty response")
                    NetworkResult.Empty(body.message ?: "داده‌ای یافت نشد")
                }
                
                // Response indicates failure
                body?.success == false -> {
                    Log.e("SafeApiCall", "❌ API returned failure: ${body.message}")
                    NetworkResult.Error(
                        exception = Exception(body.message),
                        errorType = ErrorType.UNKNOWN,
                        code = response.code(),
                        message = body.message
                    )
                }
                
                // Response body is null
                body == null -> {
                    Log.e("SafeApiCall", "❌ Response body is null")
                    NetworkResult.Error(
                        exception = Exception("Response body is null"),
                        errorType = ErrorType.UNKNOWN,
                        code = response.code(),
                        message = "Response body is null"
                    )
                }
                
                // Default case (shouldn't reach here)
                else -> {
                    Log.e("SafeApiCall", "❌ Unexpected response state")
                    NetworkResult.Error(
                        exception = Exception("Unexpected response"),
                        errorType = ErrorType.UNKNOWN,
                        message = "Unexpected response state"
                    )
                }
            }
        } else {
            // HTTP error response
            Log.e("SafeApiCall", "❌ HTTP error: ${response.code()}")
            NetworkErrorHandler.handleException(
                retrofit2.HttpException(response)
            )
        }
        
    } catch (e: Exception) {
        // Network or other exceptions
        Log.e("SafeApiCall", "🚨 Exception: ${e.message}", e)
        NetworkErrorHandler.handleException(e)
    }
}

/**
 * Safe API call with automatic null handling
 * 
 * Returns Empty if data is null, Success otherwise
 */
suspend inline fun <T : Any> safeApiCallNonNull(
    crossinline apiCall: suspend () -> Response<ResponseDto<T>>
): NetworkResult<T> {
    return when (val result = safeApiCall(apiCall)) {
        is NetworkResult.Empty -> NetworkResult.Empty()
        is NetworkResult.Success -> {
            if (result.data != null) {
                NetworkResult.Success(result.data)
            } else {
                NetworkResult.Empty()
            }
        }
        else -> result
    }
}

/**
 * Safe API call with data transformation
 * 
 * Useful for converting DTOs to domain models
 */
suspend inline fun <T, R> safeApiCallMap(
    crossinline apiCall: suspend () -> Response<ResponseDto<T>>,
    crossinline mapper: (T) -> R
): NetworkResult<R> {
    return when (val result = safeApiCall(apiCall)) {
        is NetworkResult.Success -> NetworkResult.Success(mapper(result.data))
        is NetworkResult.Error -> result
        is NetworkResult.Loading -> result
        is NetworkResult.Empty -> result
    }
}

/**
 * Safe API call with list transformation
 * 
 * Useful for converting lists of DTOs to domain models
 */
suspend inline fun <T, R> safeApiCallListMap(
    crossinline apiCall: suspend () -> Response<ResponseDto<List<T>>>,
    crossinline mapper: (T) -> R
): NetworkResult<List<R>> {
    return when (val result = safeApiCall(apiCall)) {
        is NetworkResult.Success -> {
            try {
                NetworkResult.Success(result.data.map(mapper))
            } catch (e: Exception) {
                Log.e("SafeApiCall", "Error mapping list items: ${e.message}")
                NetworkResult.Error(
                    exception = e,
                    errorType = ErrorType.UNKNOWN,
                    message = "Error processing data"
                )
            }
        }
        is NetworkResult.Error -> result
        is NetworkResult.Loading -> result
        is NetworkResult.Empty -> result
    }
}
