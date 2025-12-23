package com.noghre.sod.core.result

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Safe wrapper for API calls that converts Retrofit responses to Result objects.
 * Handles both successful and error responses with proper type conversion.
 */
object SafeApiCall {

    private const val TAG = "SafeApiCall"

    /**
     * Wraps a Retrofit API call with error handling.
     *
     * @param dispatcher The coroutine dispatcher to execute on
     * @param apiCall Suspend function that makes the API call
     * @return Result containing the parsed response or an error
     */
    suspend inline fun <T> execute(
        dispatcher: CoroutineDispatcher,
        crossinline apiCall: suspend () -> Response<T>
    ): Result<T> {
        return withContext(dispatcher) {
            try {
                val response = apiCall()
                handleResponse(response)
            } catch (e: Exception) {
                Log.e(TAG, "API call error: ", e)
                Result.error(e.toAppError())
            }
        }
    }

    /**
     * Wraps a Retrofit API call without dispatcher switching.
     *
     * @param apiCall Suspend function that makes the API call
     * @return Result containing the parsed response or an error
     */
    suspend inline fun <T> executeAsync(
        crossinline apiCall: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = apiCall()
            handleResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "API call error: ", e)
            Result.error(e.toAppError())
        }
    }

    /**
     * Handles Retrofit Response and converts to Result.
     *
     * @param response The Retrofit Response
     * @return Result with data or error
     */
    private inline fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Log.d(TAG, "API call successful: ${response.code()}")
                Result.success(body)
            } else {
                Log.e(TAG, "Response body is null for successful response")
                Result.error(
                    AppError.UnknownError(
                        Exception("Response body is null")
                    )
                )
            }
        } else {
            Log.e(TAG, "API call failed: ${response.code()} - ${response.message()}")
            val httpException = retrofit2.HttpException(response)
            Result.error(AppError.ServerError.from(httpException))
        }
    }
}

/**
 * Extension function for convenient safe API calls.
 *
 * Usage:
 * val result = safeApiCall(ioDispatcher) { apiService.getProducts() }
 */
suspend inline fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    crossinline apiCall: suspend () -> Response<T>
): Result<T> = SafeApiCall.execute(dispatcher, apiCall)

/**
 * Extension function for safe API calls without dispatcher.
 *
 * Usage:
 * val result = safeApiCallAsync { apiService.getProducts() }
 */
suspend inline fun <T> safeApiCallAsync(
    crossinline apiCall: suspend () -> Response<T>
): Result<T> = SafeApiCall.executeAsync(apiCall)
