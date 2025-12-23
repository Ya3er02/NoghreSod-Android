package com.noghre.sod.domain.model

/**
 * Domain model for Product.
 * This is the "clean" model used throughout the application layer.
 * It's independent from API and database representations.
 *
 * @param id Unique product identifier
 * @param name Product name in Persian
 * @param nameEn Product name in English
 * @param description Product description
 * @param price Price in Rials
 * @param images List of image URLs
 * @param category Category the product belongs to
 * @param weight Weight in grams
 * @param purity Purity percentage/standard
 * @param stock Available stock quantity
 * @param createdAt When the product was created
 * @param updatedAt When the product was last updated
 */
data class Product(
    val id: String,
    val name: String,
    val nameEn: String?,
    val description: String,
    val price: Double,
    val images: List<String>,
    val category: Category?,
    val weight: Double?,
    val purity: String?,
    val stock: Int,
    val createdAt: Long?,
    val updatedAt: Long?
) {
    /**
     * Returns the display name.
     * Uses Persian name if available, falls back to English.
     */
    fun getDisplayName(): String = if (name.isNotEmpty()) name else (nameEn ?: "Unknown")

    /**
     * Gets the primary image URL.
     * Returns the first image or a default placeholder.
     */
    fun getPrimaryImage(): String? = images.firstOrNull()

    /**
     * Checks if the product has images.
     */
    fun hasImages(): Boolean = images.isNotEmpty()

    /**
     * Checks if the product is in stock.
     */
    fun isInStock(): Boolean = stock > 0

    /**
     * Checks if the product is low in stock (less than 5).
     */
    fun isLowStock(): Boolean = stock in 1..4

    /**
     * Checks if the product is out of stock.
     */
    fun isOutOfStock(): Boolean = stock <= 0

    /**
     * Formats the price with currency symbol.
     * Returns price in format: "15,000 ریال"
     */
    fun formatPrice(): String {
        val formatter = java.text.NumberFormat.getInstance(java.util.Locale("fa", "IR"))
        return "${formatter.format(price.toLong())} ریال"
    }
}
