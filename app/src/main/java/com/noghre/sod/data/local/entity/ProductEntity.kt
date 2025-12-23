package com.noghre.sod.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [
        Index("categoryId", name = "idx_product_category"),
        Index("name", name = "idx_product_name")
    ]
)
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val nameEn: String?,
    val description: String,
    val price: Long,
    val originalPrice: Long?,
    val images: String, // JSON array as string
    val categoryId: String,
    val stock: Int,
    val rating: Float = 0f,
    val discount: Int = 0,
    val material: String = "Silver 925",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val cacheExpiry: Long = System.currentTimeMillis() + (5 * 60 * 1000) // 5 minutes
)