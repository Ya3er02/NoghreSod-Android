package com.noghre.sod.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val orderNumber: String,
    val total: Long,
    val status: String,
    val paymentStatus: String,
    val shippingAddressId: String?,
    val items: String, // JSON array
    val estimatedDelivery: String?,
    val createdAt: String,
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "order_tracking")
data class OrderTrackingEntity(
    @PrimaryKey
    val orderId: String,
    val status: String,
    val events: String, // JSON array of tracking events
    val carrier: String?,
    val trackingNumber: String?,
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "return_requests")
data class ReturnRequestEntity(
    @PrimaryKey
    val id: String,
    val orderId: String,
    val reason: String,
    val status: String,
    val createdAt: String,
    val updatedAt: Long = System.currentTimeMillis(),
)
