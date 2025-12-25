package com.noghre.sod.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Order status enum.
 */
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}

/**
 * Domain model representing an order item.
 * 
 * @property id Unique order item identifier
 * @property productId Reference to product
 * @property productName Name of product at time of order
 * @property quantity Number of items ordered
 * @property pricePerUnit Price per unit at time of order
 * @property subtotal Total for this item (quantity * pricePerUnit)
 * 
 * @since 1.0.0
 */
data class OrderItem(
    val id: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val pricePerUnit: BigDecimal,
    val subtotal: BigDecimal = BigDecimal.ZERO
) {
    
    /**
     * Calculate subtotal if not provided.
     */
    fun getSubtotal(): BigDecimal {
        return pricePerUnit * BigDecimal(quantity)
    }
}

/**
 * Shipping information for order.
 */
data class ShippingInfo(
    val address: String,
    val city: String,
    val country: String,
    val postalCode: String,
    val recipientName: String,
    val phoneNumber: String
)

/**
 * Domain model representing a complete order.
 * 
 * @property id Unique order identifier
 * @property userId User who placed the order
 * @property items List of items in order
 * @property subtotal Sum of all item prices
 * @property taxAmount Calculated tax
 * @property shippingCost Shipping fee
 * @property discountAmount Applied discount
 * @property total Final total amount
 * @property status Current order status
 * @property shippingInfo Shipping address details
 * @property trackingNumber Shipping tracking number
 * @property paymentMethod Payment method used
 * @property createdAt Order creation date
 * @property updatedAt Last modification date
 * @property deliveredAt Delivery completion date
 * 
 * @since 1.0.0
 */
data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem> = emptyList(),
    val subtotal: BigDecimal = BigDecimal.ZERO,
    val taxAmount: BigDecimal = BigDecimal.ZERO,
    val shippingCost: BigDecimal = BigDecimal.ZERO,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal = BigDecimal.ZERO,
    val status: OrderStatus = OrderStatus.PENDING,
    val shippingInfo: ShippingInfo,
    val trackingNumber: String? = null,
    val paymentMethod: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deliveredAt: LocalDateTime? = null
) {
    
    /**
     * Get total number of items in order.
     */
    fun getItemCount(): Int = items.sumOf { it.quantity }
    
    /**
     * Check if order can be cancelled.
     */
    fun canBeCancelled(): Boolean {
        return status in listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED)
    }
    
    /**
     * Check if order has been delivered.
     */
    fun isDelivered(): Boolean = status == OrderStatus.DELIVERED
    
    /**
     * Check if order is pending.
     */
    fun isPending(): Boolean = status == OrderStatus.PENDING
    
    /**
     * Get order processing time in days.
     */
    fun getProcessingDays(): Long? {
        return if (deliveredAt != null) {
            java.time.temporal.ChronoUnit.DAYS.between(createdAt, deliveredAt)
        } else {
            null
        }
    }
}
