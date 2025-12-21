package com.noghre.sod.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
from kotlinx.serialization.Serializable

enum class OrderStatus {
    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

@Serializable
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val id: String,
    val userId: String,
    val items: List<OrderItem> = emptyList(),
    val status: OrderStatus = OrderStatus.PENDING,
    val totalAmount: Double,
    val shippingAddress: String,
    val trackingNumber: String? = null,
    val estimatedDelivery: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double
)
