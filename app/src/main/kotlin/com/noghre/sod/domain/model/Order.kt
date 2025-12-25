package com.noghre.sod.domain.model

/**
 * Domain model for Order.
 * Represents a completed purchase order.
 *
 * @property id Order identifier
 * @property userId User ID who placed the order
 * @property orderNumber Human-readable order number
 * @property items List of items in order
 * @property shippingAddress Delivery address
 * @property paymentMethod Payment method used
 * @property subtotal Subtotal before discounts
 * @property shippingCost Shipping cost
 * @property discountAmount Discount applied
 * @property totalAmount Final total amount
 * @property status Order status
 * @property tracking Tracking information
 * @property notes Special notes about order
 * @property createdAt Order creation timestamp
 * @property updatedAt Last update timestamp
 */
data class Order(
    val id: String,
    val userId: String,
    val orderNumber: String,
    val items: List<OrderItem>,
    val shippingAddress: Address,
    val paymentMethod: PaymentMethod,
    val subtotal: Double,
    val shippingCost: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double,
    val status: OrderStatus = OrderStatus.PENDING,
    val tracking: OrderTracking? = null,
    val notes: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

/**
 * Order item model.
 * Represents a product in an order.
 */
data class OrderItem(
    val id: String,
    val product: Product,
    val quantity: Int,
    val unitPrice: Double,
    val selectedColor: String? = null,
    val selectedSize: String? = null
)

/**
 * Order status enum.
 */
enum class OrderStatus {
    PENDING,        // Awaiting payment
    CONFIRMED,      // Payment received
    PROCESSING,     // Being prepared
    SHIPPED,        // On the way
    DELIVERED,      // Successfully delivered
    CANCELLED,      // Order cancelled
    REFUNDED        // Refund issued
}

/**
 * Order tracking information.
 *
 * @property trackingNumber Tracking number
 * @property carrier Shipping carrier name
 * @property estimatedDelivery Estimated delivery date
 * @property currentLocation Current location of shipment
 * @property events List of tracking events
 */
data class OrderTracking(
    val trackingNumber: String,
    val carrier: String,
    val estimatedDelivery: String,
    val currentLocation: String,
    val events: List<TrackingEvent> = emptyList()
)

/**
 * Tracking event.
 *
 * @property status Status at this point
 * @property timestamp When this event occurred
 * @property location Location of event
 * @property description Description of event
 */
data class TrackingEvent(
    val status: String,
    val timestamp: String,
    val location: String,
    val description: String
)
