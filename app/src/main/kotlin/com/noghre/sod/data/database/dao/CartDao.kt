package com.noghre.sod.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.database.entity.CartEntity
import kotlinx.coroutines.flow.Flow

/**
 * CartDao - Data Access Object for shopping cart operations.
 * 
 * Handles all database queries for cart items.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Dao
interface CartDao {
    
    /**
     * Insert a cart item
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartEntity): Long
    
    /**
     * Insert multiple cart items
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItems(items: List<CartEntity>)
    
    /**
     * Update cart item
     */
    @Update
    suspend fun updateCartItem(item: CartEntity)
    
    /**
     * Delete cart item
     */
    @Delete
    suspend fun deleteCartItem(item: CartEntity)
    
    /**
     * Delete cart item by ID
     */
    @Query("DELETE FROM cart_items WHERE id = :itemId")
    suspend fun deleteCartItemById(itemId: Long)
    
    /**
     * Delete all cart items for user
     */
    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun deleteUserCart(userId: String)
    
    /**
     * Get all cart items for user
     */
    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    suspend fun getCartItems(userId: String): List<CartEntity>
    
    /**
     * Get cart items as Flow for real-time updates
     */
    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    fun getCartItemsFlow(userId: String): Flow<List<CartEntity>>
    
    /**
     * Get cart item by ID
     */
    @Query("SELECT * FROM cart_items WHERE id = :itemId")
    suspend fun getCartItemById(itemId: Long): CartEntity?
    
    /**
     * Check if product is in cart
     */
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getCartItem(userId: String, productId: String): CartEntity?
    
    /**
     * Get cart item count
     */
    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    suspend fun getCartItemCount(userId: String): Int
    
    /**
     * Get total cart value
     */
    @Query("SELECT SUM(subtotal) FROM cart_items WHERE userId = :userId")
    suspend fun getCartTotal(userId: String): Double
    
    /**
     * Get total items quantity
     */
    @Query("SELECT SUM(quantity) FROM cart_items WHERE userId = :userId")
    suspend fun getTotalQuantity(userId: String): Int
    
    /**
     * Clear expired cart items (older than 30 days)
     */
    @Query("""
        DELETE FROM cart_items 
        WHERE userId = :userId 
        AND datetime(lastUpdatedAt) < datetime('now', '-30 days')
    """)
    suspend fun clearExpiredItems(userId: String)
    
    /**
     * Update item quantity
     */
    @Query("""
        UPDATE cart_items 
        SET quantity = :quantity, 
            subtotal = :quantity * pricePerUnit,
            lastUpdatedAt = datetime('now')
        WHERE id = :itemId
    """)
    suspend fun updateQuantity(itemId: Long, quantity: Int)
    
    /**
     * Check cart availability (all items in stock)
     */
    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId AND isAvailable = 0")
    suspend fun getUnavailableItemCount(userId: String): Int
}
