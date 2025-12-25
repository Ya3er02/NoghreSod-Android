package com.noghre.sod.core.network

import android.util.Log
import com.noghre.sod.core.exception.AppException
import com.noghre.sod.core.exception.HttpException
import com.noghre.sod.core.exception.NetworkException
import com.noghre.sod.core.exception.UnknownException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Safe API call wrapper that handles all types of network errors.
 * Converts exceptions to Result types for better error handling.
 */
suspend inline fun <reified T> safeApiCall(
    apiCall: suspend () -> Response<T>
): Result<T> = withContext(Dispatchers.IO) {
    try {
        val response = apiCall()
        
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body)
            } else {
                Log.w("SafeApiCall", "Empty response body")
                Result.failure(AppException.EmptyResponseException())
            }
        } else {
            Log.e("SafeApiCall", "HTTP Error: ${response.code()} - ${response.message()}")
            
            val errorBody = response.errorBody()?.string()
            Result.failure(
                HttpException(
                    code = response.code(),
                    message = response.message(),
                    errorBody = errorBody
                )
            )
        }
    } catch (e: SocketTimeoutException) {
        Log.e("SafeApiCall", "Request timeout", e)
        Result.failure(NetworkException.TimeoutException(e))
    } catch (e: ConnectException) {
        Log.e("SafeApiCall", "Connection failed", e)
        Result.failure(NetworkException.ConnectionException(e))
    } catch (e: UnknownHostException) {
        Log.e("SafeApiCall", "Unknown host", e)
        Result.failure(NetworkException.UnknownHostException(e))
    } catch (e: IOException) {
        Log.e("SafeApiCall", "Network error", e)
        Result.failure(NetworkException(e))
    } catch (e: Exception) {
        Log.e("SafeApiCall", "Unknown error", e)
        Result.failure(UnknownException(e))
    }
}

/**
 * Safe API call with retry logic.
 */
suspend inline fun <reified T> safeApiCallWithRetry(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    apiCall: suspend () -> Response<T>
): Result<T> = withContext(Dispatchers.IO) {
    var lastException: Exception? = null
    var delay = initialDelayMs

    repeat(maxRetries) { attempt ->
        val result = safeApiCall(apiCall)
        
        if (result.isSuccess) {
            return@withContext result
        }

        lastException = result.exceptionOrNull()
        
        // Only retry on specific errors
        val shouldRetry = lastException?.let { exception ->
            when (exception) {
                is SocketTimeoutException -> true
                is ConnectException -> true
                is NetworkException.ConnectionException -> true
                is NetworkException.TimeoutException -> true
                else -> false
            }
        } ?: false

        if (shouldRetry && attempt < maxRetries - 1) {
            Log.d("SafeApiCall", "Retry attempt ${attempt + 1}/$maxRetries after ${delay}ms")
            kotlinx.coroutines.delay(delay)
            delay *= 2 // Exponential backoff
        }
    }

    return@withContext Result.failure(
        lastException ?: UnknownException(Exception("Max retries exceeded"))
    )
}