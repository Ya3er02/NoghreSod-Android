package com.noghre.sod.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Domain model representing a jewelry product in the NoghreSod store.
 * 
 * Uses BigDecimal for price calculations to ensure accurate financial precision.
 * This prevents floating-point rounding errors common with Double.
 * 
 * @property id Unique identifier for the product
 * @property name Product name (e.g., "Silver Bracelet")
 * @property description Detailed product description
 * @property price Product price in currency units (uses BigDecimal for accuracy)
 * @property originalPrice Original price before discount (for display purposes)
 * @property discountPercentage Discount percentage applied (0-100)
 * @property category Product category (e.g., "Bracelets", "Rings", "Necklaces")
 * @property images List of product image URLs
 * @property rating Average customer rating (1-5)
 * @property totalReviews Total number of reviews
 * @property inStock Availability status
 * @property weight Product weight in grams
 * @property material Material composition (e.g., "925 Silver", "14K Gold")
 * @property dimensions Product dimensions
 * @property createdAt Timestamp when product was added
 * @property lastUpdated Timestamp of last modification
 * 
 * @since 1.0.0
 * @see BigDecimal
 */
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val originalPrice: BigDecimal? = null,
    val discountPercentage: BigDecimal = BigDecimal.ZERO,
    val category: String,
    val images: List<String>,
    val rating: Double = 0.0,
    val totalReviews: Int = 0,
    val inStock: Boolean,
    val weight: BigDecimal? = null,
    val material: String? = null,
    val dimensions: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
    val tags: List<String> = emptyList(),
    val sku: String? = null
) {
    
    /**
     * Calculate the discounted price based on discount percentage.
     * 
     * @return Discounted price as BigDecimal
     * 
     * Example:
     * ```
     * val product = Product(price = 100.00, discountPercentage = 10)
     * val discountedPrice = product.getFinalPrice() // Returns 90.00
     * ```
     */
    fun getFinalPrice(): BigDecimal {
        val discountMultiplier = BigDecimal(100) - discountPercentage
        return (price * discountMultiplier) / BigDecimal(100)
    }
    
    /**
     * Calculate the discount amount in currency.
     * 
     * @return Discount amount as BigDecimal
     */
    fun getDiscountAmount(): BigDecimal {
        return price - getFinalPrice()
    }
    
    /**
     * Check if product has a valid discount applied.
     * 
     * @return true if discountPercentage > 0
     */
    fun hasDiscount(): Boolean {
        return discountPercentage > BigDecimal.ZERO
    }
    
    /**
     * Get product rating as percentage (out of 100).
     * 
     * @return Rating as percentage (0-100)
     */
    fun getRatingPercentage(): Double {
        return (rating / 5.0) * 100
    }
    
    /**
     * Validate product data integrity.
     * 
     * @return true if product data is valid
     */
    fun isValid(): Boolean {
        return id.isNotBlank() &&
                name.isNotBlank() &&
                description.isNotBlank() &&
                price > BigDecimal.ZERO &&
                images.isNotEmpty() &&
                rating in 0.0..5.0 &&
                totalReviews >= 0 &&
                discountPercentage in BigDecimal.ZERO..BigDecimal(100)
    }
}

/**
 * Data class representing a product in cart.
 * Extends Product with cart-specific information.
 * 
 * @property product The underlying product
 * @property quantity Quantity of this product in cart
 * @property addedAt Timestamp when added to cart
 */
data class CartProduct(
    val product: Product,
    val quantity: Int,
    val addedAt: LocalDateTime = LocalDateTime.now()
) {
    
    /**
     * Calculate total price for this cart item (quantity × final price).
     * 
     * @return Total price as BigDecimal
     */
    fun getTotalPrice(): BigDecimal {
        return product.getFinalPrice() * BigDecimal(quantity)
    }
    
    /**
     * Validate cart item.
     * 
     * @return true if cart item is valid
     */
    fun isValid(): Boolean {
        return product.isValid() && quantity > 0
    }
}

/**
 * Lightweight product DTO for list displays.
 * Used to reduce data transfer and improve performance.
 * 
 * @property id Product identifier
 * @property name Product name
 * @property price Final price (with discount applied)
 * @property mainImage Primary product image URL
 * @property rating Average rating
 * @property inStock Stock status
 */
data class ProductPreview(
    val id: String,
    val name: String,
    val price: BigDecimal,
    val mainImage: String,
    val rating: Double,
    val inStock: Boolean
)

/**
 * Extension function to convert Product to ProductPreview.
 */
fun Product.toPreview(): ProductPreview {
    return ProductPreview(
        id = id,
        name = name,
        price = getFinalPrice(),
        mainImage = images.firstOrNull() ?: "",
        rating = rating,
        inStock = inStock
    )
}
