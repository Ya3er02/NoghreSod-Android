package com.noghre.sod.domain.repository

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for shopping cart operations
 */
interface CartRepository {
    /**
     * Get current user's cart
     */
    fun getCart(): Flow<Result<Cart>>

    /**
     * Get cart summary
     */
    fun getCartSummary(): Flow<Result<CartSummary>>

    /**
     * Add product to cart
     */
    suspend fun addToCart(
        productId: String,
        product: Product,
        quantity: Int,
    ): Result<CartItem>

    /**
     * Update cart item quantity
     */
    suspend fun updateCartItem(
        cartItemId: String,
        quantity: Int,
    ): Result<CartItem>

    /**
     * Remove item from cart
     */
    suspend fun removeFromCart(cartItemId: String): Result<Unit>

    /**
     * Clear entire cart
     */
    suspend fun clearCart(): Result<Unit>

    /**
     * Apply discount code to cart
     */
    suspend fun applyDiscountCode(code: String): Result<Cart>

    /**
     * Remove discount code from cart
     */
    suspend fun removeDiscountCode(): Result<Cart>

    /**
     * Calculate cart totals (including tax and shipping)
     */
    suspend fun calculateTotals(
        shippingAddress: Address? = null,
        discountCode: String? = null,
    ): Result<CartTotals>

    /**
     * Validate cart before checkout
     */
    suspend fun validateCart(): Result<CartValidation>

    /**
     * Get saved carts (wishlists)
     */
    fun getSavedCarts(): Flow<Result<List<SavedCart>>>

    /**
     * Create new saved cart
     */
    suspend fun createSavedCart(
        name: String,
        items: List<CartItem>,
        notes: String? = null,
    ): Result<SavedCart>

    /**
     * Load saved cart to active cart
     */
    suspend fun loadSavedCart(savedCartId: String): Result<Cart>

    /**
     * Delete saved cart
     */
    suspend fun deleteSavedCart(savedCartId: String): Result<Unit>

    /**
     * Share cart with code
     */
    suspend fun shareCart(savedCartId: String): Result<String> // Returns share code

    /**
     * Load shared cart
     */
    suspend fun loadSharedCart(shareCode: String): Result<SavedCart>
}

/**
 * Cart totals calculation result
 */
data class CartTotals(
    val subtotal: Long,
    val taxRate: Float,
    val taxAmount: Long,
    val shippingCost: Long,
    val discountAmount: Long,
    val total: Long,
    val totalWeight: Double,
)

/**
 * Cart validation result
 */
data class CartValidation(
    val isValid: Boolean,
    val errors: List<CartValidationError> = emptyList(),
    val warnings: List<CartValidationWarning> = emptyList(),
)

/**
 * Cart validation error
 */
data class CartValidationError(
    val code: String,
    val message: String,
    val severity: ValidationSeverity = ValidationSeverity.ERROR,
    val itemId: String? = null,
)

/**
 * Cart validation warning
 */
data class CartValidationWarning(
    val code: String,
    val message: String,
    val itemId: String? = null,
)

/**
 * Validation severity levels
 */
enum class ValidationSeverity {
    ERROR,
    WARNING,
    INFO,
}
