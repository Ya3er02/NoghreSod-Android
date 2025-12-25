package com.noghre.sod.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.usecases.*
import com.noghre.sod.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

// ==================== CART VIEW MODEL ====================

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val calculateTotalUseCase: CalculateTotalUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()
    
    init {
        loadCart()
    }
    
    /**
     * Load cart items
     */
    fun loadCart() {
        viewModelScope.launch {
            _uiState.value = CartUiState.Loading
            try {
                when (val result = getCartUseCase()) {
                    is Result.Success -> {
                        val items = result.data
                        if (items.isEmpty()) {
                            _uiState.value = CartUiState.Empty
                        } else {
                            val cartData = calculateCartData(items)
                            _uiState.value = CartUiState.Success(cartData)
                        }
                    }
                    is Result.Error -> 
                        _uiState.value = CartUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _uiState.value = CartUiState.Error(e)
            }
        }
    }
    
    /**
     * Remove item from cart
     */
    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            try {
                removeFromCartUseCase(productId)
                loadCart() // Reload cart
            } catch (e: Exception) {
                _uiState.value = CartUiState.Error(e)
            }
        }
    }
    
    /**
     * Update cart item quantity
     */
    fun updateQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                updateCartItemUseCase(productId, quantity)
                loadCart() // Reload cart
            } catch (e: Exception) {
                _uiState.value = CartUiState.Error(e)
            }
        }
    }
    
    /**
     * Calculate cart totals
     */
    private suspend fun calculateCartData(
        items: List<com.noghre.sod.domain.entities.CartItem>
    ): CartData {
        val subtotal = items.sumOf { it.price.toLong() * it.quantity }
        val discount = (subtotal * 0.1).toLong() // 10% discount
        val tax = ((subtotal - discount) * 0.09).toLong() // 9% tax
        val total = subtotal - discount + tax
        
        return CartData(
            items = items,
            subtotal = subtotal,
            discount = discount,
            tax = tax,
            total = total
        )
    }
}

// ==================== CHECKOUT VIEW MODEL ====================

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val getCheckoutDataUseCase: GetCheckoutDataUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Loading)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()
    
    init {
        loadCheckoutData()
    }
    
    /**
     * Load checkout data
     */
    fun loadCheckoutData() {
        viewModelScope.launch {
            _uiState.value = CheckoutUiState.Loading
            try {
                when (val result = getCheckoutDataUseCase()) {
                    is Result.Success -> {
                        val data = result.data
                        _uiState.value = CheckoutUiState.Success(
                            CheckoutData(
                                shippingAddress = data.address,
                                subtotal = data.subtotal,
                                tax = data.tax,
                                total = data.total
                            )
                        )
                    }
                    is Result.Error -> 
                        _uiState.value = CheckoutUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _uiState.value = CheckoutUiState.Error(e)
            }
        }
    }
    
    /**
     * Place order
     */
    fun placeOrder(paymentMethod: String) {
        viewModelScope.launch {
            try {
                when (val result = placeOrderUseCase(paymentMethod)) {
                    is Result.Success -> {
                        _uiState.value = CheckoutUiState.OrderPlaced(result.data.id)
                    }
                    is Result.Error -> 
                        _uiState.value = CheckoutUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _uiState.value = CheckoutUiState.Error(e)
            }
        }
    }
}

// ==================== ORDERS VIEW MODEL ====================

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<OrdersUiState>(OrdersUiState.Loading)
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()
    
    init {
        loadOrders()
    }
    
    /**
     * Load user orders
     */
    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = OrdersUiState.Loading
            try {
                when (val result = getOrdersUseCase()) {
                    is Result.Success -> {
                        val orders = result.data
                        _uiState.value = if (orders.isEmpty()) {
                            OrdersUiState.Empty
                        } else {
                            OrdersUiState.Success(orders)
                        }
                    }
                    is Result.Error -> 
                        _uiState.value = OrdersUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _uiState.value = OrdersUiState.Error(e)
            }
        }
    }
}
