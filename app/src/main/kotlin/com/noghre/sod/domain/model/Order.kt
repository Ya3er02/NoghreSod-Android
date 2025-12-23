package com.noghre.sod.domain.model

import java.time.LocalDateTime

/**
 * Order representing a completed purchase
 */
data class Order(
    val orderId: String,
    val userId: String,
    val orderNumber: String, // Customer-friendly order number
    val items: List<OrderItem>,
    val shippingAddress: Address,
    val billingAddress: Address? = null,
    val status: OrderStatus,
    val paymentStatus: PaymentStatus,
    val paymentMethod: PaymentMethod,
    val subtotal: Long, // Price before tax and shipping
    val taxAmount: Long,
    val shippingCost: Long,
    val discountAmount: Long = 0,
    val discountCode: String? = null,
    val total: Long, // Final amount
    val totalWeight: Double,
    val totalQuantity: Int,
    val paymentTransactionId: String? = null,
    val trackingCode: String? = null, // Shipping tracking number
    val carrier: ShippingCarrier? = null,
    val notes: String? = null,
    val adminNotes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val estimatedDeliveryDate: LocalDateTime? = null,
    val actualDeliveryDate: LocalDateTime? = null,
    val cancelledAt: LocalDateTime? = null,
    val cancellationReason: String? = null,
    val events: List<OrderEvent> = emptyList(),
    val returnRequest: ReturnRequest? = null,
)

/**
 * Item in an order
 */
data class OrderItem(
    val orderItemId: String,
    val orderId: String,
    val productId: String,
    val productName: String,
    val productImage: String?,
    val quantity: Int,
    val weight: Double,
    val purity: PurityType,
    val unitPrice: Long,
    val totalPrice: Long,
    val laborCost: Long = 0,
    val sku: String? = null,
    val notes: String? = null,
)

/**
 * Order status
 */
enum class OrderStatus {
    PENDING,          // در انتظار
    CONFIRMED,        // تایيد شده
    PREPARING,        // در حال آماده
    READY,            // آماده آرسال
    SHIPPED,          // ارسال شده
    IN_TRANSIT,       // در مسیر
    OUT_FOR_DELIVERY, // در انتظار تحويل
    DELIVERED,        // تحويل اشده
    CANCELLED,        // لغو شده
    RETURNED,         // برگردان شده
    FAILED,           // ناباء برابر
}

/**
 * Payment status
 */
enum class PaymentStatus {
    PENDING,      // In progress
    PROCESSING,   // Payment is being processed
    COMPLETED,    // Successfully paid
    FAILED,       // Payment failed
    CANCELLED,    // Payment cancelled
    REFUNDED,     // Refunded
    PARTIALLY_REFUNDED, // Partially refunded
}

/**
 * Payment method
 */
enum class PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    BANK_TRANSFER,
    ONLINE_GATEWAY, // Zarinpal, IDPay, etc.
    WALLET,
    CASH_ON_DELIVERY,
    INSTALLMENT,
}

/**
 * Shipping carrier
 */
enum class ShippingCarrier {
    Iran_POST,
    TCI,
    SNAPP,
    LOGIC,
    TIPAX,
    PICKUP, // Self-pickup
}

/**
 * Order event for tracking
 */
data class OrderEvent(
    val eventId: String,
    val orderId: String,
    val eventType: OrderEventType,
    val status: OrderStatus,
    val description: String,
    val metadata: Map<String, String>? = null,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)

/**
 * Order event types
 */
enum class OrderEventType {
    CREATED,
    CONFIRMED,
    PAYMENT_RECEIVED,
    PREPARING,
    READY,
    PICKED,
    SHIPPED,
    TRACKING_UPDATE,
    DELIVERED,
    CANCELLED,
    RETURNED,
    ERROR,
}

/**
 * Return request for order items
 */
data class ReturnRequest(
    val returnId: String,
    val orderId: String,
    val items: List<OrderItem>,
    val reason: ReturnReason,
    val description: String,
    val returnStatus: ReturnStatus,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val approvedAt: LocalDateTime? = null,
    val returnedAt: LocalDateTime? = null,
    val refundAmount: Long? = null,
    val refundDate: LocalDateTime? = null,
)

/**
 * Return reason
 */
enum class ReturnReason {
    DEFECTIVE,
    DAMAGED,
    NOT_AS_DESCRIBED,
    UNWANTED,
    DIFFERENT_PURITY,
    WEIGHT_MISMATCH,
    OTHER,
}

/**
 * Return status
 */
enum class ReturnStatus {
    REQUESTED,      // Return requested
    APPROVED,       // Approved for return
    REJECTED,       // Not approved
    IN_TRANSIT,     // Item in transit to warehouse
    RECEIVED,       // Item received at warehouse
    REFUNDED,       // Refund processed
    CANCELLED,      // Return cancelled
}

/**
 * Order summary for list display
 */
data class OrderSummary(
    val orderId: String,
    val orderNumber: String,
    val total: Long,
    val status: OrderStatus,
    val paymentStatus: PaymentStatus,
    val createdAt: LocalDateTime,
    val estimatedDeliveryDate: LocalDateTime?,
    val itemCount: Int,
)

/**
 * Order filter criteria
 */
data class OrderFilter(
    val status: OrderStatus? = null,
    val paymentStatus: PaymentStatus? = null,
    val fromDate: LocalDateTime? = null,
    val toDate: LocalDateTime? = null,
    val minAmount: Long? = null,
    val maxAmount: Long? = null,
)
