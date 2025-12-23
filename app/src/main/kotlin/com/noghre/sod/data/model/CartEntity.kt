package com.noghre.sod.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Cart Item Entity for Room Database
 * Represents a product added to user's shopping cart
 */
@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"])
    ]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: String,
    val quantity: Int,
    val weight: Double, // Total weight for this item
    val unitPrice: Long,
    val laborCost: Long = 0,
    val addedAt: Long = System.currentTimeMillis()
) {
    val totalPrice: Long
        get() = (unitPrice + laborCost) * quantity
}

/**
 * Cart Entity representing user's shopping cart
 */
@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey
    val id: String = "user_cart",
    val totalItems: Int = 0,
    val totalWeight: Double = 0.0,
    val subtotal: Long = 0,
    val tax: Long = 0,
    val shippingCost: Long = 0,
    val discountAmount: Long = 0,
    val total: Long = 0,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val grandTotal: Long
        get() = subtotal + tax + shippingCost - discountAmount
}
