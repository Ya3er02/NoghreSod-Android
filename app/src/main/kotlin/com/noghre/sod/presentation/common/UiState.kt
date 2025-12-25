package com.noghre.sod.presentation.common

import java.time.Instant

/**
 * Generic UI State with all possible states
 * 
 * Prevents impossible states like:
 * - isLoading=true and error!=null at the same time
 * - Multiple data representations
 * 
 * @param T The type of data this state holds
 * 
 * @since 1.0.0
 */
sealed interface UiState<out T> {
    
    /**
     * Initial state - no operation started yet
     */
    object Idle : UiState<Nothing> {
        override fun toString(): String = "UiState.Idle"
    }
    
    /**
     * Loading state without previous data
     */
    object Loading : UiState<Nothing> {
        override fun toString(): String = "UiState.Loading"
    }
    
    /**
     * Loading state with previous data
     * Used for pull-to-refresh, pagination, or background refresh
     */
    data class LoadingWithData<T>(
        val data: T,
        val timestamp: Long = System.currentTimeMillis()
    ) : UiState<T>
    
    /**
     * Success state with data
     */
    data class Success<T>(
        val data: T,
        val timestamp: Long = System.currentTimeMillis()
    ) : UiState<T>
    
    /**
     * Empty state - operation successful but no data
     */
    data class Empty(
        val message: String? = null,
        val timestamp: Long = System.currentTimeMillis()
    ) : UiState<Nothing>
    
    /**
     * Error state without previous data
     */
    data class Error(
        val error: com.noghre.sod.domain.common.AppError,
        val canRetry: Boolean = true,
        val timestamp: Long = System.currentTimeMillis()
    ) : UiState<Nothing>
    
    /**
     * Error state with previous data (stale data)
     * User can see old data while error occurred
     */
    data class ErrorWithData<T>(
        val data: T,
        val error: com.noghre.sod.domain.common.AppError,
        val canRetry: Boolean = true,
        val timestamp: Long = System.currentTimeMillis()
    ) : UiState<T>
}

/**
 * Extension functions for UiState
 */
fun <T> UiState<T>.isLoading(): Boolean {
    return this is UiState.Loading || this is UiState.LoadingWithData
}

fun <T> UiState<T>.getData(): T? {
    return when (this) {
        is UiState.Success -> data
        is UiState.LoadingWithData -> data
        is UiState.ErrorWithData -> data
        else -> null
    }
}

fun <T> UiState<T>.getError(): com.noghre.sod.domain.common.AppError? {
    return when (this) {
        is UiState.Error -> error
        is UiState.ErrorWithData -> error
        else -> null
    }
}

fun <T> UiState<T>.hasData(): Boolean {
    return getData() != null
}

fun <T> UiState<T>.canShowData(): Boolean {
    return when (this) {
        is UiState.Success,
        is UiState.LoadingWithData,
        is UiState.ErrorWithData -> true
        else -> false
    }
}

inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> {
    return when (this) {
        is UiState.Idle -> UiState.Idle
        is UiState.Loading -> UiState.Loading
        is UiState.LoadingWithData -> UiState.LoadingWithData(transform(data))
        is UiState.Success -> UiState.Success(transform(data))
        is UiState.Empty -> this
        is UiState.Error -> this
        is UiState.ErrorWithData -> UiState.ErrorWithData(transform(data), error, canRetry)
    }
}

inline fun <T, R> UiState<T>.flatMap(transform: (T) -> UiState<R>): UiState<R> {
    return when (this) {
        is UiState.Idle -> UiState.Idle
        is UiState.Loading -> UiState.Loading
        is UiState.LoadingWithData -> {
            when (val result = transform(data)) {
                is UiState.Success -> UiState.LoadingWithData(result.data)
                is UiState.LoadingWithData -> result
                else -> result
            }
        }
        is UiState.Success -> transform(data)
        is UiState.Empty -> this
        is UiState.Error -> this
        is UiState.ErrorWithData -> {
            when (val result = transform(data)) {
                is UiState.Error -> UiState.ErrorWithData(data, result.error, result.canRetry)
                else -> result
            }
        }
    }
}

inline fun <T> UiState<T>.onSuccess(block: (T) -> Unit): UiState<T> {
    if (this is UiState.Success) {
        block(data)
    }
    return this
}

inline fun <T> UiState<T>.onError(block: (com.noghre.sod.domain.common.AppError) -> Unit): UiState<T> {
    when (this) {
        is UiState.Error -> block(error)
        is UiState.ErrorWithData -> block(error)
        else -> {}
    }
    return this
}

inline fun <T> UiState<T>.onLoading(block: () -> Unit): UiState<T> {
    if (this is UiState.Loading) {
        block()
    }
    return this
}

/**
 * Type aliases for common UI states
 */
typealias ProductsUiState = UiState<ProductsData>
typealias ProductDetailUiState = UiState<ProductDetailData>
typealias CartUiState = UiState<CartData>
typealias AuthUiState = UiState<AuthData>

/**
 * Data classes for state
 */
data class ProductsData(
    val products: List<com.noghre.sod.domain.model.Product>,
    val filters: FilterState = FilterState(),
    val pagination: PaginationState = PaginationState()
)

data class FilterState(
    val selectedCategory: String? = null,
    val priceRange: Pair<Long, Long>? = null,
    val sortBy: String = "newest",
    val searchQuery: String = ""
)

data class PaginationState(
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val pageSize: Int = 20,
    val total: Int = 0
)

data class ProductDetailData(
    val product: com.noghre.sod.domain.model.Product,
    val relatedProducts: List<com.noghre.sod.domain.model.Product> = emptyList()
)

data class CartData(
    val items: List<com.noghre.sod.domain.model.CartItem>,
    val subtotal: Long,
    val discount: Long = 0,
    val shipping: Long = 0,
    val total: Long
)

data class AuthData(
    val userId: String,
    val email: String,
    val name: String,
    val token: String
)
