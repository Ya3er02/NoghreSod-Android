package com.noghre.sod.data.network

import android.util.Log
import kotlinx.serialization.Serializable
import retrofit2.Response
import java.io.IOException

/**
 * Type-safe API response wrapper
 * Handles success, error, and loading states uniformly
 */
@Serializable
sealed class ApiResponse<out T : Any> {
    
    data class Success<T : Any>(val data: T, val code: Int = 200) : ApiResponse<T>()
    
    data class Error<T : Any>(
        val message: String,
        val code: Int = 500,
        val exception: Exception? = null
    ) : ApiResponse<T>()
    
    data class Loading<T : Any>(
        val isLoading: Boolean = true
    ) : ApiResponse<T>()
    
    data class NetworkError<T : Any>(
        val message: String = "Network error",
        val exception: IOException? = null
    ) : ApiResponse<T>()
}

/**
 * Extension to transform Retrofit Response to ApiResponse
 */
inline fun <reified T : Any> Response<T>.toApiResponse(): ApiResponse<T> {
    return when {
        isSuccessful -> {
            val body = body()
            if (body != null) {
                ApiResponse.Success(body, code())
            } else {
                ApiResponse.Error(
                    message = "Empty response body",
                    code = code()
                )
            }
        }
        code() == 401 -> ApiResponse.Error(
            message = "Unauthorized",
            code = code()
        )
        code() == 403 -> ApiResponse.Error(
            message = "Forbidden",
            code = code()
        )
        code() == 404 -> ApiResponse.Error(
            message = "Not found",
            code = code()
        )
        code() == 429 -> ApiResponse.Error(
            message = "Too many requests",
            code = code()
        )
        code() == 500 -> ApiResponse.Error(
            message = "Server error",
            code = code()
        )
        else -> ApiResponse.Error(
            message = message(),
            code = code()
        )
    }
}

/**
 * Safe API call wrapper with error handling
 */
suspend inline fun <reified T : Any> safeApiCall(
    apiCall: suspend () -> Response<T>
): ApiResponse<T> {
    return try {
        val response = apiCall()
        response.toApiResponse()
    } catch (e: IOException) {
        Log.e("ApiResponse", "Network error: ${e.message}", e)
        ApiResponse.NetworkError(
            message = "Network error: ${e.message}",
            exception = e
        )
    } catch (e: Exception) {
        Log.e("ApiResponse", "Unknown error: ${e.message}", e)
        ApiResponse.Error(
            message = "Unknown error: ${e.message}",
            exception = e
        )
    }
}

/**
 * Retry logic with exponential backoff
 */
suspend inline fun <reified T : Any> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 100,
    maxDelayMs: Long = 10000,
    backoffMultiplier: Double = 2.0,
    apiCall: suspend () -> ApiResponse<T>
): ApiResponse<T> {
    var currentDelay = initialDelayMs
    var lastException: Exception? = null
    
    repeat(maxRetries) { attempt ->
        return try {
            apiCall()
        } catch (e: Exception) {
            lastException = e
            if (attempt < maxRetries - 1) {
                Log.d(
                    "RetryLogic",
                    "Attempt $attempt failed, retrying in ${currentDelay}ms"
                )
                kotlinx.coroutines.delay(currentDelay)
                currentDelay = (currentDelay * backoffMultiplier).toLong()
                    .coerceAtMost(maxDelayMs)
            }
            null
        } ?: return@repeat
    }
    
    return ApiResponse.Error(
        message = "Failed after $maxRetries attempts: ${lastException?.message}",
        exception = lastException
    )
}

/**
 * Pagination response wrapper
 */
@Serializable
data class PaginatedResponse<T : Any>(
    val data: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
) {
    fun isLastPage(): Boolean = !hasNextPage
    fun isEmpty(): Boolean = data.isEmpty()
}

/**
 * API error details
 */
@Serializable
data class ApiErrorResponse(
    val code: Int,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val path: String? = null,
    val details: Map<String, String>? = null
)

/**
 * Helper to extract error message from ApiResponse
 */
fun <T : Any> ApiResponse<T>.getErrorMessage(): String? {
    return when (this) {
        is ApiResponse.Error -> this.message
        is ApiResponse.NetworkError -> this.message
        else -> null
    }
}

/**
 * Helper to get data from ApiResponse safely
 */
fun <T : Any> ApiResponse<T>.getDataOrNull(): T? {
    return when (this) {
        is ApiResponse.Success -> this.data
        else -> null
    }
}

/**
 * Helper to handle ApiResponse with callbacks
 */
inline fun <T : Any> ApiResponse<T>.handle(
    onSuccess: (T) -> Unit,
    onError: (String) -> Unit = {},
    onLoading: (() -> Unit)? = null,
    onNetworkError: (String) -> Unit = {}
) {
    when (this) {
        is ApiResponse.Success -> onSuccess(this.data)
        is ApiResponse.Error -> onError(this.message)
        is ApiResponse.Loading -> onLoading?.invoke()
        is ApiResponse.NetworkError -> onNetworkError(this.message)
    }
}
