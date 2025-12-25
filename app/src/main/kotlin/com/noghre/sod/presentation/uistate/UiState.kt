package com.noghre.sod.presentation.uistate

import com.noghre.sod.domain.model.error.AppError
import kotlinx.coroutines.flow.Flow

/**
 * 🎨 Generic UI State Pattern
 * 
 * Ensures:
 * - No impossible states
 * - Type-safe state management
 * - Consistent state transitions
 * - Proper loading, error, and data states
 * 
 * @since 1.0.0
 */
sealed interface UiState<out T> {
    
    /**
     * Initial state - no operations started
     */
    object Idle : UiState<Nothing>
    
    /**
     * Loading state - without previous data
     */
    object Loading : UiState<Nothing>
    
    /**
     * Loading state - with previous data
     * Used for pull-to-refresh or pagination
     */
    data class LoadingWithData<T>(
        val data: T,
        val progress: Float? = null
    ) : UiState<T>
    
    /**
     * Success state - with data
     */
    data class Success<T>(
        val data: T,
        val timestamp: Long = System.currentTimeMillis()
    ) : UiState<T>
    
    /**
     * Empty state - operation succeeded but no data
     */
    data class Empty(
        val message: String? = null
    ) : UiState<Nothing>
    
    /**
     * Error state - without previous data
     */
    data class Error(
        val error: AppError,
        val canRetry: Boolean = true
    ) : UiState<Nothing>
    
    /**
     * Error state - with stale data
     * Show previous data with error indicator
     */
    data class ErrorWithData<T>(
        val data: T,
        val error: AppError,
        val canRetry: Boolean = true
    ) : UiState<T>
}

/**
 * Partial Success State
 * Used when some items succeed and some fail (batch operations)
 */
data class PartialSuccess<T>(
    val successData: List<T>,
    val errors: List<AppError>,
    val totalCount: Int
) : UiState<List<T>>

// ==================== Extension Functions ====================

/**
 * Check if currently loading (any loading state)
 */
fun <T> UiState<T>.isLoading(): Boolean {
    return this is UiState.Loading || this is UiState.LoadingWithData
}

/**
 * Check if has data
 */
fun <T> UiState<T>.hasData(): Boolean {
    return this is UiState.Success || 
           this is UiState.LoadingWithData || 
           this is UiState.ErrorWithData
}

/**
 * Get data if available
 */
fun <T> UiState<T>.getData(): T? {
    return when (this) {
        is UiState.Success -> data
        is UiState.LoadingWithData -> data
        is UiState.ErrorWithData -> data
        else -> null
    }
}

/**
 * Get error if available
 */
fun <T> UiState<T>.getError(): AppError? {
    return when (this) {
        is UiState.Error -> error
        is UiState.ErrorWithData -> error
        else -> null
    }
}

/**
 * Check if can retry
 */
fun <T> UiState<T>.canRetry(): Boolean {
    return when (this) {
        is UiState.Error -> canRetry
        is UiState.ErrorWithData -> canRetry
        else -> false
    }
}

/**
 * Transform data in state
 */
fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> {
    return when (this) {
        is UiState.Idle -> UiState.Idle
        is UiState.Loading -> UiState.Loading
        is UiState.LoadingWithData -> UiState.LoadingWithData(transform(data), progress)
        is UiState.Success -> UiState.Success(transform(data), timestamp)
        is UiState.Empty -> this
        is UiState.Error -> this
        is UiState.ErrorWithData -> UiState.ErrorWithData(transform(data), error, canRetry)
    }
}

/**
 * Combine two states
 */
fun <T, R, U> UiState<T>.combine(
    other: UiState<R>,
    transform: (T?, R?) -> U
): UiState<U> {
    val data1 = this.getData()
    val data2 = other.getData()
    val error1 = this.getError()
    val error2 = other.getError()
    
    return when {
        this is UiState.Loading || other is UiState.Loading -> UiState.Loading
        data1 != null && data2 != null -> UiState.Success(transform(data1, data2))
        error1 != null -> UiState.Error(error1)
        error2 != null -> UiState.Error(error2)
        else -> UiState.Idle
    }
}

/**
 * Fold state into value
 */
fun <T, R> UiState<T>.fold(
    onIdle: () -> R,
    onLoading: () -> R,
    onLoadingWithData: (T) -> R,
    onSuccess: (T) -> R,
    onEmpty: (String?) -> R,
    onError: (AppError, Boolean) -> R,
    onErrorWithData: (T, AppError, Boolean) -> R
): R {
    return when (this) {
        is UiState.Idle -> onIdle()
        is UiState.Loading -> onLoading()
        is UiState.LoadingWithData -> onLoadingWithData(data)
        is UiState.Success -> onSuccess(data)
        is UiState.Empty -> onEmpty(message)
        is UiState.Error -> onError(error, canRetry)
        is UiState.ErrorWithData -> onErrorWithData(data, error, canRetry)
    }
}

// ==================== Specific Type Aliases ====================

typealias ProductsUiState = UiState<ProductsData>
typealias ProductDetailUiState = UiState<ProductDetailData>
typealias CartUiState = UiState<CartData>
typealias OrdersUiState = UiState<OrdersData>

// ==================== Data Classes for States ====================

/**
 * Products list data
 */
data class ProductsData(
    val products: List<Any>, // Product
    val filters: FilterState = FilterState(),
    val pagination: PaginationState = PaginationState()
)

/**
 * Filter state
 */
data class FilterState(
    val selectedCategory: String? = null,
    val priceRange: Pair<Long, Long>? = null,
    val sortBy: SortOption = SortOption.NEWEST,
    val searchQuery: String = "",
    val inStock: Boolean? = null
)

enum class SortOption {
    NEWEST, PRICE_LOW, PRICE_HIGH, RATING, POPULAR
}

/**
 * Pagination state
 */
data class PaginationState(
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val pageSize: Int = 20,
    val totalCount: Int = 0
)

/**
 * Product detail data
 */
data class ProductDetailData(
    val product: Any, // Product
    val relatedProducts: List<Any> = emptyList(),
    val reviews: List<Any> = emptyList()
)

/**
 * Cart data
 */
data class CartData(
    val items: List<Any>, // CartItem
    val subtotal: Long,
    val discount: Long = 0,
    val shipping: Long = 0,
    val total: Long
)

/**
 * Orders data
 */
data class OrdersData(
    val orders: List<Any>, // Order
    val filters: OrderFilterState = OrderFilterState(),
    val pagination: PaginationState = PaginationState()
)

data class OrderFilterState(
    val status: String? = null,
    val fromDate: Long? = null,
    val toDate: Long? = null
)
