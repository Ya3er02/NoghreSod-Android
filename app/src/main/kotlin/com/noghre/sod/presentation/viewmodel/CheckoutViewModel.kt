package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.error.GlobalExceptionHandler
import com.noghre.sod.core.util.onError
import com.noghre.sod.core.util.onSuccess
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.domain.repository.OrderRepository
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
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    private val _cartState = MutableStateFlow<UiState<List<CartItem>>>(UiState.Idle)
    val cartState: StateFlow<UiState<List<CartItem>>> = _cartState.asStateFlow()
    
    private val _checkoutState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val checkoutState: StateFlow<UiState<String>> = _checkoutState.asStateFlow()
    
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()
    
    init {
        loadCartItems()
    }
    
    fun loadCartItems() {
        viewModelScope.launch(exceptionHandler.handler) {
            _cartState.value = UiState.Loading
            Timber.d("Loading cart items for checkout")
            
            cartRepository.getCartItems()
                .onSuccess { items ->
                    Timber.d("Cart items loaded: ${items.size}")
                    _totalPrice.value = items.sumOf { it.product.price * it.quantity }
                    _cartState.value = if (items.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(items)
                    }
                }
                .onError { error ->
                    Timber.e("Failed to load cart items: ${error.message}")
                    _cartState.value = UiState.Error(error)
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onPlaceOrder(shippingAddress: String, paymentMethod: String) {
        if (shippingAddress.isBlank() || paymentMethod.isBlank()) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowToast("لطفاً تمام اطلاعات را پر کنید"))
            }
            return
        }
        
        viewModelScope.launch(exceptionHandler.handler) {
            _checkoutState.value = UiState.Loading
            Timber.d("Placing order with address: $shippingAddress, payment: $paymentMethod")
            
            // Create order details
            val orderDetails = mapOf(
                "shipping_address" to shippingAddress,
                "payment_method" to paymentMethod,
                "total_price" to _totalPrice.value.toString()
            )
            
            orderRepository.createOrder(orderDetails)
                .onSuccess { orderId ->
                    Timber.d("Order created successfully: $orderId")
                    _checkoutState.value = UiState.Success(orderId)
                    _events.send(UiEvent.ShowToast("سفارش ثبت شد"))
                    _events.send(UiEvent.Navigate("order_confirmation/$orderId"))
                    // Clear cart after successful order
                    cartRepository.clearCart()
                }
                .onError { error ->
                    Timber.e("Failed to place order: ${error.message}")
                    _checkoutState.value = UiState.Error(error)
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onRetryClick() {
        Timber.d("Retry clicked")
        loadCartItems()
    }
}
