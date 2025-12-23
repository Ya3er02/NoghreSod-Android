package com.noghre.sod.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val nameInFarsi: String,
    val price: Long,
    val weight: Double,
    val purity: String,
    val category: String,
    val description: String?,
    val images: String, // JSON array stored as string
    val thumbnailImage: String?,
    val stock: Int,
    val rating: Float = 0f,
    val isHandmade: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "product_reviews")
data class ProductReviewEntity(
    @PrimaryKey
    val id: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val rating: Float,
    val comment: String?,
    val createdAt: String,
)

@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey
    val productId: String,
    val addedAt: Long = System.currentTimeMillis(),
)
