package com.noghre.sod.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing product information locally.
 * Includes caching metadata for offline-first strategy.
 *
 * @param id Unique product identifier
 * @param name Product name in Persian
 * @param nameEn Product name in English
 * @param description Product description
 * @param price Product price in Rials
 * @param images Comma-separated list of image URLs
 * @param categoryId Reference to category
 * @param weight Product weight in grams
 * @param purity Product purity percentage
 * @param stock Current stock quantity
 * @param createdAt Backend creation timestamp
 * @param updatedAt Backend last update timestamp
 * @param cachedAt Local cache timestamp for invalidation
 */
@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("id", unique = true),
        Index("categoryId"),
        Index("cachedAt"),
        Index("name")
    ]
)
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val nameEn: String? = null,
    val description: String,
    val price: Double,
    val images: String = "", // Stored as comma-separated
    val categoryId: String? = null,
    val weight: Double? = null,
    val purity: String? = null,
    val stock: Int = 0,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    // Caching metadata
    val cachedAt: Long = System.currentTimeMillis()
) {
    /**
     * Checks if this cached product is still valid.
     * Cache is considered valid for 24 hours.
     */
    fun isCacheValid(validityHours: Int = 24): Boolean {
        val cacheValidityMs = validityHours * 60 * 60 * 1000L
        return (System.currentTimeMillis() - cachedAt) < cacheValidityMs
    }
}
