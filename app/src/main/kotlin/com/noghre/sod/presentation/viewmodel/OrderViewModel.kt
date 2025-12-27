package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.error.GlobalExceptionHandler
import com.noghre.sod.core.util.onError
import com.noghre.sod.core.util.onSuccess
import com.noghre.sod.domain.model.Order
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
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState<List<Order>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Order>>> = _uiState.asStateFlow()
    
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()
    
    init {
        loadOrders()
    }
    
    fun loadOrders(page: Int = 1) {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = UiState.Loading
            _currentPage.value = page
            Timber.d("Loading orders for page: $page")
            
            orderRepository.getUserOrders(page)
                .onSuccess { orders ->
                    Timber.d("Orders loaded: ${orders.size}")
                    _uiState.value = if (orders.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(orders)
                    }
                }
                .onError { error ->
                    Timber.e("Failed to load orders: ${error.message}")
                    _uiState.value = UiState.Error(error)
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onOrderClick(orderId: String) {
        viewModelScope.launch {
            Timber.d("Order clicked: $orderId")
            _events.send(UiEvent.Navigate("order_detail/$orderId"))
        }
    }
    
    fun onCancelOrder(orderId: String, reason: String) {
        if (reason.isBlank()) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowToast("لطفاً لال لغو را وارد کنید"))
            }
            return
        }
        
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Cancelling order: $orderId")
            
            orderRepository.cancelOrder(orderId, reason)
                .onSuccess {
                    Timber.d("Order cancelled successfully")
                    _events.send(UiEvent.ShowToast("سفارش لغو شد"))
                    loadOrders()
                }
                .onError { error ->
                    Timber.e("Failed to cancel order: ${error.message}")
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onRetryClick() {
        Timber.d("Retry clicked")
        loadOrders()
    }
    
    fun onNextPage() {
        Timber.d("Loading next page")
        loadOrders(_currentPage.value + 1)
    }
    
    fun onPreviousPage() {
        if (_currentPage.value > 1) {
            Timber.d("Loading previous page")
            loadOrders(_currentPage.value - 1)
        }
    }
}
