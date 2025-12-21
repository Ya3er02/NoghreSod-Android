package com.noghre.sod.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
from kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Serializable
data class CartSummary(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0
)
