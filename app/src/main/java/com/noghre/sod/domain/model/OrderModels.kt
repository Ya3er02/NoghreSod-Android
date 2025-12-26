package com.noghre.sod.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

// ============== ORDER ==============

/**
 * Order domain model.
 * Contains complete order information including items, tracking, and payment.
 */
data class Order(
    val id: String,
    val orderNumber: String,
    val userId: String,
    val items: List<OrderItem>,
    val shippingAddress: Address,
    val paymentMethod: PaymentMethod,
    val summary: OrderSummary,
    val status: OrderStatus,
    val tracking: OrderTracking?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    init {
        require(id.isNotBlank()) { "Order ID cannot be blank" }
        require(orderNumber.isNotBlank()) { "Order number cannot be blank" }
        require(userId.isNotBlank()) { "User ID cannot be blank" }
        require(items.isNotEmpty()) { "Order must have at least one item" }
    }
}

// ============== ORDER ITEM ==============

/**
 * Order item containing product and quantity information.
 * Records the item as it was at time of purchase.
 */
data class OrderItem(
    val id: String,
    val product: ProductSummary,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val variant: ProductVariant? = null
) {
    init {
        require(id.isNotBlank()) { "Order item ID cannot be blank" }
        require(quantity > 0) { "Quantity must be positive" }
        require(unitPrice >= BigDecimal.ZERO) { "Unit price cannot be negative" }
    }

    val subtotal: BigDecimal
        get() = unitPrice.multiply(quantity.toBigDecimal())
}

// ============== ORDER SUMMARY ==============

/**
 * Order summary with pricing breakdown.
 * Records financial information at time of order placement.
 */
data class OrderSummary(
    val subtotal: BigDecimal,
    val discount: BigDecimal,
    val shipping: BigDecimal,
    val tax: BigDecimal,
    val total: BigDecimal
) {
    init {
        require(subtotal >= BigDecimal.ZERO) { "Subtotal cannot be negative" }
        require(discount >= BigDecimal.ZERO) { "Discount cannot be negative" }
        require(shipping >= BigDecimal.ZERO) { "Shipping cost cannot be negative" }
        require(tax >= BigDecimal.ZERO) { "Tax cannot be negative" }
        require(total >= BigDecimal.ZERO) { "Total cannot be negative" }
    }
}

// ============== ORDER STATUS ==============

/**
 * Order status enumeration.
 * Represents different stages of an order lifecycle.
 */
enum class OrderStatus(val displayName: String, val displayNameFa: String) {
    PENDING_PAYMENT(
        "Pending Payment",
        "در انتظار پرداخت"
    ),
    PAYMENT_CONFIRMED(
        "Payment Confirmed",
        "پرداخت تایید شد"
    ),
    PROCESSING(
        "Processing",
        ی
        "در حال اماده‌سازی"
    ),
    SHIPPED(
        "Shipped",
        "خروج رفته"
    ),
    DELIVERED(
        "Delivered",
        "تحویل داده شد"
    ),
    CANCELLED(
        "Cancelled",
        "لغو شده"
    ),
    REFUNDED(
        "Refunded",
        "به عقب برگردانده شد"
    );

    val isFinal: Boolean
        get() = this in listOf(DELIVERED, CANCELLED, REFUNDED)

    val isShipped: Boolean
        get() = this in listOf(SHIPPED, DELIVERED)
}

// ============== ORDER TRACKING ==============

/**
 * Order tracking information.
 * Contains carrier details and current location.
 */
data class OrderTracking(
    val trackingNumber: String,
    val carrier: String,
    val estimatedDelivery: LocalDateTime,
    val currentStatus: String,
    val events: List<TrackingEvent>
) {
    init {
        require(trackingNumber.isNotBlank()) { "Tracking number cannot be blank" }
        require(carrier.isNotBlank()) { "Carrier cannot be blank" }
    }
}

// ============== TRACKING EVENT ==============

/**
 * Tracking event for order shipment.
 * Records status updates along the delivery route.
 */
data class TrackingEvent(
    val status: String,
    val description: String,
    val location: String,
    val timestamp: LocalDateTime
) {
    init {
        require(status.isNotBlank()) { "Status cannot be blank" }
        require(location.isNotBlank()) { "Location cannot be blank" }
    }
}

// ============== PAYMENT METHOD ==============

enum class PaymentMethod(val displayName: String, val displayNameFa: String) {
    ONLINE_GATEWAY(
        "Online Payment",
        "لذاذک الکترونی"
    ),
    BANK_TRANSFER(
        "Bank Transfer",
        "انتقال بانکی"
    ),
    CASH_ON_DELIVERY(
        "Cash on Delivery",
        "پرداخت در محل"
    )
}
