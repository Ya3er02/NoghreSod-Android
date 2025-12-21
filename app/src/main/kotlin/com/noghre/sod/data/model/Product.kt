package com.noghre.sod.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
from kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val category: String,
    val imageUrl: String,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val inStock: Boolean = true,
    val sellerId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
