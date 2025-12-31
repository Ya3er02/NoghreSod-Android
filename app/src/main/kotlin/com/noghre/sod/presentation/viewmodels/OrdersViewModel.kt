package com.noghre.sod.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.repository.OrderRepository
import com.noghre.sod.presentation.screens.orders.OrdersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Orders Screen.
 * 
 * Handles order history, status filtering,
 * order details, and order management.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _ordersUiState = MutableStateFlow(OrdersUiState())
    val ordersUiState: StateFlow<OrdersUiState> = _ordersUiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var allOrders = emptyList<Order>()

    init {
        loadOrders()
    }

    /**
     * Load all user orders.
     */
    private fun loadOrders() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Timber.d("Loading orders")

                val orders = orderRepository.getOrders()

                allOrders = orders.map { order ->
                    Order(
                        id = order.id,
                        orderNumber = order.orderNumber,
                        createdDate = order.createdDate,
                        status = order.status,
                        total = "${order.totalAmount} ریال",
                        itemCount = order.itemCount,
                        estimatedDelivery = order.estimatedDelivery
                    )
                }

                _ordersUiState.value = _ordersUiState.value.copy(
                    orders = allOrders
                )

                Timber.d("Orders loaded: ${allOrders.size} items")
            } catch (e: Exception) {
                Timber.e(e, "Error loading orders")
                _error.value = e.localizedMessage ?: "خطای نامشخص رخ داد"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Filter orders by status.
     */
    fun filterByStatus(status: String) {
        viewModelScope.launch {
            try {
                Timber.d("Filtering orders by status: $status")

                val filteredOrders = if (status.isBlank()) {
                    allOrders
                } else {
                    allOrders.filter { it.status == status }
                }

                _ordersUiState.value = _ordersUiState.value.copy(
                    orders = filteredOrders
                )

                Timber.d("Filtered orders: ${filteredOrders.size} items")
            } catch (e: Exception) {
                Timber.e(e, "Error filtering orders")
                _error.value = e.localizedMessage ?: "خطا در فیلتر"
            }
        }
    }

    /**
     * Get order details.
     */
    fun getOrderDetails(orderId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Timber.d("Loading order details: $orderId")

                val order = orderRepository.getOrderById(orderId)
                // TODO: Handle order details

                Timber.d("Order details loaded")
            } catch (e: Exception) {
                Timber.e(e, "Error loading order details")
                _error.value = e.localizedMessage ?: "خطای نامشخص رخ داد"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cancel order.
     */
    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Timber.d("Cancelling order: $orderId")

                orderRepository.cancelOrder(orderId)

                // Reload orders
                loadOrders()

                Timber.d("Order cancelled successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error cancelling order")
                _error.value = e.localizedMessage ?: "خطا در لغو"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Reorder from previous order.
     */
    fun reorder(orderId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Timber.d("Reordering from order: $orderId")

                orderRepository.reorder(orderId)

                Timber.d("Order reordered successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error reordering")
                _error.value = e.localizedMessage ?: "خطا در ثبت سفارش"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _error.value = null
    }
}

/**
 * Order data class for UI.
 */
data class Order(
    val id: String,
    val orderNumber: String,
    val createdDate: String,
    val status: String,
    val total: String,
    val itemCount: Int,
    val estimatedDelivery: String? = null
)
