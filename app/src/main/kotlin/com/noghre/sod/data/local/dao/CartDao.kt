package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Cart operations.
 * Handles local cart storage and synchronization.
 */
@Dao
interface CartDao {

    /**
     * Get all cart items.
     */
    @Query("SELECT * FROM cart_items ORDER BY added_at DESC")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    /**
     * Get cart item by ID.
     */
    @Query("SELECT * FROM cart_items WHERE id = :itemId")
    suspend fun getCartItemById(itemId: String): CartItemEntity?

    /**
     * Insert item to cart.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    /**
     * Update cart item (mainly quantity).
     */
    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    /**
     * Remove item from cart.
     */
    @Delete
    suspend fun removeCartItem(item: CartItemEntity)

    /**
     * Remove cart item by ID.
     */
    @Query("DELETE FROM cart_items WHERE id = :itemId")
    suspend fun removeCartItemById(itemId: String)

    /**
     * Clear all cart items.
     */
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    /**
     * Get total cart price.
     */
    @Query("SELECT SUM(subtotal) FROM cart_items")
    fun getCartTotal(): Flow<Double?>

    /**
     * Get cart items count.
     */
    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): Flow<Int>

    /**
     * Check if product is in cart.
     */
    @Query("SELECT COUNT(*) FROM cart_items WHERE product_id = :productId")
    suspend fun isProductInCart(productId: String): Int
}
