package com.noghre.sod.presentation.screens.cart

import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.model.Result
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.presentation.base.BaseViewModel
import com.noghre.sod.presentation.base.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

/**
 * State for CartScreen.
 */
data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discountPercent: Double = 0.0,
    val discountAmount: Double = 0.0,
    val taxAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val itemCount: Int = 0
)

/**
 * ViewModel for Cart screen.
 * 
* Manages shopping cart operations and calculations.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : BaseViewModel<CartUiState>() {

    private val _cartUiState = MutableStateFlow(CartUiState())
    val cartUiState = _cartUiState.asStateFlow()

    init {
        loadCart()
    }

    /**
     * Load cart items.
     */
    fun loadCart() {
        launchAsync(
            block = {
                cartRepository.getCartItems().collect { result ->
                    when (result) {
                        is Result.Success -> {
                            updateCartState(result.data)
                        }
                        is Result.Error -> {
                            setError(result.message)
                            emitEvent(UiEvent.Error(result.exception))
                        }
                    }
                }
            }
        )
    }

    /**
     * Add item to cart.
     */
    fun addToCart(item: CartItem) {
        launchAsync(
            block = {
                when (val result = cartRepository.addToCart(item)) {
                    is Result.Success -> {
                        emitEvent(UiEvent.ShowToast("اضافه شد به سبد خریدتان"))
                        loadCart()
                    }
                    is Result.Error -> setError(result.message)
                }
            }
        )
    }

    /**
     * Remove item from cart.
     */
    fun removeFromCart(productId: String) {
        launchAsync(
            block = {
                when (val result = cartRepository.removeFromCart(productId)) {
                    is Result.Success -> {
                        emitEvent(UiEvent.ShowToast("حذف شد از سبد خریدتان"))
                        loadCart()
                    }
                    is Result.Error -> setError(result.message)
                }
            }
        )
    }

    /**
     * Update item quantity.
     */
    fun updateQuantity(productId: String, quantity: Int) {
        launchAsync(
            block = {
                when (val result = cartRepository.updateQuantity(productId, quantity)) {
                    is Result.Success -> loadCart()
                    is Result.Error -> setError(result.message)
                }
            }
        )
    }

    /**
     * Apply discount code.
     */
    fun applyDiscount(discountPercent: Double) {
        val state = _cartUiState.value
        val newSubtotal = state.subtotal
        val discountAmount = newSubtotal * (discountPercent / 100)
        val afterDiscount = newSubtotal - discountAmount
        val taxAmount = afterDiscount * 0.09 // 9% VAT
        val total = afterDiscount + taxAmount
        
        _cartUiState.value = state.copy(
            discountPercent = discountPercent,
            discountAmount = discountAmount,
            taxAmount = taxAmount,
            totalAmount = total
        )
    }

    /**
     * Clear discount.
     */
    fun clearDiscount() {
        applyDiscount(0.0)
    }

    /**
     * Clear entire cart.
     */
    fun clearCart() {
        launchAsync(
            block = {
                when (val result = cartRepository.clearCart()) {
                    is Result.Success -> {
                        _cartUiState.value = CartUiState()
                        emitEvent(UiEvent.ShowToast("سبد خریدتان خالی شد"))
                    }
                    is Result.Error -> setError(result.message)
                }
            }
        )
    }

    /**
     * Proceed to checkout.
     */
    fun proceedToCheckout() {
        val state = _cartUiState.value
        if (state.items.isEmpty()) {
            emitEvent(UiEvent.ShowToast("سبد خریدتان خالی است"))
            return
        }
        emitEvent(UiEvent.Navigate("checkout"))
    }

    /**
     * Update cart state from items.
     */
    private fun updateCartState(items: List<CartItem>) {
        val subtotal = items.sumOf { it.price * it.quantity }
        val state = _cartUiState.value
        val discountAmount = subtotal * (state.discountPercent / 100)
        val afterDiscount = subtotal - discountAmount
        val taxAmount = afterDiscount * 0.09
        val total = afterDiscount + taxAmount
        
        _cartUiState.value = state.copy(
            items = items,
            subtotal = subtotal,
            discountAmount = discountAmount,
            taxAmount = taxAmount,
            totalAmount = total,
            itemCount = items.size
        )
    }
}
