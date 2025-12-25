package com.noghre.sod.presentation.viewmodel

import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.User

/**
 * Product UI State sealed class for type-safe state management.
 */
sealed class ProductUiState {
    object Loading : ProductUiState()
    object Empty : ProductUiState()
    data class Success(val products: List<Product>) : ProductUiState()
    data class Error(val message: String, val throwable: Throwable? = null) : ProductUiState()
    object RefreshingError : ProductUiState()
}

/**
 * Product Detail UI State.
 */
sealed class ProductDetailUiState {
    object Loading : ProductDetailUiState()
    data class Success(val product: Product) : ProductDetailUiState()
    data class Error(val message: String, val throwable: Throwable? = null) : ProductDetailUiState()
}

/**
 * Cart UI State sealed class.
 */
sealed class CartUiState {
    object Loading : CartUiState()
    object Empty : CartUiState()
    data class Success(
        val items: List<Product>,
        val total: Double,
        val itemCount: Int
    ) : CartUiState()
    data class Error(val message: String, val throwable: Throwable? = null) : CartUiState()
}

/**
 * Order UI State sealed class.
 */
sealed class OrderUiState {
    object Loading : OrderUiState()
    object Empty : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class Error(val message: String, val throwable: Throwable? = null) : OrderUiState()
}

/**
 * Order Detail UI State.
 */
sealed class OrderDetailUiState {
    object Loading : OrderDetailUiState()
    data class Success(val order: Order) : OrderDetailUiState()
    data class Error(val message: String, val throwable: Throwable? = null) : OrderDetailUiState()
}

/**
 * User Profile UI State.
 */
sealed class UserProfileUiState {
    object Loading : UserProfileUiState()
    data class Success(val user: User) : UserProfileUiState()
    data class Error(val message: String, val throwable: Throwable? = null) : UserProfileUiState()
}

/**
 * Authentication UI State.
 */
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String, val throwable: Throwable? = null) : AuthUiState()
}

/**
 * Checkout UI State.
 */
sealed class CheckoutUiState {
    object Idle : CheckoutUiState()
    object Loading : CheckoutUiState()
    object ProcessingPayment : CheckoutUiState()
    object Success : CheckoutUiState()
    data class Error(val message: String, val throwable: Throwable? = null) : CheckoutUiState()
}

/**
 * Generic Result wrapper for operations.
 */
sealed class UiResult<out T> {
    data class Success<T>(val data: T) : UiResult<T>()
    data class Error<T>(val message: String, val throwable: Throwable? = null) : UiResult<T>()
    object Loading : UiResult<Nothing>()
}

/**
 * Extension function to map UiResult to UiState.
 */
fun <T> UiResult<T>.toUiState(): UiState {
    return when (this) {
        is UiResult.Loading -> UiState.Loading
        is UiResult.Success<*> -> UiState.Success
        is UiResult.Error -> UiState.Error(this.message, this.throwable)
    }
}

/**
 * Generic UI State for reusable components.
 */
sealed class UiState {
    object Loading : UiState()
    object Empty : UiState()
    object Success : UiState()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState()
}
