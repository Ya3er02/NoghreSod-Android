package com.noghre.sod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import com.noghre.sod.data.repository.CartRepository
import com.noghre.sod.data.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * UI state representation for cart screen
 * Encapsulates all cart-related UI concerns
 */
data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val itemCount: Int = 0,
    val isLoading: Boolean = false,
    val error: CartError? = null
)

/**
 * Sealed error type for cart operations
 * Provides type-safe error handling instead of raw strings
 */
sealed class CartError(open val message: String) {
    data class FailedToLoad(override val message: String = "خطا در بارگذاری سبد خریدشما") : CartError(message)
    data class FailedToAdd(override val message: String = "خطا در افزودن محصول به سبد") : CartError(message)
    data class FailedToRemove(override val message: String = "خطا در حذف محصول از سبد") : CartError(message)
    data class FailedToClear(override val message: String = "خطا در خالی کردن سبد") : CartError(message)
    data class Validation(override val message: String) : CartError(message)
}

/**
 * CartViewModel for managing shopping cart state
 * 
 * Responsibilities:
 * - Load cart items and count from repository
 * - Handle add/remove/clear operations with optimistic updates
 * - Manage loading states and error propagation
 * - Provide single source of truth for UI via uiState Flow
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    /**
     * Load cart items and count by combining flows
     * Uses Flow.combine to ensure atomic state updates and prevent race conditions
     */
    private fun loadCart() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // Combine items and count flows into single state update
                // This prevents race conditions where items and count update independently
                combine(
                    cartRepository.getCartItems(),
                    cartRepository.getCartItemCount()
                ) { items, count ->
                    CartUiState(
                        items = items,
                        itemCount = count,
                        isLoading = false,
                        error = null
                    )
                }.collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading cart")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = CartError.FailedToLoad()
                )
            }
        }
    }

    /**
     * Add item to cart with input validation and optimistic update
     * @param productId ID of product to add (must not be blank)
     * @param quantity Quantity to add (must be > 0)
     */
    fun addToCart(productId: String, quantity: Int) {
        // Validate input
        if (productId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = CartError.Validation("شناسه محصول معتبر نیست")
            )
            return
        }
        if (quantity <= 0) {
            _uiState.value = _uiState.value.copy(
                error = CartError.Validation("تعداد باید بیشتر از صفر باشد")
            )
            return
        }

        viewModelScope.launch {
            try {
                // Optimistic update: clear error on attempt
                _uiState.value = _uiState.value.copy(error = null)
                
                cartRepository.addToCart(productId, quantity)
                Timber.d("Product $productId added to cart with quantity $quantity")
            } catch (e: Exception) {
                Timber.e(e, "Error adding to cart: $productId")
                _uiState.value = _uiState.value.copy(
                    error = CartError.FailedToAdd()
                )
            }
        }
    }

    /**
     * Remove item from cart
     * @param productId ID of product to remove (must not be blank)
     */
    fun removeFromCart(productId: String) {
        if (productId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = CartError.Validation("شناسه محصول معتبر نیست")
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(error = null)
                
                cartRepository.removeFromCart(productId)
                Timber.d("Product $productId removed from cart")
            } catch (e: Exception) {
                Timber.e(e, "Error removing from cart: $productId")
                _uiState.value = _uiState.value.copy(
                    error = CartError.FailedToRemove()
                )
            }
        }
    }

    /**
     * Clear entire cart
     */
    fun clearCart() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(error = null)
                
                cartRepository.clearCart()
                Timber.i("Cart cleared")
            } catch (e: Exception) {
                Timber.e(e, "Error clearing cart")
                _uiState.value = _uiState.value.copy(
                    error = CartError.FailedToClear()
                )
            }
        }
    }

    /**
     * Clear error state (called by UI when dismissing error dialog)
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
