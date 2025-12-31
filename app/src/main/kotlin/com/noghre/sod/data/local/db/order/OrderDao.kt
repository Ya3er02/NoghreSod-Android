package com.noghre.sod.data.local.db.order

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Order entities.
 * 
* Handles all database operations for orders.
 * Stores order history and status tracking.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Dao
interface OrderDao {

    /**
     * Insert order.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    /**
     * Insert multiple orders.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    /**
     * Get all orders as Flow.
     */
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    /**
     * Get all orders (non-Flow).
     */
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    suspend fun getAllOrdersSync(): List<OrderEntity>

    /**
     * Get order by ID.
     */
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    /**
     * Get orders by status.
     */
    @Query(
        """
        SELECT * FROM orders 
        WHERE status = :status 
        ORDER BY createdAt DESC
        """
    )
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    /**
     * Get pending orders.
     */
    @Query(
        """
        SELECT * FROM orders 
        WHERE status = 'pending' 
        ORDER BY createdAt DESC
        """
    )
    fun getPendingOrders(): Flow<List<OrderEntity>>

    /**
     * Get confirmed orders.
     */
    @Query(
        """
        SELECT * FROM orders 
        WHERE status = 'confirmed' 
        ORDER BY createdAt DESC
        """
    )
    fun getConfirmedOrders(): Flow<List<OrderEntity>>

    /**
     * Get shipped orders.
     */
    @Query(
        """
        SELECT * FROM orders 
        WHERE status = 'shipped' 
        ORDER BY createdAt DESC
        """
    )
    fun getShippedOrders(): Flow<List<OrderEntity>>

    /**
     * Get delivered orders.
     */
    @Query(
        """
        SELECT * FROM orders 
        WHERE status = 'delivered' 
        ORDER BY createdAt DESC
        """
    )
    fun getDeliveredOrders(): Flow<List<OrderEntity>>

    /**
     * Get cancelled orders.
     */
    @Query(
        """
        SELECT * FROM orders 
        WHERE status = 'cancelled' 
        ORDER BY createdAt DESC
        """
    )
    fun getCancelledOrders(): Flow<List<OrderEntity>>

    /**
     * Get recent orders.
     */
    @Query(
        """
        SELECT * FROM orders 
        ORDER BY createdAt DESC 
        LIMIT :limit
        """
    )
    fun getRecentOrders(limit: Int = 10): Flow<List<OrderEntity>>

    /**
     * Get orders within date range.
     */
    @Query(
        """
        SELECT * FROM orders 
        WHERE createdAt BETWEEN :startDate AND :endDate 
        ORDER BY createdAt DESC
        """
    )
    fun getOrdersByDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<OrderEntity>>

    /**
     * Update order status.
     */
    @Query(
        """
        UPDATE orders 
        SET status = :status, updatedAt = :updatedAt 
        WHERE id = :orderId
        """
    )
    suspend fun updateOrderStatus(
        orderId: String,
        status: String,
        updatedAt: String = System.currentTimeMillis().toString()
    )

    /**
     * Update order.
     */
    @Update
    suspend fun updateOrder(order: OrderEntity)

    /**
     * Delete order by ID.
     */
    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: String)

    /**
     * Delete all orders.
     */
    @Query("DELETE FROM orders")
    suspend fun deleteAllOrders()

    /**
     * Get total orders count.
     */
    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getOrderCount(): Int

    /**
     * Get orders count by status.
     */
    @Query("SELECT COUNT(*) FROM orders WHERE status = :status")
    suspend fun getOrderCountByStatus(status: String): Int

    /**
     * Get total amount spent.
     */
    @Query("SELECT SUM(totalAmount) FROM orders WHERE status != 'cancelled'")
    suspend fun getTotalAmountSpent(): Double
}
