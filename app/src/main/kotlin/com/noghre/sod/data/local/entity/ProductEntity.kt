package com.noghre.sod.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Room database entity for products.
 * 
 * Includes proper indexing for efficient queries and constraints for data integrity.
 * 
 * Indexes:
 * - category: For category filtering queries
 * - inStock: For stock status queries
 * - rating: For sorting by rating
 * - createdAt: For chronological queries
 * 
 * @property id Primary key - unique product identifier
 * @property name Product name
 * @property description Full product description
 * @property price Product price (stored as string in SQLite, converted via TypeConverter)
 * @property originalPrice Price before discount
 * @property discountPercentage Discount percentage (0-100)
 * @property category Product category for filtering
 * @property imageUrls Serialized image URLs (JSON string)
 * @property rating Average rating (1-5)
 * @property totalReviews Count of reviews
 * @property inStock Stock availability
 * @property weight Product weight in grams
 * @property material Material composition
 * @property dimensions Product dimensions
 * @property tags Serialized tags list (JSON)
 * @property sku Stock keeping unit
 * @property createdAt Creation timestamp
 * @property lastUpdated Last modification timestamp
 * @property isFeatured Whether product is featured
 * @property isNew Whether product is newly added
 * 
 * @since 1.0.0
 */
@Entity(
    tableName = "products",
    indices = [
        Index(name = "idx_category", value = ["category"]),
        Index(name = "idx_in_stock", value = ["inStock"]),
        Index(name = "idx_rating", value = ["rating"]),
        Index(name = "idx_created_at", value = ["createdAt"]),
        Index(name = "idx_sku", value = ["sku"], unique = true)
    ]
)
data class ProductEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "price")
    val price: BigDecimal,
    
    @ColumnInfo(name = "original_price")
    val originalPrice: BigDecimal? = null,
    
    @ColumnInfo(name = "discount_percentage")
    val discountPercentage: BigDecimal = BigDecimal.ZERO,
    
    @ColumnInfo(name = "category")
    val category: String,
    
    @ColumnInfo(name = "image_urls")
    val imageUrls: List<String>,
    
    @ColumnInfo(name = "rating")
    val rating: Double = 0.0,
    
    @ColumnInfo(name = "total_reviews")
    val totalReviews: Int = 0,
    
    @ColumnInfo(name = "inStock")
    val inStock: Boolean,
    
    @ColumnInfo(name = "weight")
    val weight: BigDecimal? = null,
    
    @ColumnInfo(name = "material")
    val material: String? = null,
    
    @ColumnInfo(name = "dimensions")
    val dimensions: String? = null,
    
    @ColumnInfo(name = "tags")
    val tags: List<String> = emptyList(),
    
    @ColumnInfo(name = "sku")
    val sku: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "last_updated")
    val lastUpdated: LocalDateTime,
    
    @ColumnInfo(name = "is_featured")
    val isFeatured: Boolean = false,
    
    @ColumnInfo(name = "is_new")
    val isNew: Boolean = false
)

/**
 * Cart item entity with relationships.
 */
@Entity(
    tableName = "cart_items",
    indices = [
        Index(name = "idx_cart_product_id", value = ["productId"])
    ],
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "productId")
    val productId: String,
    
    @ColumnInfo(name = "quantity")
    val quantity: Int,
    
    @ColumnInfo(name = "added_at")
    val addedAt: LocalDateTime,
    
    @ColumnInfo(name = "selected")
    val selected: Boolean = true
)

/**
 * Order entity with metadata.
 */
@Entity(
    tableName = "orders",
    indices = [
        Index(name = "idx_order_status", value = ["status"]),
        Index(name = "idx_order_date", value = ["createdAt"])
    ]
)
data class OrderEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "total_amount")
    val totalAmount: BigDecimal,
    
    @ColumnInfo(name = "status")
    val status: String,
    
    @ColumnInfo(name = "items")
    val items: List<String>, // Serialized product IDs
    
    @ColumnInfo(name = "shipping_address")
    val shippingAddress: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime
)
