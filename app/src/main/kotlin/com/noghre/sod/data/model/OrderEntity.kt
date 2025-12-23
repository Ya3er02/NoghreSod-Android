package com.noghre.sod.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.noghre.sod.data.utils.Converters

/**
 * Order Entity for Room Database
 * Represents a customer order with tracking and payment information
 */
@Entity(tableName = "orders")
@TypeConverters(Converters::class)
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val orderNumber: String, // Human-readable order number
    val status: String = "PENDING", // PENDING, CONFIRMED, PREPARING, READY, SHIPPED, DELIVERED
    val totalWeight: Double = 0.0,
    val subtotal: Long,
    val laborCost: Long = 0,
    val tax: Long = 0,
    val shippingCost: Long = 0,
    val discountAmount: Long = 0,
    val total: Long,
    val paymentMethod: String = "WALLET", // WALLET, TRANSFER, CARD
    val paymentStatus: String = "UNPAID", // UNPAID, PAID, REFUNDED
    val paymentToken: String? = null,
    val shippingAddress: String,
    val phoneNumber: String,
    val recipientName: String,
    val trackingCode: String? = null,
    val estimatedDeliveryDate: Long? = null,
    val actualDeliveryDate: Long? = null,
    val items: List<OrderItemEntity> = emptyList(),
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val lastSyncedAt: Long = System.currentTimeMillis()
) {
    val grandTotal: Long
        get() = subtotal + laborCost + tax + shippingCost - discountAmount

    val itemCount: Int
        get() = items.sumOf { it.quantity }
}

/**
 * Order Item Entity
 * Represents individual products in an order
 */
@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["orderId"]),
        Index(value = ["productId"])
    ]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: String,
    val productId: String,
    val productName: String,
    val productImage: String,
    val purity: String,
    val quantity: Int,
    val weight: Double,
    val unitPrice: Long,
    val laborCost: Long = 0,
    val discount: Int = 0
) {
    val totalPrice: Long
        get() = ((unitPrice * (100 - discount) / 100) + laborCost) * quantity
}

/**
 * Order Status History Entity
 * Tracks status changes throughout order lifecycle
 */
@Entity(
    tableName = "order_status_history",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["orderId"])
    ]
)
data class OrderStatusHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: String,
    val status: String,
    val description: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
