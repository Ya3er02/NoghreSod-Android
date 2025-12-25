package com.noghre.sod.domain.model

import java.math.BigDecimal
import java.time.LocalDate

/**
 * Domain model for Product.
 * Independent from data layer DTOs.
 */
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Money,
    val images: List<ImageUrl>,
    val category: Category,
    val specifications: ProductSpecifications,
    val availability: StockStatus,
    val rating: Rating,
    val reviews: List<ProductReview>,
    val tags: List<String>,
    val isNew: Boolean,
    val isOnSale: Boolean,
    val createdAt: String,
    val updatedAt: String
) {
    /**
     * Money value object with currency.
     */
    data class Money(
        val amount: BigDecimal,
        val currency: Currency = Currency.IRR
    ) {
        init {
            require(amount >= BigDecimal.ZERO) { "Price cannot be negative" }
        }

        override fun toString(): String = "$amount $currency"
    }

    /**
     * Image URL with optional alt text.
     */
    data class ImageUrl(
        val url: String,
        val altText: String = "",
        val isMain: Boolean = false
    ) {
        init {
            require(url.isNotBlank()) { "URL cannot be blank" }
        }
    }

    /**
     * Stock availability status.
     */
    sealed class StockStatus {
        data class Available(
            val quantity: Int
        ) : StockStatus() {
            init {
                require(quantity >= 0) { "Quantity cannot be negative" }
            }
        }

        object OutOfStock : StockStatus()

        data class PreOrder(
            val availableDate: LocalDate
        ) : StockStatus()

        object Discontinued : StockStatus()
    }

    /**
     * Product rating and review count.
     */
    data class Rating(
        val average: Double,
        val count: Int,
        val distribution: Map<Int, Int> = emptyMap()
    ) {
        init {
            require(average in 0.0..5.0) { "Rating must be between 0 and 5" }
            require(count >= 0) { "Count cannot be negative" }
        }
    }

    /**
     * Product review.
     */
    data class ProductReview(
        val id: String,
        val userId: String,
        val userName: String,
        val rating: Int,
        val title: String,
        val content: String,
        val helpful: Int = 0,
        val createdAt: String
    ) {
        init {
            require(rating in 1..5) { "Rating must be between 1 and 5" }
        }
    }

    /**
     * Product specifications.
     */
    data class ProductSpecifications(
        val weight: String? = null,
        val dimensions: String? = null,
        val material: String? = null,
        val color: String? = null,
        val brand: String? = null,
        val sku: String? = null,
        val custom: Map<String, String> = emptyMap()
    )
}

/**
 * Supported currencies.
 */
enum class Currency {
    IRR,    // Iranian Rial
    USD,    // US Dollar
    EUR,    // Euro
    GBP;    // British Pound

    override fun toString(): String = when (this) {
        IRR -> "﷼"
        USD -> "$"
        EUR -> "€"
        GBP -> "£"
    }
}

/**
 * Category domain model.
 */
data class Category(
    val id: String,
    val name: String,
    val description: String = "",
    val imageUrl: String? = null,
    val parentId: String? = null,
    val productCount: Int = 0,
    val isActive: Boolean = true
) {
    init {
        require(id.isNotBlank()) { "Category ID cannot be blank" }
        require(name.isNotBlank()) { "Category name cannot be blank" }
    }
}