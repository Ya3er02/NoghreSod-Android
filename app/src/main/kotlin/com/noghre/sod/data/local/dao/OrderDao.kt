package com.noghre.sod.data.local.dao

import androidx.room.*
import com.noghre.sod.data.local.entity.OrderEntity
import com.noghre.sod.data.local.entity.OrderTrackingEntity
import com.noghre.sod.data.local.entity.ReturnRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    // ==================== Order Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrder(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC LIMIT :pageSize OFFSET :offset")
    fun getUserOrders(offset: Int, pageSize: Int): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    @Query("DELETE FROM orders")
    suspend fun deleteAllOrders()

    // ==================== Order Tracking ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracking(tracking: OrderTrackingEntity)

    @Query("SELECT * FROM order_tracking WHERE orderId = :orderId")
    fun getTracking(orderId: String): Flow<OrderTrackingEntity?>

    @Update
    suspend fun updateTracking(tracking: OrderTrackingEntity)

    // ==================== Return Requests ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReturnRequest(returnRequest: ReturnRequestEntity)

    @Query("SELECT * FROM return_requests WHERE orderId = :orderId")
    fun getReturnRequest(orderId: String): Flow<ReturnRequestEntity?>

    @Query("SELECT * FROM return_requests WHERE status = :status ORDER BY createdAt DESC")
    fun getReturnRequestsByStatus(status: String): Flow<List<ReturnRequestEntity>>

    @Update
    suspend fun updateReturnRequest(returnRequest: ReturnRequestEntity)
}
