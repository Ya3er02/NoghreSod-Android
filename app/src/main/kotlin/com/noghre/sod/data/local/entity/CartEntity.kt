package com.noghre.sod.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey
    val id: String = "user_cart",
    val subtotal: Long = 0,
    val tax: Long = 0,
    val shipping: Long = 0,
    val discount: Long = 0,
    val total: Long = 0,
    val discountCode: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    val id: String,
    val productId: String,
    val quantity: Int,
    val price: Long,
    val totalPrice: Long,
    val addedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "saved_carts")
data class SavedCartEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val items: String, // JSON array of cart items
    val total: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
