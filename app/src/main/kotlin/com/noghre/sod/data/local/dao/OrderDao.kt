package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.model.OrderEntity
import com.noghre.sod.data.model.OrderItemEntity
import com.noghre.sod.data.model.OrderStatusHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Order Data Access Object (DAO)
 * Handles order, order items, and order status history operations
 */
@Dao
interface OrderDao {

    // ==================== Order INSERT Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    // ==================== Order QUERY Operations ====================

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderByIdFlow(orderId: String): Flow<OrderEntity?>

    @Query(
        """SELECT * FROM orders 
           WHERE userId = :userId 
           ORDER BY createdAt DESC"""
    )
    fun getUserOrders(userId: String): Flow<List<OrderEntity>>

    @Query(
        """SELECT * FROM orders 
           WHERE userId = :userId AND status = :status 
           ORDER BY createdAt DESC"""
    )
    fun getUserOrdersByStatus(userId: String, status: String): Flow<List<OrderEntity>>

    @Query(
        """SELECT * FROM orders 
           ORDER BY createdAt DESC 
           LIMIT :limit"""
    )
    fun getRecentOrders(limit: Int = 10): Flow<List<OrderEntity>>

    @Query(
        """SELECT * FROM orders 
           WHERE status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'SHIPPED') 
           ORDER BY createdAt DESC"""
    )
    fun getActiveOrders(): Flow<List<OrderEntity>>

    @Query(
        """SELECT * FROM orders 
           WHERE paymentStatus = 'UNPAID' 
           ORDER BY createdAt DESC"""
    )
    fun getUnpaidOrders(): Flow<List<OrderEntity>>

    @Query("SELECT COUNT(*) FROM orders WHERE userId = :userId")
    fun getUserOrderCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM orders WHERE status = 'PENDING'")
    fun getPendingOrderCount(): Flow<Int>

    // ==================== Order UPDATE Operations ====================

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query(
        """UPDATE orders 
           SET status = :status, updatedAt = :timestamp 
           WHERE id = :orderId"""
    )
    suspend fun updateOrderStatus(orderId: String, status: String, timestamp: Long)

    @Query(
        """UPDATE orders 
           SET paymentStatus = :paymentStatus, updatedAt = :timestamp 
           WHERE id = :orderId"""
    )
    suspend fun updatePaymentStatus(orderId: String, paymentStatus: String, timestamp: Long)

    @Query(
        """UPDATE orders 
           SET trackingCode = :trackingCode, estimatedDeliveryDate = :estimatedDate 
           WHERE id = :orderId"""
    )
    suspend fun updateTrackingInfo(
        orderId: String,
        trackingCode: String,
        estimatedDate: Long
    )

    // ==================== Order DELETE Operations ====================

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrderById(orderId: String)

    // ==================== Order Item INSERT Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(item: OrderItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    // ==================== Order Item QUERY Operations ====================

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: String): List<OrderItemEntity>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsFlow(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT COUNT(*) FROM order_items WHERE orderId = :orderId")
    fun getOrderItemCount(orderId: String): Flow<Int>

    // ==================== Order Item UPDATE Operations ====================

    @Update
    suspend fun updateOrderItem(item: OrderItemEntity)

    // ==================== Order Item DELETE Operations ====================

    @Delete
    suspend fun deleteOrderItem(item: OrderItemEntity)

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteOrderItems(orderId: String)

    // ==================== Order Status History INSERT Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusHistory(history: OrderStatusHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusHistories(histories: List<OrderStatusHistoryEntity>)

    // ==================== Order Status History QUERY Operations ====================

    @Query(
        """SELECT * FROM order_status_history 
           WHERE orderId = :orderId 
           ORDER BY timestamp DESC"""
    )
    fun getOrderStatusHistory(orderId: String): Flow<List<OrderStatusHistoryEntity>>

    @Query(
        """SELECT * FROM order_status_history 
           WHERE orderId = :orderId 
           ORDER BY timestamp ASC"""
    )
    fun getOrderStatusHistoryAscending(orderId: String): Flow<List<OrderStatusHistoryEntity>>
}
