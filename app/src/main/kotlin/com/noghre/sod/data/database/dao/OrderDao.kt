package com.noghre.sod.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.database.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

/**
 * OrderDao - Data Access Object for order operations.
 * 
 * Handles all database queries for orders and order history.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Dao
interface OrderDao {
    
    /**
     * Insert a new order
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)
    
    /**
     * Insert multiple orders
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)
    
    /**
     * Update an order
     */
    @Update
    suspend fun updateOrder(order: OrderEntity)
    
    /**
     * Delete an order
     */
    @Delete
    suspend fun deleteOrder(order: OrderEntity)
    
    /**
     * Get order by ID
     */
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?
    
    /**
     * Get order by ID as Flow
     */
    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderByIdFlow(orderId: String): Flow<OrderEntity?>
    
    /**
     * Get all orders for user
     */
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getUserOrders(userId: String): List<OrderEntity>
    
    /**
     * Get user orders as Flow
     */
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserOrdersFlow(userId: String): Flow<List<OrderEntity>>
    
    /**
     * Get orders by status
     */
    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getOrdersByStatus(status: String): List<OrderEntity>
    
    /**
     * Get user orders by status
     */
    @Query("""
        SELECT * FROM orders 
        WHERE userId = :userId AND status = :status 
        ORDER BY createdAt DESC
    """)
    suspend fun getUserOrdersByStatus(userId: String, status: String): List<OrderEntity>
    
    /**
     * Get pending orders for user
     */
    @Query("""
        SELECT * FROM orders 
        WHERE userId = :userId AND status IN ('pending', 'confirmed')
        ORDER BY createdAt DESC
    """)
    suspend fun getPendingOrders(userId: String): List<OrderEntity>
    
    /**
     * Get pending orders as Flow
     */
    @Query("""
        SELECT * FROM orders 
        WHERE userId = :userId AND status IN ('pending', 'confirmed')
        ORDER BY createdAt DESC
    """)
    fun getPendingOrdersFlow(userId: String): Flow<List<OrderEntity>>
    
    /**
     * Get delivered orders
     */
    @Query("""
        SELECT * FROM orders 
        WHERE userId = :userId AND status = 'delivered'
        ORDER BY deliveredAt DESC
    """)
    suspend fun getDeliveredOrders(userId: String): List<OrderEntity>
    
    /**
     * Get order by order number
     */
    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber LIMIT 1")
    suspend fun getOrderByNumber(orderNumber: String): OrderEntity?
    
    /**
     * Get unsync orders
     */
    @Query("SELECT * FROM orders WHERE isSynced = 0")
    suspend fun getUnsyncedOrders(): List<OrderEntity>
    
    /**
     * Mark orders as synced
     */
    @Query("UPDATE orders SET isSynced = 1 WHERE id IN (:orderIds)")
    suspend fun markAsSynced(orderIds: List<String>)
    
    /**
     * Update order status
     */
    @Query("UPDATE orders SET status = :status, updatedAt = datetime('now') WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)
    
    /**
     * Get total order count for user
     */
    @Query("SELECT COUNT(*) FROM orders WHERE userId = :userId")
    suspend fun getUserOrderCount(userId: String): Int
    
    /**
     * Get total spent by user
     */
    @Query("SELECT COALESCE(SUM(total), 0) FROM orders WHERE userId = :userId AND status != 'cancelled'")
    suspend fun getUserTotalSpent(userId: String): Double
    
    /**
     * Get average order value
     */
    @Query("""
        SELECT COALESCE(AVG(total), 0) 
        FROM orders 
        WHERE userId = :userId AND status != 'cancelled'
    """)
    suspend fun getUserAverageOrderValue(userId: String): Double
    
    /**
     * Search orders by order number or product name
     */
    @Query("""
        SELECT * FROM orders 
        WHERE userId = :userId AND orderNumber LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    suspend fun searchUserOrders(userId: String, query: String): List<OrderEntity>
    
    /**
     * Clear old orders (older than 1 year)
     */
    @Query("""
        DELETE FROM orders 
        WHERE status IN ('cancelled', 'returned') 
        AND datetime(createdAt) < datetime('now', '-1 year')
    """)
    suspend fun clearOldOrders()
    
    /**
     * Get total orders count
     */
    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getTotalOrderCount(): Int
}
