package com.noghre.sod.domain.common

class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
class ServerException(message: String, cause: Throwable? = null) : Exception(message, cause)
class UnauthorizedException(message: String, cause: Throwable? = null) : Exception(message, cause)
class ValidationException(message: String, cause: Throwable? = null) : Exception(message, cause)
class NotFoundException(message: String, cause: Throwable? = null) : Exception(message, cause)
