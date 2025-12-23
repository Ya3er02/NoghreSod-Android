package com.noghre.sod.domain.common

object ErrorMapper {
    fun mapError(throwable: Throwable): String {
        return when (throwable) {
            is NetworkException -> "لطفا ااتصال اینترنت خود را بررسی کنید"
            is ServerException -> "ارور در سرویس، لطفا بعدا تلاش کنید"
            is UnauthorizedException -> "لطفا دوباره وارد شوید"
            is ValidationException -> throwable.message ?: "مقدار وارد شده نامعتبر است"
            is NotFoundException -> "مورد جستجو شده یافت نشد"
            else -> throwable.message ?: "خطای نامشخص"
        }
    }
}

class NetworkException(message: String) : Exception(message)
class ServerException(message: String) : Exception(message)
class UnauthorizedException(message: String) : Exception(message)
class ValidationException(message: String) : Exception(message)
class NotFoundException(message: String) : Exception(message)
