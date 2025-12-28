package com.noghre.sod.data.repository

import com.noghre.sod.core.error.AppException
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.local.CartDao
import com.noghre.sod.data.local.ProductDao
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val productDao: ProductDao
) : CartRepository {
    
    override suspend fun getCartItems(): Result<List<CartItem>> = try {
        Timber.d("Fetching cart items")
        val items = cartDao.getAll()
        Timber.d("Cart items: ${items.size}")
        Result.Success(items)
    } catch (e: Exception) {
        Timber.e("Error fetching cart items: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Unknown error"))
    }
    
    override suspend fun addToCart(productId: String, quantity: Int): Result<Unit> = try {
        Timber.d("Adding to cart: productId=$productId, quantity=$quantity")
        
        val product = productDao.getProductById(productId)
        if (product == null) {
            Timber.w("Product not found: $productId")
            return Result.Error(AppException.NotFound("Product not found"))
        }
        
        val existingItem = cartDao.getByProductId(productId)
        if (existingItem != null) {
            // Update existing item
            val updatedItem = existingItem.copy(
                quantity = existingItem.quantity + quantity
            )
            cartDao.update(updatedItem)
            Timber.d("Updated cart item quantity: ${updatedItem.quantity}")
        } else {
            // Add new item
            val newItem = CartItem(
                id = "${System.currentTimeMillis()}",
                product = product,
                quantity = quantity
            )
            cartDao.insert(newItem)
            Timber.d("Added new item to cart")
        }
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error adding to cart: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Unknown error"))
    }
    
    override suspend fun removeFromCart(cartItemId: String): Result<Unit> = try {
        Timber.d("Removing from cart: $cartItemId")
        cartDao.delete(cartItemId)
        Timber.d("Removed from cart")
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error removing from cart: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Unknown error"))
    }
    
    override suspend fun updateQuantity(cartItemId: String, quantity: Int): Result<Unit> = try {
        Timber.d("Updating quantity: cartItemId=$cartItemId, quantity=$quantity")
        
        if (quantity <= 0) {
            Timber.w("Invalid quantity: $quantity")
            return Result.Error(AppException.ValidationException("Quantity must be positive"))
        }
        
        val item = cartDao.getById(cartItemId)
        if (item != null) {
            val updatedItem = item.copy(quantity = quantity)
            cartDao.update(updatedItem)
            Timber.d("Quantity updated: $quantity")
            Result.Success(Unit)
        } else {
            Timber.w("Cart item not found: $cartItemId")
            Result.Error(AppException.NotFound("Cart item not found"))
        }
    } catch (e: Exception) {
        Timber.e("Error updating quantity: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Unknown error"))
    }
    
    override suspend fun clearCart(): Result<Unit> = try {
        Timber.d("Clearing cart")
        cartDao.deleteAll()
        Timber.d("Cart cleared")
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error clearing cart: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Unknown error"))
    }
    
    override suspend fun getCartTotal(): Result<Double> = try {
        Timber.d("Calculating cart total")
        val items = cartDao.getAll()
        val total = items.sumOf { it.product.price * it.quantity }
        Timber.d("Cart total: $total")
        Result.Success(total)
    } catch (e: Exception) {
        Timber.e("Error calculating total: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Unknown error"))
    }
    
    override fun observeCart(): Flow<List<CartItem>> {
        Timber.d("Observing cart")
        return cartDao.observeAll()
    }
    
    override suspend fun getCartItemCount(): Result<Int> = try {
        Timber.d("Getting cart item count")
        val count = cartDao.getAll().size
        Timber.d("Cart item count: $count")
        Result.Success(count)
    } catch (e: Exception) {
        Timber.e("Error getting cart count: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Unknown error"))
    }
}
