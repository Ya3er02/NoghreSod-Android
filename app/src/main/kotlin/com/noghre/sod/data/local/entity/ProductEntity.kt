package com.noghre.sod.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for Product local caching.
 * Stores product data for offline access and performance optimization.
 */
@Entity(
    tableName = "products",
    indices = [
        Index("category_id"),
        Index("is_favorite"),
        Index("cached_at")
    ]
)
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "name_en")
    val nameEn: String?,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "description_en")
    val descriptionEn: String?,

    @ColumnInfo(name = "price")
    val price: Double,

    @ColumnInfo(name = "discount_price")
    val discountPrice: Double?,

    @ColumnInfo(name = "images")
    val images: String, // JSON serialized list

    @ColumnInfo(name = "category_id")
    val categoryId: String,

    @ColumnInfo(name = "stock")
    val stock: Int,

    @ColumnInfo(name = "rating")
    val rating: Float,

    @ColumnInfo(name = "review_count")
    val reviewCount: Int,

    @ColumnInfo(name = "weight")
    val weight: Double?,

    @ColumnInfo(name = "material")
    val material: String,

    @ColumnInfo(name = "specifications")
    val specifications: String?, // JSON serialized map

    @ColumnInfo(name = "seller_id")
    val sellerId: String,

    @ColumnInfo(name = "seller_name")
    val sellerName: String?,

    @ColumnInfo(name = "seller_rating")
    val sellerRating: Float?,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: String?,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String?,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)
