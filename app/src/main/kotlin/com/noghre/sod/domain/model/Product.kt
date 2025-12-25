package com.noghre.sod.domain.model

import java.io.Serializable

/**
 * Domain model for Product.
 * Represents a silver jewelry product in the catalog.
 *
 * @property id Unique product identifier
 * @property name Product name
 * @property description Detailed product description
 * @property price Price in currency
 * @property originalPrice Original price before discount
 * @property category Product category
 * @property imageUrl Primary product image URL
 * @property images Additional product images
 * @property rating User rating (0-5)
 * @property reviewCount Number of reviews
 * @property quantity Available quantity
 * @property weight Weight in grams
 * @property material Material composition
 * @property colors Available colors
 * @property sizes Available sizes
 * @property inStock Stock availability
 * @property isFavorite Marked as favorite by user
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val category: String,
    val imageUrl: String,
    val images: List<String> = emptyList(),
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val quantity: Int = 0,
    val weight: Double = 0.0,
    val material: String = "",
    val colors: List<String> = emptyList(),
    val sizes: List<String> = emptyList(),
    val inStock: Boolean = true,
    val isFavorite: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
) : Serializable {

    /**
     * Calculate discount percentage.
     */
    fun getDiscountPercentage(): Int {
        return if (originalPrice != null && originalPrice > 0) {
            ((originalPrice - price) / originalPrice * 100).toInt()
        } else {
            0
        }
    }

    /**
     * Check if product has discount.
     */
    fun hasDiscount(): Boolean = originalPrice != null && originalPrice > price
}
