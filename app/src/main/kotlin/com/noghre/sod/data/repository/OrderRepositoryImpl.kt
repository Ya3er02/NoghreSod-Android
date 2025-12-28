package com.noghre.sod.data.repository

import com.noghre.sod.core.error.AppException
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.local.OrderDao
import com.noghre.sod.data.remote.OrderApi
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderApi: OrderApi,
    private val orderDao: OrderDao
) : OrderRepository {
    
    override suspend fun getUserOrders(page: Int): Result<List<Order>> = try {
        Timber.d("Fetching user orders, page: $page")
        val response = orderApi.getUserOrders(page)
        
        // Save to local database
        orderDao.insertAll(response.orders)
        Timber.d("Orders saved to local DB: ${response.orders.size}")
        
        Result.Success(response.orders)
    } catch (e: Exception) {
        Timber.e("Error fetching orders: ${e.message}")
        // Fallback to local data
        try {
            val localOrders = orderDao.getAllOrders()
            if (localOrders.isNotEmpty()) {
                Timber.d("Returning local orders: ${localOrders.size}")
                Result.Success(localOrders)
            } else {
                Result.Error(AppException.NetworkException("No orders found"))
            }
        } catch (ex: Exception) {
            Timber.e("Error loading local orders: ${ex.message}")
            Result.Error(AppException.DatabaseException(ex.message ?: "Unknown error"))
        }
    }
    
    override suspend fun getOrderById(id: String): Result<Order> = try {
        Timber.d("Fetching order by ID: $id")
        val response = orderApi.getOrderById(id)
        
        // Save to local database
        orderDao.insert(response)
        Timber.d("Order saved to local DB: ${response.id}")
        
        Result.Success(response)
    } catch (e: Exception) {
        Timber.e("Error fetching order: ${e.message}")
        // Fallback to local data
        try {
            val localOrder = orderDao.getOrderById(id)
            if (localOrder != null) {
                Timber.d("Returning local order: ${localOrder.id}")
                Result.Success(localOrder)
            } else {
                Result.Error(AppException.NetworkException("Order not found"))
            }
        } catch (ex: Exception) {
            Timber.e("Error loading local order: ${ex.message}")
            Result.Error(AppException.DatabaseException(ex.message ?: "Unknown error"))
        }
    }
    
    override suspend fun createOrder(orderDetails: Map<String, String>): Result<String> = try {
        Timber.d("Creating order")
        
        val response = orderApi.createOrder(orderDetails)
        
        // Create order object and save to local DB
        val order = Order(
            id = response.id,
            userId = "",
            totalPrice = orderDetails["total_price"]?.toDoubleOrNull() ?: 0.0,
            status = "pending",
            date = System.currentTimeMillis().toString(),
            items = emptyList()
        )
        orderDao.insert(order)
        Timber.d("Order created successfully: ${response.id}")
        
        Result.Success(response.id)
    } catch (e: Exception) {
        Timber.e("Error creating order: ${e.message}")
        Result.Error(AppException.NetworkException(e.message ?: "Failed to create order"))
    }
    
    override suspend fun cancelOrder(orderId: String, reason: String): Result<Unit> = try {
        Timber.d("Cancelling order: $orderId, reason: $reason")
        
        orderApi.cancelOrder(orderId, reason)
        
        // Update local database
        val order = orderDao.getOrderById(orderId)
        if (order != null) {
            val updatedOrder = order.copy(status = "cancelled")
            orderDao.update(updatedOrder)
            Timber.d("Order cancelled: $orderId")
        }
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error cancelling order: ${e.message}")
        Result.Error(AppException.NetworkException(e.message ?: "Failed to cancel order"))
    }
    
    override suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> = try {
        Timber.d("Updating order status: $orderId -> $status")
        
        val order = orderDao.getOrderById(orderId)
        if (order != null) {
            val updatedOrder = order.copy(status = status)
            orderDao.update(updatedOrder)
            Timber.d("Order status updated: $status")
            Result.Success(Unit)
        } else {
            Timber.w("Order not found: $orderId")
            Result.Error(AppException.NotFound("Order not found"))
        }
    } catch (e: Exception) {
        Timber.e("Error updating order status: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Unknown error"))
    }
    
    override fun observeOrders(): Flow<List<Order>> {
        Timber.d("Observing orders")
        return orderDao.observeAllOrders()
    }
    
    override fun observeOrderById(id: String): Flow<Order?> {
        Timber.d("Observing order: $id")
        return orderDao.observeOrderById(id)
    }
}
