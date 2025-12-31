package com.noghre.sod.data.repository.cart

import com.noghre.sod.core.result.Result
import com.noghre.sod.data.database.dao.CartDao
import com.noghre.sod.data.database.dao.ProductDao
import com.noghre.sod.data.error.ExceptionHandler
import com.noghre.sod.data.mapper.CartMapper.toDomain
import com.noghre.sod.data.mapper.CartMapper.toEntity
import com.noghre.sod.data.network.NoghreSodApi
import com.noghre.sod.data.repository.networkBoundResource
import com.noghre.sod.domain.model.Cart
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

/**
 * Cart Repository Implementation.
 *
 * Implements Single Source of Truth (SSOT) pattern:
 * - Caches cart data locally
 * - Syncs with network when needed
 * - Provides offline-first access
 * - Handles errors gracefully
 *
 * @param api Retrofit API client
 * @param cartDao Database access object for cart
 * @param productDao Database access object for products
 * @param ioDispatcher Dispatcher for I/O operations
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
class CartRepositoryImpl @Inject constructor(
    private val api: NoghreSodApi,
    private val cartDao: CartDao,
    private val productDao: ProductDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ICartRepository {

    // ============ QUERIES ============

    override fun getCart(): Flow<Result<Cart>> = networkBoundResource(
        query = {
            cartDao.getCart()?.toDomain()
                ?: Cart(id = "", userId = "", items = emptyList(), totalPrice = 0.0, itemCount = 0)
        },
        fetch = {
            api.getCart()
        },
        saveFetchResult = { response ->
            val cart = response.toDomain()
            cartDao.insertCart(cart.toEntity())
            Timber.d("Cart cached: ${cart.id}")
        },
        shouldFetch = { localData ->
            // Fetch if cache is old (check timestamp)
            localData.id.isEmpty() || isCacheStale()
        },
        onFetchFailed = { exception ->
            Timber.e(exception, "Failed to fetch cart")
        }
    ).flowOn(ioDispatcher)

    override suspend fun getCartLocally(): Cart? {
        return try {
            cartDao.getCart()?.toDomain()
        } catch (e: Exception) {
            Timber.e(e, "Error getting local cart")
            null
        }
    }

    // ============ MUTATIONS ============

    override suspend fun addToCart(
        productId: String,
        quantity: Int,
        selectedColor: String?,
        selectedSize: String?
    ): Result<Cart> = try {
        val requestBody = mapOf(
            "productId" to productId,
            "quantity" to quantity,
            "selectedColor" to (selectedColor ?: ""),
            "selectedSize" to (selectedSize ?: "")
        )

        val response = api.addToCart(requestBody)

        if (response.isSuccessful && response.body()?.success == true) {
            val cartDto = response.body()?.data
            if (cartDto != null) {
                val cart = cartDto.toDomain()
                cartDao.insertCart(cart.toEntity())
                Timber.d("Item added to cart: $productId")
                Result.success(cart)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            val errorMsg = "Failed to add item: ${response.code()}"
            Timber.e(errorMsg)
            Result.failure(Exception(errorMsg))
        }
    } catch (e: Exception) {
        val errorState = ExceptionHandler.handle(e, "addToCart")
        Timber.e(e, "Error adding to cart")
        Result.failure(e)
    }

    override suspend fun updateCartItem(
        itemId: String,
        quantity: Int
    ): Result<Cart> = try {
        val requestBody = mapOf("quantity" to quantity)
        val response = api.updateCartItem(itemId, requestBody)

        if (response.isSuccessful && response.body()?.success == true) {
            val cartDto = response.body()?.data
            if (cartDto != null) {
                val cart = cartDto.toDomain()
                cartDao.insertCart(cart.toEntity())
                Timber.d("Cart item updated: $itemId")
                Result.success(cart)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            Result.failure(Exception("Failed to update item"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "updateCartItem")
        Timber.e(e, "Error updating cart item")
        Result.failure(e)
    }

    override suspend fun removeFromCart(itemId: String): Result<Cart> = try {
        val response = api.removeFromCart(itemId)

        if (response.isSuccessful && response.body()?.success == true) {
            val cartDto = response.body()?.data
            if (cartDto != null) {
                val cart = cartDto.toDomain()
                cartDao.insertCart(cart.toEntity())
                Timber.d("Item removed from cart: $itemId")
                Result.success(cart)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            Result.failure(Exception("Failed to remove item"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "removeFromCart")
        Timber.e(e, "Error removing from cart")
        Result.failure(e)
    }

    override suspend fun clearCart(): Result<Unit> = try {
        val response = api.clearCart()

        if (response.isSuccessful && response.body()?.success == true) {
            cartDao.clearCart()
            Timber.d("Cart cleared")
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to clear cart"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "clearCart")
        Timber.e(e, "Error clearing cart")
        Result.failure(e)
    }

    // ============ HELPERS ============

    /**
     * Check if local cache is stale (older than 1 hour).
     */
    private suspend fun isCacheStale(): Boolean {
        return try {
            val cart = cartDao.getCart()
            if (cart == null) {
                true
            } else {
                val ageInMs = System.currentTimeMillis() - (cart.lastUpdated ?: 0)
                ageInMs > 60 * 60 * 1000  // 1 hour
            }
        } catch (e: Exception) {
            Timber.e(e, "Error checking cache staleness")
            true
        }
    }
}
