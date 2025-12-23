package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE id = :id")
    fun getOrderById(id: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserOrders(userId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE userId = :userId AND status = :status")
    fun getOrdersByStatus(userId: String, status: String): Flow<List<OrderEntity>>

    @Query("DELETE FROM orders WHERE userId = :userId")
    suspend fun deleteUserOrders(userId: String)

    @Query("DELETE FROM orders")
    suspend fun clearAll()
}