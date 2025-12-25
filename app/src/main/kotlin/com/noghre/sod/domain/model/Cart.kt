package com.noghre.sod.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Domain model representing a shopping cart item.
 * 
 * @property id Unique cart item identifier
 * @property productId Reference to product
 * @property quantity Number of items in cart
 * @property addedAt When item was added to cart
 * 
 * @since 1.0.0
 */
data class CartItem(
    val id: String,
    val productId: String,
    val quantity: Int = 1,
    val addedAt: LocalDateTime = LocalDateTime.now()
) {
    
    /**
     * Validate cart item quantity.
     */
    fun isValid(): Boolean = quantity > 0 && productId.isNotBlank()
    
    /**
     * Increment quantity.
     */
    fun incrementQuantity(amount: Int = 1): CartItem {
        return copy(quantity = quantity + amount)
    }
    
    /**
     * Decrement quantity.
     */
    fun decrementQuantity(amount: Int = 1): CartItem {
        val newQuantity = (quantity - amount).coerceAtLeast(1)
        return copy(quantity = newQuantity)
    }
}

/**
 * Domain model representing the complete shopping cart.
 * 
 * @property id Unique cart identifier
 * @property userId User who owns this cart
 * @property items List of items in cart
 * @property subtotal Sum of all item prices
 * @property discountAmount Applied discount
 * @property taxAmount Calculated tax
 * @property total Final total amount
 * @property createdAt Cart creation date
 * @property updatedAt Last modification date
 * 
 * @since 1.0.0
 */
data class Cart(
    val id: String,
    val userId: String,
    val items: List<CartItem> = emptyList(),
    val subtotal: BigDecimal = BigDecimal.ZERO,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val taxAmount: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal = BigDecimal.ZERO,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    
    /**
     * Check if cart is empty.
     */
    fun isEmpty(): Boolean = items.isEmpty()
    
    /**
     * Get total number of items.
     */
    fun getItemCount(): Int = items.sumOf { it.quantity }
    
    /**
     * Add item to cart.
     */
    fun addItem(item: CartItem): Cart {
        val existingItem = items.find { it.productId == item.productId }
        val newItems = if (existingItem != null) {
            items.map { 
                if (it.productId == item.productId) 
                    it.copy(quantity = it.quantity + item.quantity)
                else it
            }
        } else {
            items + item
        }
        return copy(items = newItems, updatedAt = LocalDateTime.now())
    }
    
    /**
     * Remove item from cart.
     */
    fun removeItem(productId: String): Cart {
        return copy(
            items = items.filterNot { it.productId == productId },
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * Clear all items from cart.
     */
    fun clear(): Cart {
        return copy(
            items = emptyList(),
            subtotal = BigDecimal.ZERO,
            discountAmount = BigDecimal.ZERO,
            taxAmount = BigDecimal.ZERO,
            total = BigDecimal.ZERO,
            updatedAt = LocalDateTime.now()
        )
    }
}
