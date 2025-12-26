package com.noghre.sod.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

// ============== CART ==============

/**
 * Shopping cart domain model.
 * Contains all cart information and items.
 */
data class Cart(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val summary: CartSummary,
    val updatedAt: LocalDateTime
) {
    init {
        require(id.isNotBlank()) { "Cart ID cannot be blank" }
        require(userId.isNotBlank()) { "User ID cannot be blank" }
    }

    val isEmpty: Boolean
        get() = items.isEmpty()

    val itemCount: Int
        get() = items.sumOf { it.quantity }
}

// ============== CART ITEM ==============

/**
 * Cart item containing product and quantity information.
 * Includes variant selection and calculation of subtotal.
 */
data class CartItem(
    val id: String,
    val product: ProductSummary,
    val quantity: Int,
    val variant: ProductVariant? = null,
    val addedAt: LocalDateTime
) {
    init {
        require(id.isNotBlank()) { "Cart item ID cannot be blank" }
        require(quantity > 0) { "Quantity must be positive" }
    }

    val subtotal: BigDecimal
        get() = product.price.amount.multiply(quantity.toBigDecimal())
}

// ============== CART SUMMARY ==============

/**
 * Cart summary with pricing breakdown.
 * Includes subtotal, discounts, shipping, tax, and total.
 */
data class CartSummary(
    val subtotal: BigDecimal,
    val discount: BigDecimal = BigDecimal.ZERO,
    val shipping: BigDecimal = BigDecimal.ZERO,
    val tax: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal,
    val appliedCoupon: Coupon? = null
) {
    init {
        require(subtotal >= BigDecimal.ZERO) { "Subtotal cannot be negative" }
        require(discount >= BigDecimal.ZERO) { "Discount cannot be negative" }
        require(shipping >= BigDecimal.ZERO) { "Shipping cost cannot be negative" }
        require(tax >= BigDecimal.ZERO) { "Tax cannot be negative" }
        require(total >= BigDecimal.ZERO) { "Total cannot be negative" }
    }

    val effectiveDiscount: BigDecimal
        get() = discount + (appliedCoupon?.calculatedDiscount ?: BigDecimal.ZERO)
}

// ============== COUPON ==============

/**
 * Discount coupon.
 * Can be percentage-based or fixed amount.
 */
data class Coupon(
    val code: String,
    val type: CouponType,
    val value: BigDecimal,
    val minPurchase: BigDecimal? = null,
    val maxDiscount: BigDecimal? = null,
    val expiresAt: LocalDateTime
) {
    init {
        require(code.isNotBlank()) { "Coupon code cannot be blank" }
        require(value > BigDecimal.ZERO) { "Coupon value must be positive" }
    }

    val isExpired: Boolean
        get() = LocalDateTime.now().isAfter(expiresAt)

    /**
     * Calculate discount amount for given subtotal.
     */
    fun calculateDiscount(subtotal: BigDecimal): BigDecimal {
        // Check minimum purchase requirement
        minPurchase?.let {
            if (subtotal < it) return BigDecimal.ZERO
        }

        // Calculate discount based on type
        val calculatedDiscount = when (type) {
            CouponType.PERCENTAGE ->
                subtotal.multiply(value).divide(BigDecimal(100))
            CouponType.FIXED_AMOUNT ->
                value
        }

        // Apply maximum discount limit if specified
        return if (maxDiscount != null && calculatedDiscount > maxDiscount) {
            maxDiscount
        } else {
            calculatedDiscount
        }
    }

    val calculatedDiscount: BigDecimal
        get() = value
}

enum class CouponType {
    PERCENTAGE,
    FIXED_AMOUNT
}
