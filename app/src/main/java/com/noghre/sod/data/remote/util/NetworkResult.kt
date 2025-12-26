package com.noghre.sod.data.remote.util

import retrofit2.Response
import timber.log.Timber

/**
 * Sealed class for handling network results with type safety.
 * Replaces raw Response<T> with proper error handling and state management.
 *
 * States:
 * - Success: Response successful with data
 * - Error: Response failed with error message and code
 * - Loading: Request in progress
 *
 * Benefits:
 * - Type-safe network result handling
 * - Centralized error management
 * - Easy state machine handling
 * - Null safety
 *
 * @author Yaser
 * @version 1.0.0
 */
sealed class NetworkResult<T> {
    /**
     * Successful network response.
     *
     * @param data The response body data
     */
    data class Success<T>(val data: T) : NetworkResult<T>()

    /**
     * Failed network response.
     *
     * @param message Error message
     * @param code HTTP error code (optional)
     */
    data class Error<T>(val message: String, val code: Int? = null) : NetworkResult<T>()

    /**
     * Loading state.
     * Used to show loading indicators.
     */
    class Loading<T> : NetworkResult<T>()

    /**
     * Check if this is a success result.
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Check if this is an error result.
     */
    fun isError(): Boolean = this is Error

    /**
     * Check if this is a loading state.
     */
    fun isLoading(): Boolean = this is Loading

    /**
     * Get the data if successful, null otherwise.
     */
    fun getDataOrNull(): T? = if (this is Success) data else null

    /**
     * Get the error message if error, null otherwise.
     */
    fun getErrorOrNull(): String? = if (this is Error) message else null
}

/**
 * Extension function to safely handle API responses.
 * Converts Retrofit Response to NetworkResult with proper error handling.
 *
 * Handles:
 * - Successful responses (HTTP 2xx)
 * - Error responses (HTTP 4xx, 5xx)
 * - Network exceptions
 * - Empty response bodies
 *
 * @param apiCall Suspend function that returns a Retrofit Response
 * @return NetworkResult containing success data or error information
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            // Successful response
            response.body()?.let {
                Timber.d("API call successful: ${response.code()}")
                NetworkResult.Success(it)
            } ?: run {
                // Empty response body
                val errorMsg = "Empty response body"
                Timber.w("API call failed: $errorMsg (${response.code()})")
                NetworkResult.Error(errorMsg, response.code())
            }
        } else {
            // Error response
            val errorMsg = response.errorBody()?.string()
                ?: "Unknown error: ${response.message()}"
            val code = response.code()
            Timber.e("API call failed: $errorMsg ($code)")
            NetworkResult.Error(errorMsg, code)
        }
    } catch (e: Exception) {
        // Network exception
        val errorMsg = e.localizedMessage ?: "Network error occurred"
        Timber.e(e, "Network exception: $errorMsg")
        NetworkResult.Error(errorMsg)
    }
}

/**
 * Extension function to safely handle API responses and transform data.
 * Useful for transforming DTO to domain models.
 *
 * @param apiCall Suspend function that returns a Retrofit Response
 * @param transform Function to transform response to desired type
 * @return NetworkResult containing transformed data or error
 */
suspend fun <T, R> safeApiCall(
    apiCall: suspend () -> Response<T>,
    transform: (T) -> R
): NetworkResult<R> {
    return when (val result = safeApiCall(apiCall)) {
        is NetworkResult.Success -> {
            try {
                val transformedData = transform(result.data)
                NetworkResult.Success(transformedData)
            } catch (e: Exception) {
                Timber.e(e, "Data transformation failed")
                NetworkResult.Error("Data transformation failed: ${e.message}")
            }
        }
        is NetworkResult.Error -> NetworkResult.Error(result.message, result.code)
        is NetworkResult.Loading -> NetworkResult.Loading()
    }
}

/**
 * Convert NetworkResult to nullable data type.
 * Useful for simple cases where you only need data or null.
 */
fun <T> NetworkResult<T>.toDataOrNull(): T? = when (this) {
    is NetworkResult.Success -> this.data
    else -> null
}

/**
 * Convert NetworkResult to Result<T>.
 * Useful for compatibility with Kotlin Result API.
 */
fun <T> NetworkResult<T>.toResult(): Result<T> = when (this) {
    is NetworkResult.Success -> Result.success(this.data)
    is NetworkResult.Error -> Result.failure(Exception(this.message))
    is NetworkResult.Loading -> Result.failure(Exception("Loading"))
}
