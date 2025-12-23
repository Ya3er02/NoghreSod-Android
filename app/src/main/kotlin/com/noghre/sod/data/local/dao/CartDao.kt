package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.model.CartEntity
import com.noghre.sod.data.model.CartItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Cart Data Access Object (DAO)
 * Manages shopping cart and cart items operations
 */
@Dao
interface CartDao {

    // ==================== CartItem INSERT Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItems(items: List<CartItemEntity>)

    // ==================== CartItem QUERY Operations ====================

    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItemByProductId(productId: String): CartItemEntity?

    @Query("SELECT * FROM cart_items WHERE id = :itemId")
    suspend fun getCartItemById(itemId: Long): CartItemEntity?

    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): Flow<Int>

    @Query(
        """SELECT SUM(quantity) FROM cart_items"""
    )
    fun getTotalItems(): Flow<Int?>

    @Query(
        """SELECT SUM(weight) FROM cart_items"""
    )
    fun getTotalWeight(): Flow<Double?>

    @Query(
        """SELECT SUM((unitPrice + laborCost) * quantity) FROM cart_items"""
    )
    fun getCartTotal(): Flow<Long?>

    // ==================== CartItem UPDATE Operations ====================

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :itemId")
    suspend fun updateQuantity(itemId: Long, quantity: Int)

    // ==================== CartItem DELETE Operations ====================

    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :itemId")
    suspend fun deleteCartItemById(itemId: Long)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItemByProductId(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // ==================== Cart INSERT Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: CartEntity)

    // ==================== Cart QUERY Operations ====================

    @Query("SELECT * FROM cart WHERE id = :cartId")
    fun getCart(cartId: String = "user_cart"): Flow<CartEntity?>

    @Query(
        """SELECT 
               'user_cart' as id,
               COUNT(*) as totalItems,
               COALESCE(SUM(weight), 0.0) as totalWeight,
               COALESCE(SUM((unitPrice + laborCost) * quantity), 0) as subtotal,
               0 as tax,
               0 as shippingCost,
               0 as discountAmount,
               COALESCE(SUM((unitPrice + laborCost) * quantity), 0) as total,
               ? as updatedAt
           FROM cart_items"""
    )
    fun getCurrentCartSummary(timestamp: Long): Flow<CartEntity?>

    // ==================== Cart UPDATE Operations ====================

    @Update
    suspend fun updateCart(cart: CartEntity)

    // ==================== Cart DELETE Operations ====================

    @Query("DELETE FROM cart")
    suspend fun deleteCart()
}
