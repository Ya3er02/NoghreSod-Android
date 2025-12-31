package com.noghre.sod.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * CartEntity - Database representation of a shopping cart item.
 * 
 * Represents items in the user's shopping cart with quantity and variant info.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Entity(
    tableName = "cart_items",
    indices = [
        Index(value = ["productId"], unique = false),
        Index(value = ["userId"], unique = false)
    ]
)
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val userId: String,
    val productId: String,
    
    // Item details
    val productName: String,
    val productPrice: Double,
    val imageUrl: String? = null,
    
    // Quantity and variants
    val quantity: Int = 1,
    val selectedSize: String? = null,
    val selectedColor: String? = null,
    val selectedVariant: String? = null,
    
    // Pricing snapshot
    val pricePerUnit: Double = productPrice,
    val subtotal: Double = productPrice * quantity,
    val discount: Double? = null,
    val discountPercentage: Double? = null,
    
    // Item state
    val isAvailable: Boolean = true,
    val addedAt: Date = Date(),
    val lastUpdatedAt: Date = Date()
)
