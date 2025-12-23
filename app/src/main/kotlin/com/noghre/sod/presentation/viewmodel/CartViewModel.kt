package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.usecase.GetCartUseCase
import com.noghre.sod.domain.usecase.AddToCartUseCase
import com.noghre.sod.domain.usecase.RemoveFromCartUseCase
import com.noghre.sod.domain.usecase.UpdateCartItemUseCase
import com.noghre.sod.domain.usecase.ClearCartUseCase
import com.noghre.sod.presentation.cart.CartItemUi
import com.noghre.sod.presentation.cart.CartUiState
import com.noghre.sod.presentation.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for shopping cart screen.
 * Manages cart items, totals, and checkout flow.
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadCart()
    }

    /**
     * Load cart items
     */
    fun loadCart() {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                val cart = getCartUseCase()
                val items = cart.items.map { cartItem ->
                    CartItemUi(
                        id = cartItem.id,
                        product = cartItem.product,
                        quantity = cartItem.quantity,
                        subtotal = cartItem.subtotal
                    )
                }
                updateState {
                    copy(
                        items = items,
                        totalPrice = cart.totalPrice,
                        discountAmount = cart.discountAmount,
                        finalPrice = cart.finalPrice,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading cart")
                updateState {
                    copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load cart"
                    )
                }
            }
        }
    }

    /**
     * Update item quantity
     *
     * @param itemId Cart item ID
     * @param quantity New quantity
     */
    fun updateQuantity(itemId: String, quantity: Int) {
        if (quantity < 1) {
            removeItem(itemId)
            return
        }

        viewModelScope.launch {
            try {
                updateState {
                    copy(
                        items = items.map {
                            if (it.id == itemId) it.copy(isUpdating = true) else it
                        }
                    )
                }

                updateCartItemUseCase(itemId, quantity)
                loadCart()
            } catch (e: Exception) {
                Timber.e(e, "Error updating quantity")
                loadCart()
                _events.send(UiEvent.ShowSnackbar("Failed to update quantity"))
            }
        }
    }

    /**
     * Remove item from cart
     *
     * @param itemId Cart item ID
     */
    fun removeItem(itemId: String) {
        viewModelScope.launch {
            try {
                removeFromCartUseCase(itemId)
                updateState {
                    copy(items = items.filter { it.id != itemId })
                }
                calculateTotals()
                _events.send(UiEvent.ShowSnackbar("Item removed"))
            } catch (e: Exception) {
                Timber.e(e, "Error removing item")
                _events.send(UiEvent.ShowSnackbar("Failed to remove item"))
            }
        }
    }

    /**
     * Clear entire cart
     */
    fun clearCart() {
        viewModelScope.launch {
            try {
                updateState { copy(isClearing = true) }
                clearCartUseCase()
                updateState {
                    copy(
                        items = emptyList(),
                        totalPrice = 0.0,
                        discountAmount = 0.0,
                        finalPrice = 0.0,
                        isClearing = false
                    )
                }
                _events.send(UiEvent.ShowSnackbar("Cart cleared"))
            } catch (e: Exception) {
                Timber.e(e, "Error clearing cart")
                updateState { copy(isClearing = false) }
                _events.send(UiEvent.ShowSnackbar("Failed to clear cart"))
            }
        }
    }

    /**
     * Proceed to checkout with validation
     */
    fun proceedToCheckout() {
        if (uiState.value.items.isEmpty()) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowSnackbar("Cart is empty"))
            }
            return
        }

        viewModelScope.launch {
            _events.send(UiEvent.Navigate("checkout"))
        }
    }

    /**
     * Calculate and update totals
     */
    private fun calculateTotals() {
        val items = uiState.value.items
        val total = items.sumOf { it.subtotal }
        updateState {
            copy(
                totalPrice = total,
                finalPrice = total - discountAmount
            )
        }
    }

    /**
     * Retry loading cart
     */
    fun retry() {
        loadCart()
    }

    private fun updateState(block: CartUiState.() -> CartUiState) {
        _uiState.update(block)
    }
}
