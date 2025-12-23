package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.local.entity.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(item: CartEntity)

    @Update
    suspend fun updateCartItem(item: CartEntity)

    @Delete
    suspend fun removeFromCart(item: CartEntity)

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getUserCart(userId: String): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId")
    fun getCartItem(userId: String, productId: String): Flow<CartEntity?>

    @Query("DELETE FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun removeCartItem(userId: String, productId: String)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearUserCart(userId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    fun getCartItemCount(userId: String): Flow<Int>
}