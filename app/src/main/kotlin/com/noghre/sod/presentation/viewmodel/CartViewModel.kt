package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.error.GlobalExceptionHandler
import com.noghre.sod.core.util.onError
import com.noghre.sod.core.util.onSuccess
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState<List<CartItem>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<CartItem>>> = _uiState.asStateFlow()
    
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()
    
    init {
        loadCartItems()
    }
    
    fun loadCartItems() {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = UiState.Loading
            Timber.d("Loading cart items")
            
            cartRepository.getCartItems()
                .onSuccess { items ->
                    Timber.d("Cart items loaded: ${items.size}")
                    _uiState.value = if (items.isEmpty()) {
                        _totalPrice.value = 0.0
                        UiState.Empty
                    } else {
                        _totalPrice.value = items.sumOf { it.product.price * it.quantity }
                        UiState.Success(items)
                    }
                }
                .onError { error ->
                    Timber.e("Failed to load cart: ${error.message}")
                    _uiState.value = UiState.Error(error)
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onUpdateQuantity(itemId: String, newQuantity: Int) {
        if (newQuantity < 1) return
        
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Updating cart item quantity: $itemId -> $newQuantity")
            
            cartRepository.updateCartItem(itemId, newQuantity)
                .onSuccess {
                    Timber.d("Quantity updated successfully")
                    loadCartItems() // Refresh
                }
                .onError { error ->
                    Timber.e("Failed to update quantity: ${error.message}")
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onRemoveItem(itemId: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Removing cart item: $itemId")
            
            cartRepository.removeFromCart(itemId)
                .onSuccess {
                    Timber.d("Item removed successfully")
                    _events.send(UiEvent.ShowToast("از سبد خرید حذف شد"))
                    loadCartItems() // Refresh
                }
                .onError { error ->
                    Timber.e("Failed to remove item: ${error.message}")
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onClearCart() {
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Clearing entire cart")
            
            cartRepository.clearCart()
                .onSuccess {
                    Timber.d("Cart cleared successfully")
                    _events.send(UiEvent.ShowToast("سبد خرید خالی شد"))
                    loadCartItems() // Refresh
                }
                .onError { error ->
                    Timber.e("Failed to clear cart: ${error.message}")
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onCheckoutClick() {
        viewModelScope.launch {
            Timber.d("Navigating to checkout")
            _events.send(UiEvent.Navigate("checkout"))
        }
    }
    
    fun onRetryClick() {
        Timber.d("Retry clicked")
        loadCartItems()
    }
}
