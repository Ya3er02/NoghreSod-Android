package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.exception.GlobalExceptionHandler
import com.noghre.sod.core.util.UiEvent
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.usecase.cart.CalculateCartTotalUseCase
import com.noghre.sod.domain.usecase.order.CancelOrderUseCase
import com.noghre.sod.domain.usecase.order.CreateOrderUseCase
import com.noghre.sod.domain.usecase.order.GetOrderByIdUseCase
import com.noghre.sod.domain.usecase.order.GetOrdersUseCase
import com.noghre.sod.domain.usecase.order.GetOrderTrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class OrderUiState {
    object Idle : OrderUiState()
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class OrderCreated(val order: Order) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}

data class OrderTrackingUiModel(
    val orderId: String,
    val trackingNumber: String,
    val status: String,
    val location: String,
    val estimatedDaysLeft: Int
)

@HiltViewModel
class OrderViewModel @Inject constructor(
    // ✅ Order UseCases
    private val createOrderUseCase: CreateOrderUseCase,
    private val getOrdersUseCase: GetOrdersUseCase,
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val getOrderTrackingUseCase: GetOrderTrackingUseCase,
    
    // ✅ Cart UseCase for total calculation
    private val calculateCartTotalUseCase: CalculateCartTotalUseCase,
    
    // Infrastructure
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Idle)
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    
    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()
    
    private val _orderTracking = MutableStateFlow<OrderTrackingUiModel?>(null)
    val orderTracking: StateFlow<OrderTrackingUiModel?> = _orderTracking.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    
    init {
        loadOrders()
    }
    
    fun loadOrders() {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = OrderUiState.Loading
            
            val result = getOrdersUseCase(Unit)
            
            if (result.isSuccess) {
                val orders = result.getOrNull() ?: emptyList()
                _orders.value = orders
                _uiState.value = OrderUiState.Success(orders)
                Timber.d("Loaded ${orders.size} orders")
            } else {
                val error = result.exceptionOrNull()
                _uiState.value = OrderUiState.Error(error?.message ?: "Failed to load orders")
            }
        }
    }
    
    fun getOrderDetails(orderId: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = OrderUiState.Loading
            
            val result = getOrderByIdUseCase(orderId)
            
            if (result.isSuccess) {
                val order = result.getOrNull()
                _selectedOrder.value = order
                _uiState.value = OrderUiState.Success(listOf(order!!))
                Timber.d("Loaded order: $orderId")
            } else {
                val error = result.exceptionOrNull()
                _uiState.value = OrderUiState.Error(error?.message ?: "Failed to load order")
            }
        }
    }
    
    fun createOrder(
        cartId: String,
        shippingAddressId: String,
        shippingMethodId: String,
        paymentMethodId: String,
        insuranceId: String? = null,
        couponCode: String? = null
    ) {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = OrderUiState.Loading
            
            val params = CreateOrderUseCase.Params(
                cartId = cartId,
                shippingAddressId = shippingAddressId,
                shippingMethodId = shippingMethodId,
                paymentMethodId = paymentMethodId,
                insuranceId = insuranceId,
                couponCode = couponCode
            )
            val result = createOrderUseCase(params)
            
            if (result.isSuccess) {
                val order = result.getOrNull()
                _selectedOrder.value = order
                _uiState.value = OrderUiState.OrderCreated(order!!)
                _uiEvent.emit(UiEvent.ShowToast("Order created successfully"))
                Timber.d("Order created: ${order.id}")
                loadOrders()
            } else {
                val error = result.exceptionOrNull()
                _uiState.value = OrderUiState.Error(error?.message ?: "Failed to create order")
            }
        }
    }
    
    fun cancelOrder(orderId: String, reason: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            val params = CancelOrderUseCase.Params(
                orderId = orderId,
                reason = reason
            )
            val result = cancelOrderUseCase(params)
            
            if (result.isSuccess) {
                val cancelledOrder = result.getOrNull()
                _selectedOrder.value = cancelledOrder
                _uiEvent.emit(UiEvent.ShowToast("Order cancelled"))
                Timber.d("Order cancelled: $orderId")
                loadOrders()
            } else {
                val error = result.exceptionOrNull()
                _uiEvent.emit(UiEvent.ShowError(error?.message ?: "Failed to cancel order"))
            }
        }
    }
    
    fun getOrderTracking(orderId: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            val result = getOrderTrackingUseCase(orderId)
            
            if (result.isSuccess) {
                val tracking = result.getOrNull()
                _orderTracking.value = OrderTrackingUiModel(
                    orderId = tracking?.orderId ?: "",
                    trackingNumber = tracking?.trackingNumber ?: "",
                    status = tracking?.status ?: "",
                    location = tracking?.location ?: "",
                    estimatedDaysLeft = tracking?.estimatedDaysLeft ?: 0
                )
                Timber.d("Tracking info loaded for $orderId")
            }
        }
    }
}
