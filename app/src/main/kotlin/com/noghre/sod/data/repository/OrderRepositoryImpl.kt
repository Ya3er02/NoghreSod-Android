package com.noghre.sod.data.repository

import com.noghre.sod.data.local.db.order.OrderDao
import com.noghre.sod.data.local.db.order.OrderEntity
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.model.Result
import com.noghre.sod.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of OrderRepository.
 * 
* Manages orders with both local and remote storage.
 * Handles order creation, status tracking, and history.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val orderDao: OrderDao
) : OrderRepository {

    /**
     * Get user's order history.
     */
    override fun getOrderHistory(): Flow<Result<List<Order>>> = flow {
        try {
            val response = apiService.getOrders()
            
            if (response.isSuccessful) {
                response.body()?.data?.let { orders ->
                    // Cache locally
                    val entities = orders.map { it.toEntity() }
                    orderDao.insertOrders(entities)
                    emit(Result.Success(orders))
                }
            } else {
                emit(Result.Error(
                    exception = Exception(response.message()),
                    message = "Failed to fetch orders"
                ))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching order history")
            
            // Fallback to cache
            try {
                val cached = orderDao.getAllOrders()
                    .map { it.toDomain() }
                emit(Result.Success(cached))
            } catch (cacheError: Exception) {
                emit(Result.Error(
                    exception = e,
                    message = "Failed to fetch orders"
                ))
            }
        }
    }.catch { exception ->
        emit(Result.Error(
            exception = exception as Exception,
            message = exception.localizedMessage ?: "Unknown error"
        ))
    }

    /**
     * Get specific order details.
     */
    override fun getOrderDetails(orderId: String): Flow<Result<Order>> = flow {
        try {
            val response = apiService.getOrderDetails(orderId)
            
            if (response.isSuccessful) {
                response.body()?.data?.let { order ->
                    orderDao.insertOrder(order.toEntity())
                    emit(Result.Success(order))
                }
            } else {
                emit(Result.Error(
                    exception = Exception(response.message()),
                    message = "Order not found"
                ))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching order details")
            emit(Result.Error(
                exception = e,
                message = "Failed to fetch order details"
            ))
        }
    }

    /**
     * Create and submit new order.
     */
    override suspend fun createOrder(order: Order): Result<Order> {
        return try {
            val response = apiService.createOrder(order.toDto())
            
            if (response.isSuccessful) {
                response.body()?.data?.let { createdOrder ->
                    orderDao.insertOrder(createdOrder.toEntity())
                    return Result.Success(createdOrder)
                }
            }
            
            Result.Error(
                exception = Exception(response.message()),
                message = "Failed to create order"
            )
        } catch (e: Exception) {
            Timber.e(e, "Error creating order")
            Result.Error(
                exception = e,
                message = "Order creation failed"
            )
        }
    }

    /**
     * Cancel order by ID.
     */
    override suspend fun cancelOrder(orderId: String): Result<Unit> {
        return try {
            val response = apiService.cancelOrder(orderId)
            
            if (response.isSuccessful) {
                orderDao.updateOrderStatus(orderId, "cancelled")
                return Result.Success(Unit)
            }
            
            Result.Error(
                exception = Exception(response.message()),
                message = "Failed to cancel order"
            )
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Cancellation failed"
            )
        }
    }

    /**
     * Get orders filtered by status.
     */
    override fun getOrdersByStatus(status: String): Flow<Result<List<Order>>> = flow {
        try {
            val orders = orderDao.getOrdersByStatus(status)
                .map { entities ->
                    entities.map { it.toDomain() }
                }
            
            orders.collect { orderList ->
                emit(Result.Success(orderList))
            }
        } catch (e: Exception) {
            emit(Result.Error(
                exception = e,
                message = "Failed to fetch orders by status"
            ))
        }
    }

    /**
     * Get recent orders (limit 10).
     */
    override fun getRecentOrders(limit: Int): Flow<Result<List<Order>>> = flow {
        try {
            val orders = orderDao.getRecentOrders(limit)
                .map { entities ->
                    entities.map { it.toDomain() }
                }
            
            orders.collect { orderList ->
                emit(Result.Success(orderList))
            }
        } catch (e: Exception) {
            emit(Result.Error(
                exception = e,
                message = "Failed to fetch recent orders"
            ))
        }
    }
}
