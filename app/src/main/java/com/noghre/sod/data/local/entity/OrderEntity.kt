package com.noghre.sod.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId", name = "idx_order_user"),
        Index("status", name = "idx_order_status")
    ]
)
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val orderNumber: String,
    val items: String, // JSON array
    val totalAmount: Long,
    val discountAmount: Long = 0,
    val shippingCost: Long = 0,
    val taxAmount: Long = 0,
    val status: String,
    val paymentMethod: String?,
    val trackingCode: String?,
    val deliveryDate: Long?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val notes: String?,
    val shippingAddress: String?
)