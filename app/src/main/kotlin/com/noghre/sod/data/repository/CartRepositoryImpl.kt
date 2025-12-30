package com.noghre.sod.data.repository

import com.noghre.sod.data.local.db.cart.CartDao
import com.noghre.sod.data.local.db.cart.CartItemEntity
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.model.Result
import com.noghre.sod.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CartRepository.
 * 
* Manages shopping cart with local Room database persistence.
 * Supports add, remove, update operations with automatic total calculation.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) : CartRepository {

    /**
     * Get all cart items as Flow.
     */
    override fun getCartItems(): Flow<Result<List<CartItem>>> = flow {
        try {
            cartDao.getAllCartItems()
                .map { entities ->
                    entities.map { it.toDomain() }
                }.collect { items ->
                    emit(Result.Success(items))
                }
        } catch (e: Exception) {
            Timber.e(e, "Error getting cart items")
            emit(Result.Error(
                exception = e,
                message = "Failed to get cart items: ${e.localizedMessage}"
            ))
        }
    }.catch { exception ->
        emit(Result.Error(
            exception = exception as Exception,
            message = exception.localizedMessage ?: "Unknown error"
        ))
    }

    /**
     * Add item to cart or update quantity if exists.
     */
    override suspend fun addToCart(item: CartItem): Result<Unit> {
        return try {
            val existing = cartDao.getCartItemByProductId(item.productId)
            
            if (existing != null) {
                // Update quantity
                val updated = existing.copy(
                    quantity = existing.quantity + item.quantity,
                    updatedAt = System.currentTimeMillis()
                )
                cartDao.updateCartItem(updated)
            } else {
                // Insert new
                cartDao.insertCartItem(item.toEntity())
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error adding to cart")
            Result.Error(
                exception = e,
                message = "Failed to add item to cart"
            )
        }
    }

    /**
     * Remove item from cart by product ID.
     */
    override suspend fun removeFromCart(productId: String): Result<Unit> {
        return try {
            cartDao.deleteCartItem(productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error removing from cart")
            Result.Error(
                exception = e,
                message = "Failed to remove item from cart"
            )
        }
    }

    /**
     * Update cart item quantity.
     */
    override suspend fun updateQuantity(
        productId: String,
        quantity: Int
    ): Result<Unit> {
        return try {
            if (quantity <= 0) {
                cartDao.deleteCartItem(productId)
            } else {
                val item = cartDao.getCartItemByProductId(productId)
                item?.let {
                    val updated = it.copy(
                        quantity = quantity,
                        updatedAt = System.currentTimeMillis()
                    )
                    cartDao.updateCartItem(updated)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Failed to update quantity"
            )
        }
    }

    /**
     * Get cart total with optional discount and tax.
     */
    override suspend fun getCartTotal(
        discountPercent: Double = 0.0,
        taxPercent: Double = 9.0
    ): Result<CartTotal> {
        return try {
            val items = cartDao.getAllCartItemsSync()
            
            val subtotal = items.sumOf { item ->
                item.price * item.quantity
            }
            
            val discountAmount = subtotal * (discountPercent / 100)
            val afterDiscount = subtotal - discountAmount
            val taxAmount = afterDiscount * (taxPercent / 100)
            val total = afterDiscount + taxAmount
            
            Result.Success(
                CartTotal(
                    subtotal = subtotal,
                    discountAmount = discountAmount,
                    taxAmount = taxAmount,
                    total = total,
                    itemCount = items.size
                )
            )
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Failed to calculate cart total"
            )
        }
    }

    /**
     * Clear entire cart.
     */
    override suspend fun clearCart(): Result<Unit> {
        return try {
            cartDao.deleteAllItems()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Failed to clear cart"
            )
        }
    }

    /**
     * Get cart item count.
     */
    override suspend fun getCartItemCount(): Result<Int> {
        return try {
            val count = cartDao.getAllCartItemsSync().size
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Failed to get cart count"
            )
        }
    }
}

/**
 * Data class for cart totals calculation.
 */
data class CartTotal(
    val subtotal: Double,
    val discountAmount: Double,
    val taxAmount: Double,
    val total: Double,
    val itemCount: Int
)
