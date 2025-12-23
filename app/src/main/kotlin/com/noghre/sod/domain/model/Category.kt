package com.noghre.sod.domain.model

/**
 * Domain model for Category.
 * Represents product categories in the business logic layer.
 *
 * @param id Unique category identifier
 * @param name Category name in Persian
 * @param nameEn Category name in English
 * @param slug URL-friendly slug
 * @param icon URL to category icon
 */
data class Category(
    val id: String,
    val name: String,
    val nameEn: String?,
    val slug: String,
    val icon: String?
) {
    /**
     * Returns the display name.
     * Uses Persian name if available, falls back to English.
     */
    fun getDisplayName(): String = if (name.isNotEmpty()) name else (nameEn ?: "Unknown")

    /**
     * Checks if the category has an icon.
     */
    fun hasIcon(): Boolean = !icon.isNullOrEmpty()
}
