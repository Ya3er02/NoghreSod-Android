package com.noghre.sod.data.local.dao

import androidx.room.*
import com.noghre.sod.data.local.entity.CartEntity
import com.noghre.sod.data.local.entity.CartItemEntity
import com.noghre.sod.data.local.entity.SavedCartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // ==================== Cart Operations ====================

    @Query("SELECT * FROM cart WHERE id = 'user_cart'")
    fun getCart(): Flow<CartEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: CartEntity)

    @Update
    suspend fun updateCart(cart: CartEntity)

    @Query("DELETE FROM cart")
    suspend fun clearCart()

    // ==================== Cart Items ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItems(items: List<CartItemEntity>)

    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE id = :itemId")
    fun getCartItem(itemId: String): Flow<CartItemEntity?>

    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): Flow<Int>

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :itemId")
    suspend fun removeCartItem(itemId: String)

    @Query("DELETE FROM cart_items")
    suspend fun deleteAllCartItems()

    // ==================== Saved Carts ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCart(savedCart: SavedCartEntity)

    @Query("SELECT * FROM saved_carts ORDER BY createdAt DESC")
    fun getSavedCarts(): Flow<List<SavedCartEntity>>

    @Query("SELECT * FROM saved_carts WHERE id = :id")
    fun getSavedCart(id: String): Flow<SavedCartEntity?>

    @Delete
    suspend fun deleteSavedCart(savedCart: SavedCartEntity)
}
