package com.noghre.sod.data.repository

import com.noghre.sod.data.local.dao.CartDao
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.api.request.AddToCartRequest
import com.noghre.sod.data.remote.api.request.UpdateCartItemRequest
import com.noghre.sod.data.remote.exception.ApiException
import com.noghre.sod.data.remote.network.NetworkMonitor
import com.noghre.sod.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

/**
 * Cart Repository Implementation with local and remote sync.
 */
class CartRepositoryImpl @Inject constructor(
    private val api: NoghreSodApiService,
    private val cartDao: CartDao,
    private val networkMonitor: NetworkMonitor
) {

    /**
     * Get current cart.
     */
    suspend fun getCart(): Result<Unit> = try {
        if (networkMonitor.isNetworkAvailable()) {
            val response = api.getCart()
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Failed to fetch cart")
            }
        } else {
            Result.Success(Unit) // Return local cart
        }
    } catch (e: Exception) {
        Timber.e(e, "Error getting cart")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Add item to cart.
     */
    suspend fun addToCart(productId: String, quantity: Int): Result<Unit> = try {
        // Add to local cart first
        val cartItem = com.noghre.sod.data.local.entity.CartItemEntity(
            id = "cart_item_${System.currentTimeMillis()}",
            productId = productId,
            productName = "",
            productImage = null,
            quantity = quantity,
            priceAtTime = 0.0,
            subtotal = 0.0
        )
        cartDao.insertCartItem(cartItem)

        // Sync with server if online
        if (networkMonitor.isNetworkAvailable()) {
            try {
                val response = api.addToCart(AddToCartRequest(productId, quantity))
                if (response.success) {
                    Result.Success(Unit)
                } else {
                    Result.Error(response.message ?: "Failed to add to cart")
                }
            } catch (e: ApiException) {
                Timber.e(e, "API error adding to cart")
                Result.Error(e.message ?: "Network error")
            }
        } else {
            Result.Success(Unit)
        }
    } catch (e: Exception) {
        Timber.e(e, "Error adding to cart")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Update cart item quantity.
     */
    suspend fun updateCartItem(itemId: String, quantity: Int): Result<Unit> = try {
        val item = cartDao.getCartItemById(itemId)
        if (item != null) {
            val updated = item.copy(quantity = quantity)
            cartDao.updateCartItem(updated)

            if (networkMonitor.isNetworkAvailable()) {
                try {
                    api.updateCartItem(itemId, UpdateCartItemRequest(quantity))
                    Result.Success(Unit)
                } catch (e: ApiException) {
                    Result.Error(e.message ?: "Network error")
                }
            } else {
                Result.Success(Unit)
            }
        } else {
            Result.Error("Cart item not found")
        }
    } catch (e: Exception) {
        Timber.e(e, "Error updating cart item")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Remove item from cart.
     */
    suspend fun removeFromCart(itemId: String): Result<Unit> = try {
        cartDao.removeCartItemById(itemId)

        if (networkMonitor.isNetworkAvailable()) {
            try {
                api.removeFromCart(itemId)
                Result.Success(Unit)
            } catch (e: ApiException) {
                Result.Error(e.message ?: "Network error")
            }
        } else {
            Result.Success(Unit)
        }
    } catch (e: Exception) {
        Timber.e(e, "Error removing from cart")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Clear entire cart.
     */
    suspend fun clearCart(): Result<Unit> = try {
        cartDao.clearCart()

        if (networkMonitor.isNetworkAvailable()) {
            try {
                api.clearCart()
                Result.Success(Unit)
            } catch (e: ApiException) {
                Result.Error(e.message ?: "Network error")
            }
        } else {
            Result.Success(Unit)
        }
    } catch (e: Exception) {
        Timber.e(e, "Error clearing cart")
        Result.Error(e.message ?: "Unknown error")
    }
}
