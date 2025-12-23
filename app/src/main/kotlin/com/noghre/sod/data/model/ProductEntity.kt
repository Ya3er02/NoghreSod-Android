package com.noghre.sod.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.noghre.sod.data.utils.Converters

/**
 * Room Database Entity for Product
 * Represents a silver jewelry product in local storage
 *
 * Special fields for jewelry:
 * - [purity]: Silver purity (925, 950, 999)
 * - [weight]: Product weight in grams
 * - [laborCost]: Manual labor cost in Toman
 */
@Entity(tableName = "products")
@TypeConverters(Converters::class)
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val nameFA: String,
    val description: String,
    val descriptionFA: String?,
    val price: Long, // Price in Toman (تومان)
    val purity: String, // 925, 950, 999
    val weight: Double, // Weight in grams
    val laborCost: Long = 0, // Labor cost in Toman
    val categoryId: String,
    val categoryName: String,
    val stock: Int,
    val images: List<String>,
    val isFavorite: Boolean = false,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val discount: Int = 0, // Discount percentage
    val isNew: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
    val lastSyncedAt: Long = System.currentTimeMillis()
) {
    val discountedPrice: Long
        get() = if (discount > 0) (price * (100 - discount) / 100) else price

    val totalPrice: Long
        get() = discountedPrice + laborCost
}
