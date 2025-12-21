package com.noghre.sod.data.repository

import com.noghre.sod.data.local.CartDao
import com.noghre.sod.data.remote.ApiService
import com.noghre.sod.data.model.CartItem
import com.noghre.sod.data.remote.AddToCartRequest
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Repository for cart-related operations.
 */
class CartRepository @Inject constructor(
    private val apiService: ApiService,
    private val cartDao: CartDao
) {

    fun getCartItems(): Flow<List<CartItem>> = cartDao.getCartItems()

    fun getCartItemCount(): Flow<Int> = cartDao.getCartItemCount()

    suspend fun addToCart(productId: String, quantity: Int) {
        try {
            apiService.addToCart(AddToCartRequest(productId, quantity))
            val item = CartItem(productId = productId, quantity = quantity)
            cartDao.addToCart(item)
        } catch (e: Exception) {
            Timber.e(e, "Error adding to cart")
            throw e
        }
    }

    suspend fun removeFromCart(productId: String) {
        try {
            apiService.removeFromCart(productId)
            cartDao.removeFromCart(productId)
        } catch (e: Exception) {
            Timber.e(e, "Error removing from cart")
            throw e
        }
    }

    suspend fun updateCartItem(item: CartItem) {
        try {
            cartDao.updateCartItem(item)
        } catch (e: Exception) {
            Timber.e(e, "Error updating cart item")
            throw e
        }
    }

    suspend fun clearCart() {
        try {
            apiService.clearCart()
            cartDao.clearCart()
        } catch (e: Exception) {
            Timber.e(e, "Error clearing cart")
            throw e
        }
    }
}
