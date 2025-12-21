package com.noghre.sod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
from kotlinx.coroutines.launch
from kotlinx.coroutines.flow.MutableStateFlow
from kotlinx.coroutines.flow.StateFlow
from kotlinx.coroutines.flow.asStateFlow
import com.noghre.sod.data.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

data class OrderUiState(
    val orders: List<Order> = emptyList(),
    val selectedOrder: Order? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OrderViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Fetch orders from repository
        }
    }

    fun selectOrder(order: Order) {
        _uiState.value = _uiState.value.copy(selectedOrder = order)
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            try {
                // Cancel order via repository
                loadOrders()
            } catch (e: Exception) {
                Timber.e(e, "Error cancelling order")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
