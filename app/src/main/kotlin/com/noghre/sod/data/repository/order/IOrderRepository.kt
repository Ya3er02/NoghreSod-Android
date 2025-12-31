package com.noghre.sod.data.repository.order

import com.noghre.sod.core.result.Result
import com.noghre.sod.domain.model.Order
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for order operations.
 *
 * Defines contract for:
 * - Retrieving user orders
 * - Getting order details
 * - Creating new orders
 * - Tracking order status
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
interface IOrderRepository {

    /**
     * Get all orders for current user.
     *
     * @return Flow emitting Result with list of Orders
     */
    fun getOrders(): Flow<Result<List<Order>>>

    /**
     * Get detailed information for specific order.
     *
     * @param orderId Order ID to retrieve
     * @return Flow emitting Result with Order details
     */
    fun getOrderDetail(orderId: String): Flow<Result<Order>>

    /**
     * Create new order from cart.
     *
     * @param order Order object with items and shipping details
     * @return Result with created Order
     */
    suspend fun createOrder(order: Order): Result<Order>

    /**
     * Get order tracking information.
     *
     * @param orderId Order ID to track
     * @return Result with Order including tracking details
     */
    suspend fun getOrderTracking(orderId: String): Result<Order>
}
