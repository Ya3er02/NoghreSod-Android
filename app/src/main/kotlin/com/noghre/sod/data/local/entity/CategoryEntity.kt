package com.noghre.sod.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing product categories locally.
 *
 * @param id Unique category identifier
 * @param name Category name in Persian
 * @param nameEn Category name in English
 * @param slug URL-friendly slug for the category
 * @param icon URL to category icon
 * @param cachedAt Local cache timestamp for invalidation
 */
@Entity(
    tableName = "categories",
    indices = [
        Index("id", unique = true),
        Index("slug", unique = true),
        Index("cachedAt")
    ]
)
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val nameEn: String? = null,
    val slug: String,
    val icon: String? = null,
    val cachedAt: Long = System.currentTimeMillis()
) {
    /**
     * Checks if this cached category is still valid.
     * Cache is considered valid for 48 hours.
     */
    fun isCacheValid(validityHours: Int = 48): Boolean {
        val cacheValidityMs = validityHours * 60 * 60 * 1000L
        return (System.currentTimeMillis() - cachedAt) < cacheValidityMs
    }
}
