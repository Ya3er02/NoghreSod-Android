package com.noghre.sod.domain

seal class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()

    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (String) -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(message)
        is Loading -> onError("Loading")
    }
}
