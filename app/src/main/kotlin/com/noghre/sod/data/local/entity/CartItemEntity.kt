package com.noghre.sod.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Cart Items (local cart storage).
 */
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "product_id")
    val productId: String,

    @ColumnInfo(name = "product_name")
    val productName: String,

    @ColumnInfo(name = "product_image")
    val productImage: String?,

    @ColumnInfo(name = "quantity")
    val quantity: Int,

    @ColumnInfo(name = "price_at_time")
    val priceAtTime: Double,

    @ColumnInfo(name = "subtotal")
    val subtotal: Double,

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis()
)
