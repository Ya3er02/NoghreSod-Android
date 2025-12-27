package com.noghre.sod.core.util

import com.noghre.sod.core.error.AppError

/**
 * 🐛 Generic Result class that holds a value with its loading status
 * 
 * Represents three states: Loading, Success, and Error.
 * 
 * Usage:
 * ```kotlin
 * val result: Result<List<Product>> = repository.getProducts()
 * 
 * when(result) {
 *     is Result.Success -> displayProducts(result.data)
 *     is Result.Error -> showError(result.error.toUserMessage())
 *     is Result.Loading -> showLoadingIndicator()
 * }
 * 
 * // Or with functional style:
 * result
 *     .onSuccess { products -> displayProducts(products) }
 *     .onError { error -> showError(error.toUserMessage()) }
 *     .onLoading { showLoadingIndicator() }
 * ```
 */
sealed class Result<out T> {
    
    /**
     * Success state with data
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * Error state with classified error
     */
    data class Error(val error: AppError) : Result<Nothing>()
    
    /**
     * Loading state
     */
    object Loading : Result<Nothing>()
    
    // ===== Convenience Properties =====
    
    /**
     * Check if result is success
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * Check if result is error
     */
    val isError: Boolean get() = this is Error
    
    /**
     * Check if result is loading
     */
    val isLoading: Boolean get() = this is Loading
    
    /**
     * Returns data if Success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Returns error if Error, null otherwise
     */
    fun getErrorOrNull(): AppError? = when (this) {
        is Error -> error
        else -> null
    }
    
    /**
     * Returns data if Success, throws exception if Error
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw Exception(error.message)
        is Loading -> throw IllegalStateException("Cannot get data while loading")
    }
    
    /**
     * Returns data if Success, default value otherwise
     */
    fun getOrDefault(defaultValue: T): T = getOrNull() ?: defaultValue
}

// ===== Extension Functions =====

/**
 * Execute action if result is Success
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

/**
 * Execute action if result is Error
 */
inline fun <T> Result<T>.onError(action: (AppError) -> Unit): Result<T> {
    if (this is Result.Error) action(error)
    return this
}

/**
 * Execute action if result is Loading
 */
inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}

/**
 * Transform Success data, keep Error and Loading unchanged
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this
        is Result.Loading -> this
    }
}

/**
 * Transform Success data with another Result, keep Error and Loading unchanged
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return when (this) {
        is Result.Success -> transform(data)
        is Result.Error -> this
        is Result.Loading -> this
    }
}

/**
 * Combine two Results
 */
fun <T1, T2, R> Result<T1>.combine(
    other: Result<T2>,
    transform: (T1, T2) -> R
): Result<R> {
    return when {
        this is Result.Loading || other is Result.Loading -> Result.Loading
        this is Result.Error -> this
        other is Result.Error -> other
        this is Result.Success && other is Result.Success -> {
            Result.Success(transform(this.data, other.data))
        }
        else -> Result.Error(AppError.Unknown("Unknown state in combine"))
    }
}

/**
 * Recover from error with a default value
 */
fun <T> Result<T>.recover(defaultValue: T): Result<T> {
    return when (this) {
        is Result.Success -> this
        is Result.Error -> Result.Success(defaultValue)
        is Result.Loading -> this
    }
}

/**
 * Recover from error with another Result
 */
fun <T> Result<T>.recoverCatching(transform: (AppError) -> Result<T>): Result<T> {
    return when (this) {
        is Result.Success -> this
        is Result.Error -> try {
            transform(error)
        } catch (e: Exception) {
            Result.Error(AppError.Unknown(e.message ?: "Recovery failed", e))
        }
        is Result.Loading -> this
    }
}