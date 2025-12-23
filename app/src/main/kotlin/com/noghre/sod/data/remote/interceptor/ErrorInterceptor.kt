package com.noghre.sod.data.remote.interceptor

import com.google.gson.Gson
import com.noghre.sod.data.dto.ApiResponseDto
import com.noghre.sod.data.remote.exception.ApiException
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

/**
 * OkHttp Interceptor for handling HTTP error responses globally.
 * Parses error responses and throws appropriate custom exceptions.
 */
class ErrorInterceptor @Inject constructor(
    private val gson: Gson
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (!response.isSuccessful) {
            val errorBody = response.body?.string() ?: ""
            response.close()

            Timber.e("API Error: ${response.code} - $errorBody")

            val exception = when (response.code) {
                401 -> {
                    Timber.w("Unauthorized - Token expired or invalid")
                    ApiException.UnauthorizedException()
                }
                403 -> {
                    ApiException.HttpException(response.code, errorBody)
                }
                404 -> {
                    ApiException.HttpException(response.code, errorBody)
                }
                500, 502, 503 -> {
                    Timber.e("Server error: ${response.code}")
                    ApiException.ServerException("Server error: ${response.code}")
                }
                408 -> {
                    Timber.e("Request timeout")
                    ApiException.TimeoutException()
                }
                else -> {
                    ApiException.HttpException(response.code, errorBody)
                }
            }

            throw exception
        }

        return response
    }

    private fun parseErrorResponse(errorBody: String): ApiException.ValidationException? {
        return try {
            val apiResponse = gson.fromJson(errorBody, ApiResponseDto::class.java)
            val errors = apiResponse.errors?.associate { it.field ?: "general" to it.message }
                ?: mapOf("general" to (apiResponse.message ?: "Unknown error"))
            ApiException.ValidationException(errors)
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse error response")
            null
        }
    }
}
