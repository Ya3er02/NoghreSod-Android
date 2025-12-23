package com.noghre.sod.domain.model

import java.time.LocalDateTime

/**
 * Shopping cart item
 * Specialized for jewelry with weight and purity tracking
 */
data class CartItem(
    val cartItemId: String? = null,
    val productId: String,
    val product: Product? = null, // Full product details (optional, for display)
    val quantity: Int,
    val totalWeight: Double, // Total weight in grams (quantity * product weight)
    val purity: PurityType,
    val laborCost: Long = 0, // Labor cost in Toman
    val unitPrice: Long, // Price per unit
    val totalPrice: Long, // Unit price * quantity
    val addedAt: LocalDateTime = LocalDateTime.now(),
) {
    /**
     * Calculate total item cost including labor
     */
    val totalCostWithLabor: Long
        get() = totalPrice + (laborCost * quantity)
}

/**
 * Shopping cart for a user
 */
data class Cart(
    val cartId: String? = null,
    val userId: String,
    val items: List<CartItem> = emptyList(),
    val subtotal: Long = 0, // Total before tax and shipping
    val taxAmount: Long = 0,
    val taxRate: Float = 0.09f, // Default 9% VAT in Iran
    val shippingCost: Long = 0,
    val discountAmount: Long = 0,
    val discountCode: String? = null,
    val total: Long = 0, // Final total
    val totalWeight: Double = 0.0, // Total weight of all items
    val totalQuantity: Int = 0, // Total quantity of items
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null, // Cart expiration time (for session carts)
) {
    /**
     * Check if cart is empty
     */
    val isEmpty: Boolean
        get() = items.isEmpty()

    /**
     * Check if cart is expired
     */
    fun isExpired(): Boolean {
        return expiresAt?.let { LocalDateTime.now().isAfter(it) } ?: false
    }

    /**
     * Get items grouped by purity
     */
    fun groupByPurity(): Map<PurityType, List<CartItem>> {
        return items.groupBy { it.purity }
    }
}

/**
 * Cart summary for quick view
 */
data class CartSummary(
    val itemCount: Int,
    val totalWeight: Double,
    val subtotal: Long,
    val total: Long,
    val hasDiscountCode: Boolean,
)

/**
 * Cart update request
 */
data class CartUpdateRequest(
    val cartItemId: String? = null,
    val productId: String? = null,
    val quantity: Int = 1,
    val action: CartAction,
)

/**
 * Cart action types
 */
enum class CartAction {
    ADD,        // Add item to cart
    UPDATE,     // Update quantity
    REMOVE,     // Remove item from cart
    CLEAR,      // Clear entire cart
    APPLY_DISCOUNT, // Apply discount code
    REMOVE_DISCOUNT, // Remove discount code
}

/**
 * Saved cart for wishlist/later purchase
 */
data class SavedCart(
    val savedCartId: String,
    val userId: String,
    val name: String, // User-given name for this cart
    val items: List<CartItem>,
    val totalPrice: Long,
    val notes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val isPublic: Boolean = false, // Can be shared with others
    val shareCode: String? = null, // Code to share this cart
)
