package com.noghre.sod.data.local.db.cart

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Cart items.
 * 
* Handles all database operations for shopping cart.
 * Persists cart across app sessions.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Dao
interface CartDao {

    /**
     * Insert cart item.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartEntity)

    /**
     * Insert multiple cart items.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItems(items: List<CartEntity>)

    /**
     * Get all cart items as Flow.
     */
    @Query("SELECT * FROM cart ORDER BY addedAt DESC")
    fun getAllCartItems(): Flow<List<CartEntity>>

    /**
     * Get cart item by product ID.
     */
    @Query("SELECT * FROM cart WHERE productId = :productId LIMIT 1")
    suspend fun getCartItemByProductId(productId: String): CartEntity?

    /**
     * Check if product is in cart.
     */
    @Query("SELECT COUNT(*) FROM cart WHERE productId = :productId")
    suspend fun isProductInCart(productId: String): Int

    /**
     * Get total cart items count.
     */
    @Query("SELECT COUNT(*) FROM cart")
    fun getCartItemCount(): Flow<Int>

    /**
     * Get total cart price.
     */
    @Query("SELECT SUM(price * quantity) FROM cart")
    fun getTotalPrice(): Flow<Double>

    /**
     * Update cart item quantity.
     */
    @Query("UPDATE cart SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: String, quantity: Int)

    /**
     * Update cart item.
     */
    @Update
    suspend fun updateCartItem(item: CartEntity)

    /**
     * Delete cart item by product ID.
     */
    @Query("DELETE FROM cart WHERE productId = :productId")
    suspend fun deleteCartItem(productId: String)

    /**
     * Delete multiple cart items.
     */
    @Query("DELETE FROM cart WHERE productId IN (:productIds)")
    suspend fun deleteCartItems(productIds: List<String>)

    /**
     * Clear entire cart.
     */
    @Query("DELETE FROM cart")
    suspend fun clearCart()

    /**
     * Get cart items with details.
     */
    @Query(
        """
        SELECT * FROM cart 
        WHERE quantity > 0 
        ORDER BY addedAt DESC
        """
    )
    fun getValidCartItems(): Flow<List<CartEntity>>

    /**
     * Get cart summary.
     */
    @Query(
        """
        SELECT 
            COUNT(*) as itemCount,
            SUM(quantity) as totalQuantity,
            SUM(price * quantity) as totalPrice
        FROM cart
        """
    )
    suspend fun getCartSummary(): CartSummary?
}

/**
 * Cart summary data class.
 */
data class CartSummary(
    val itemCount: Int,
    val totalQuantity: Int,
    val totalPrice: Double
)
