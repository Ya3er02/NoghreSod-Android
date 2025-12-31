package com.noghre.sod.data.repository.cart

import com.noghre.sod.core.result.Result
import com.noghre.sod.domain.model.Cart
import com.noghre.sod.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for cart operations.
 *
 * Defines contract for:
 * - Retrieving user cart
 * - Adding items to cart
 * - Updating cart items
 * - Removing items from cart
 * - Clearing entire cart
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
interface ICartRepository {

    /**
     * Get current user's shopping cart.
     *
     * @return Flow emitting Result with Cart or error
     */
    fun getCart(): Flow<Result<Cart>>

    /**
     * Add a product to cart.
     *
     * @param productId ID of product to add
     * @param quantity Quantity to add
     * @param selectedColor Optional selected color variant
     * @param selectedSize Optional selected size variant
     * @return Flow emitting Result with updated Cart
     */
    suspend fun addToCart(
        productId: String,
        quantity: Int,
        selectedColor: String? = null,
        selectedSize: String? = null
    ): Result<Cart>

    /**
     * Update quantity of item in cart.
     *
     * @param itemId Cart item ID
     * @param quantity New quantity
     * @return Flow emitting Result with updated Cart
     */
    suspend fun updateCartItem(
        itemId: String,
        quantity: Int
    ): Result<Cart>

    /**
     * Remove single item from cart.
     *
     * @param itemId Cart item ID to remove
     * @return Flow emitting Result with updated Cart
     */
    suspend fun removeFromCart(itemId: String): Result<Cart>

    /**
     * Clear all items from cart.
     *
     * @return Flow emitting Result<Unit> indicating success or failure
     */
    suspend fun clearCart(): Result<Unit>

    /**
     * Get cart locally without network call.
     * Useful for optimistic UI updates.
     *
     * @return Cart if available locally, null otherwise
     */
    suspend fun getCartLocally(): Cart?
}
