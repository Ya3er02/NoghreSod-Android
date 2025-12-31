package com.noghre.sod.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * OrderEntity - Database representation of an order.
 * 
 * Stores order information for tracking and history.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Entity(
    tableName = "orders",
    indices = [
        Index(value = ["userId"], unique = false),
        Index(value = ["orderNumber"], unique = true),
        Index(value = ["status"], unique = false)
    ]
)
data class OrderEntity(
    @PrimaryKey
    val id: String,
    
    val userId: String,
    val orderNumber: String,
    
    // Order items (stored as JSON)
    val itemsJson: String,
    val itemCount: Int = 0,
    
    // Pricing
    val subtotal: Double = 0.0,
    val shippingCost: Double = 0.0,
    val tax: Double = 0.0,
    val discount: Double? = null,
    val discountCode: String? = null,
    val total: Double = 0.0,
    
    // Shipping info
    val shippingAddress: String? = null,
    val shippingMethod: String? = null,
    val trackingNumber: String? = null,
    val estimatedDeliveryDate: Date? = null,
    
    // Payment info
    val paymentMethod: String? = null,
    val paymentStatus: String = "pending", // pending, completed, failed, refunded
    val transactionId: String? = null,
    val paidAt: Date? = null,
    
    // Status
    val status: String = "pending", // pending, confirmed, shipped, delivered, cancelled, returned
    val statusHistory: String? = null, // JSON array of status changes
    
    // Notes
    val notes: String? = null,
    val customerNotes: String? = null,
    
    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val shippedAt: Date? = null,
    val deliveredAt: Date? = null,
    
    // Sync
    val isSynced: Boolean = false,
    val lastSyncedAt: Date? = null
)
