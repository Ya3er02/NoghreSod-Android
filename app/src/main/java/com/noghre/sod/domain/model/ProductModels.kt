package com.noghre.sod.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

// ============== PRODUCT SUMMARY ==============

/**
 * Product summary for list views.
 * Contains minimal information for performance optimization.
 * Used in product listings, search results, and recommendations.
 */
data class ProductSummary(
    val id: String,
    val name: String,
    val price: Money,
    val mainImage: String,
    val category: ProductCategory,
    val purity: PurityType,
    val rating: Float,
    val reviewCount: Int,
    val inStock: Boolean,
    val isNew: Boolean = false,
    val isFeatured: Boolean = false,
    val discountPercentage: Int? = null
) {
    init {
        require(id.isNotBlank()) { "Product ID cannot be blank" }
        require(name.isNotBlank()) { "Product name cannot be blank" }
        require(rating in 0f..5f) { "Rating must be between 0 and 5" }
        require(reviewCount >= 0) { "Review count cannot be negative" }
        discountPercentage?.let {
            require(it in 0..100) { "Discount must be between 0 and 100" }
        }
    }

    /**
     * Money value object with currency support.
     * Handles multiple currencies with proper formatting.
     */
    data class Money(
        val amount: BigDecimal,
        val currency: Currency = Currency.IRR
    ) {
        init {
            require(amount >= BigDecimal.ZERO) { "Price cannot be negative" }
        }

        /**
         * Get formatted price string for display.
         */
        fun formatted(): String = when (currency) {
            Currency.IRR -> "${amount.toLong().formatWithCommas()} ریال"
            Currency.USD -> "\$${amount}"
            Currency.EUR -> "€${amount}"
            Currency.GBP -> "£${amount}"
        }
    }
}

// ============== PRODUCT DETAIL ==============

/**
 * Detailed product information.
 * Used in product detail screen with complete information.
 */
data class ProductDetail(
    val id: String,
    val name: String,
    val description: String,
    val price: ProductSummary.Money,
    val originalPrice: ProductSummary.Money?,
    val category: ProductCategory,
    val purity: PurityType,
    val weight: Weight,
    val dimensions: Dimensions?,
    val material: String,
    val images: List<ProductImage>,
    val rating: ProductRating,
    val stockInfo: StockInfo,
    val variants: List<ProductVariant> = emptyList(),
    val specifications: Map<String, String> = emptyMap(),
    val isFavorite: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    init {
        require(id.isNotBlank()) { "Product ID cannot be blank" }
        require(name.isNotBlank()) { "Product name cannot be blank" }
        require(images.isNotEmpty()) { "Product must have at least one image" }
    }

    val discountPercentage: Int
        get() = if (originalPrice != null) {
            val discount = originalPrice.amount - price.amount
            val percentage = (discount / originalPrice.amount * BigDecimal(100))
            percentage.toInt()
        } else {
            0
        }
}

// ============== PRODUCT CATEGORY ==============

/**
 * Product category enumeration for jewelry.
 * Each category represents a different type of silver jewelry.
 */
enum class ProductCategory(val displayName: String, val displayNameFa: String) {
    NECKLACE("Necklace", "گردنبند"),
    BRACELET("Bracelet", "دستبند"),
    RING("Ring", "انگشتر"),
    EARRING("Earring", "گوشواره"),
    ANKLET("Anklet", "پابند"),
    PENDANT("Pendant", "آویز"),
    BROOCH("Brooch", "سینه سنجاق"),
    CHAIN("Chain", "زنجیر"),
    BANGLE("Bangle", "النگو"),
    SET("Set", "سرویس"),
    OTHER("Other", "سایر");

    companion object {
        /**
         * Convert string to ProductCategory.
         * Tries to match by name, displayName, or displayNameFa.
         */
        fun fromString(value: String): ProductCategory {
            return entries.find {
                it.name.equals(value, ignoreCase = true) ||
                it.displayName.equals(value, ignoreCase = true) ||
                it.displayNameFa == value
            } ?: OTHER
        }
    }
}

// ============== PURITY TYPE ==============

/**
 * Silver purity types.
 * Different purity levels for silver jewelry.
 */
enum class PurityType(val percentage: Double, val displayName: String) {
    STERLING_925(92.5, "۹۲۵ استرلینگ"),
    SILVER_950(95.0, "۹۵۰ نقره"),
    SILVER_999(99.9, "۹۹۹ خالص نقره"),
    SILVER_800(80.0, "۸۰۰ نقره");

    companion object {
        /**
         * Convert percentage to PurityType.
         */
        fun fromPercentage(percentage: Double): PurityType? {
            return entries.find { it.percentage == percentage }
        }
    }
}

// ============== WEIGHT ==============

/**
 * Product weight with unit.
 * Supports multiple weight units.
 */
data class Weight(
    val value: Double,
    val unit: WeightUnit = WeightUnit.GRAM
) {
    init {
        require(value > 0) { "Weight must be positive" }
    }

    /**
     * Get formatted weight string for display.
     */
    fun formatted(): String = when (unit) {
        WeightUnit.GRAM -> "$value گرم"
        WeightUnit.KILOGRAM -> "$value کیلوگرم"
        WeightUnit.OUNCE -> "$value اونس"
    }
}

enum class WeightUnit {
    GRAM,
    KILOGRAM,
    OUNCE
}

// ============== DIMENSIONS ==============

/**
 * Product dimensions.
 * Supports multiple dimension units.
 */
data class Dimensions(
    val length: Double,
    val width: Double,
    val height: Double,
    val unit: DimensionUnit = DimensionUnit.CENTIMETER
) {
    init {
        require(length > 0 && width > 0 && height > 0) {
            "All dimensions must be positive"
        }
    }
}

enum class DimensionUnit {
    CENTIMETER,
    MILLIMETER,
    INCH
}

// ============== PRODUCT IMAGE ==============

/**
 * Product image with metadata.
 * Each image can have thumbnail and order information.
 */
data class ProductImage(
    val url: String,
    val thumbnailUrl: String,
    val altText: String = "",
    val order: Int = 0,
    val isMain: Boolean = false
) {
    init {
        require(url.isNotBlank()) { "Image URL cannot be blank" }
    }
}

// ============== PRODUCT RATING ==============

/**
 * Product rating information.
 * Contains average rating, count, and distribution.
 */
data class ProductRating(
    val average: Float,
    val count: Int,
    val distribution: Map<Int, Int> = emptyMap()
) {
    init {
        require(average in 0f..5f) { "Average rating must be between 0 and 5" }
        require(count >= 0) { "Count cannot be negative" }
    }

    /**
     * Get percentage of reviews for specific star rating.
     */
    fun getPercentage(stars: Int): Int {
        if (count == 0) return 0
        val starCount = distribution[stars] ?: 0
        return ((starCount.toFloat() / count) * 100).toInt()
    }
}

// ============== STOCK INFO ==============

/**
 * Stock availability information.
 * Uses sealed class to represent different stock states.
 */
sealed class StockInfo {
    /**
     * Product is available with specified quantity.
     */
    data class Available(val quantity: Int) : StockInfo() {
        init {
            require(quantity > 0) { "Available quantity must be positive" }
        }
    }

    /**
     * Product has low stock.
     */
    data class LowStock(val quantity: Int) : StockInfo() {
        init {
            require(quantity > 0) { "Low stock quantity must be positive" }
        }
    }

    /**
     * Product is out of stock.
     */
    object OutOfStock : StockInfo()

    /**
     * Product is available for pre-order.
     */
    data class PreOrder(
        val estimatedAvailability: LocalDateTime
    ) : StockInfo()

    /**
     * Product has been discontinued.
     */
    object Discontinued : StockInfo()
}

// ============== PRODUCT VARIANT ==============

/**
 * Product variant (size, color, style, etc).
 * Represents different options for a product.
 */
data class ProductVariant(
    val id: String,
    val type: VariantType,
    val value: String,
    val priceDifference: BigDecimal = BigDecimal.ZERO,
    val available: Boolean = true
) {
    init {
        require(id.isNotBlank()) { "Variant ID cannot be blank" }
        require(value.isNotBlank()) { "Variant value cannot be blank" }
    }
}

enum class VariantType {
    SIZE,
    COLOR,
    STYLE,
    CUSTOM
}

// ============== CURRENCY ==============

enum class Currency {
    IRR,  // Iranian Rial
    USD,  // US Dollar
    EUR,  // Euro
    GBP   // British Pound
}

// ============== HELPER FUNCTIONS ==============

/**
 * Extension function to format numbers with commas.
 * Used for currency formatting (IRR).
 */
private fun Long.formatWithCommas(): String {
    return "%,d".format(this)
}
