package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.model.OrderStatus
import com.noghre.sod.domain.usecase.order.CreateOrderUseCase
import com.noghre.sod.domain.usecase.order.GetOrdersUseCase
import com.noghre.sod.domain.usecase.order.TrackOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderUiState(
    val orders: List<Order> = emptyList(),
    val currentOrder: Order? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 1
)

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val trackOrderUseCase: TrackOrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    fun loadOrders(status: OrderStatus? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val orders = getOrdersUseCase(
                    GetOrdersUseCase.Params(page = _uiState.value.page, status = status)
                )
                _uiState.value = _uiState.value.copy(
                    orders = orders,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun trackOrder(orderId: String) {
        viewModelScope.launch {
            try {
                trackOrderUseCase(TrackOrderUseCase.Params(orderId))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
