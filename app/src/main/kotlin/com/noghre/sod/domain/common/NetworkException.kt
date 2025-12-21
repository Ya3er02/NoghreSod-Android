package com.noghre.sod.domain.common

import java.io.IOException

sealed class NetworkException(message: String) : IOException(message) {
    class NoInternetException : NetworkException("No internet connection")
    data class ServerException(val code: Int, val errorMessage: String) :
        NetworkException("Server error $code: $errorMessage")

    class TimeoutException : NetworkException("Request timeout")
    data class UnknownException(val originalException: Throwable) :
        NetworkException("Unknown error: ${originalException.message}")

    class UnauthorizedException : NetworkException("Unauthorized: Please login again")
    class NotFoundException : NetworkException("Resource not found")
}

fun Int.toNetworkException(message: String = ""): NetworkException = when (this) {
    401 -> NetworkException.UnauthorizedException()
    404 -> NetworkException.NotFoundException()
    in 500..599 -> NetworkException.ServerException(this, message)
    else -> NetworkException.ServerException(this, message)
}
