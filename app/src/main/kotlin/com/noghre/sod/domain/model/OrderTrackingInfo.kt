package com.noghre.sod.domain.model

data class OrderTrackingInfo(
    val orderId: String,
    val currentStatus: OrderStatus,
    val statusHistory: List<StatusHistory>,
    val estimatedDelivery: Long?
)

data class StatusHistory(
    val status: OrderStatus,
    val timestamp: Long,
    val description: String
)

enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED, REFUNDED
}
