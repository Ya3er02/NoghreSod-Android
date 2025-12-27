package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Cart
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * CartViewModel - مدیریت سبد خرید با Optimistic Updates
 * Features:
 * - Optimistic UI Updates
 * - Stock Availability Check
 * - Offline Support & Sync
 * - Quantity Management
 * - Backend Synchronization
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    // Cart State
    private val _cartState = MutableStateFlow<CartState>(CartState.Loading)
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    // Offline Queue for pending actions
    private val pendingActions = mutableListOf<CartAction>()

    // Connectivity observer
    private var connectivityObserver: Flow<Boolean>? = null

    init {
        loadCart()
        observeConnectivity()
    }

    /**
     * Load cart from repository
     */
    private fun loadCart() {
        viewModelScope.launch {
            _cartState.value = CartState.Loading

            cartRepository.getCart()
                .onSuccess { cart ->
                    _cartState.value = CartState.Success(cart)
                }
                .onFailure { error ->
                    _cartState.value = CartState.Error(
                        message = error.message ?: "Failed to load cart"
                    )
                }
        }
    }

    /**
     * Add item to cart with optimistic update
     */
    fun addToCart(product: Product, quantity: Int) {
        viewModelScope.launch {
            val currentState = _cartState.value as? CartState.Success ?: return@launch

            // Optimistic UI update
            val optimisticCart = currentState.cart.copy(
                items = currentState.cart.items + CartItem(
                    id = "${product.id}_${System.currentTimeMillis()}",
                    product = product,
                    quantity = quantity,
                    isOptimistic = true
                )
            )
            _cartState.value = CartState.Success(optimisticCart)

            // Backend call
            cartRepository.addItem(product.id, quantity)
                .onSuccess { updatedCart ->
                    _cartState.value = CartState.Success(updatedCart)
                }
                .onFailure { error ->
                    // Rollback on failure
                    _cartState.value = currentState
                    handleError(error)
                    // Queue for retry if offline
                    pendingActions.add(CartAction.Add(product.id, quantity))
                }
        }
    }

    /**
     * Update item quantity with stock check
     */
    fun updateQuantity(itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            val currentState = _cartState.value as? CartState.Success ?: return@launch
            val currentItem = currentState.cart.items.find { it.id == itemId } ?: return@launch

            // Check stock availability
            productRepository.checkStock(currentItem.product.id, newQuantity)
                .onSuccess { available ->
                    if (available) {
                        performOptimisticUpdate(itemId, newQuantity, currentState)
                    } else {
                        _cartState.value = CartState.Error(
                            message = "Insufficient stock available"
                        )
                    }
                }
                .onFailure { error ->
                    handleError(error)
                }
        }
    }

    /**
     * Perform optimistic update for quantity change
     */
    private suspend fun performOptimisticUpdate(
        itemId: String,
        newQuantity: Int,
        currentState: CartState.Success
    ) {
        val optimisticCart = currentState.cart.copy(
            items = currentState.cart.items.map { item ->
                if (item.id == itemId) {
                    item.copy(quantity = newQuantity, isOptimistic = true)
                } else {
                    item
                }
            }
        )
        _cartState.value = CartState.Success(optimisticCart)

        // Backend call
        cartRepository.updateQuantity(itemId, newQuantity)
            .onSuccess { updatedCart ->
                _cartState.value = CartState.Success(updatedCart)
            }
            .onFailure { error ->
                // Rollback on failure
                _cartState.value = currentState
                handleError(error)
                pendingActions.add(CartAction.Update(itemId, newQuantity))
            }
    }

    /**
     * Remove item from cart
     */
    fun removeItem(itemId: String) {
        viewModelScope.launch {
            val currentState = _cartState.value as? CartState.Success ?: return@launch

            // Optimistic removal
            val optimisticCart = currentState.cart.copy(
                items = currentState.cart.items.filter { it.id != itemId }
            )
            _cartState.value = CartState.Success(optimisticCart)

            // Backend call
            cartRepository.removeItem(itemId)
                .onSuccess { updatedCart ->
                    _cartState.value = CartState.Success(updatedCart)
                }
                .onFailure { error ->
                    // Rollback on failure
                    _cartState.value = currentState
                    handleError(error)
                    pendingActions.add(CartAction.Remove(itemId))
                }
        }
    }

    /**
     * Clear entire cart
     */
    fun clearCart() {
        viewModelScope.launch {
            _cartState.value = CartState.Loading
            cartRepository.clearCart()
                .onSuccess {
                    _cartState.value = CartState.Success(Cart(items = emptyList()))
                    pendingActions.clear()
                }
                .onFailure { error ->
                    _cartState.value = CartState.Error(
                        message = error.message ?: "Failed to clear cart"
                    )
                }
        }
    }

    /**
     * Observe connectivity and sync pending actions
     */
    private fun observeConnectivity() {
        // This would observe ConnectivityManager in real implementation
        // For now, we'll sync manually when needed
    }

    /**
     * Sync pending offline operations
     */
    fun syncPendingOperations() {
        if (pendingActions.isEmpty()) return

        viewModelScope.launch {
            pendingActions.forEach { action ->
                when (action) {
                    is CartAction.Add -> {
                        cartRepository.addItem(action.productId, action.quantity)
                    }
                    is CartAction.Remove -> {
                        cartRepository.removeItem(action.itemId)
                    }
                    is CartAction.Update -> {
                        cartRepository.updateQuantity(action.itemId, action.quantity)
                    }
                }
            }
            pendingActions.clear()
            loadCart() // Refresh cart after sync
        }
    }

    /**
     * Handle errors
     */
    private fun handleError(error: Exception) {
        _cartState.value = CartState.Error(
            message = error.message ?: "Unknown error occurred"
        )
    }
}

// Cart State
sealed interface CartState {
    data object Loading : CartState
    data class Success(val cart: Cart) : CartState
    data class Error(val message: String) : CartState
}

// Cart Actions for offline queue
sealed interface CartAction {
    data class Add(val productId: String, val quantity: Int) : CartAction
    data class Remove(val itemId: String) : CartAction
    data class Update(val itemId: String, val quantity: Int) : CartAction
}
