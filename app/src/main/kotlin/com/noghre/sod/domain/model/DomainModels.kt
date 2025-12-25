package com.noghre.sod.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

// ============================================
// 💫 Domain Models (Pure Kotlin)
// ============================================

/**
 * Product domain model
 * Represents a product in the jewelry store
 */
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val longDescription: String? = null,
    val price: Money,
    val originalPrice: Money? = null,
    val images: List<ProductImage>,
    val category: Category,
    val subcategory: String? = null,
    val material: String,
    val weight: Double? = null, // in grams
    val specifications: Map<String, String> = emptyMap(),
    val availability: StockStatus,
    val rating: Rating,
    val reviews: List<ProductReview> = emptyList(),
    val tags: List<String> = emptyList(),
    val isNewProduct: Boolean = false,
    val isFeatured: Boolean = false,
    val isWishlisted: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    /**
     * Check if product is on discount
     */
    fun hasDiscount(): Boolean = originalPrice != null && originalPrice > price
    
    /**
     * Calculate discount percentage
     */
    fun discountPercentage(): Double? {
        return originalPrice?.let { original ->
            ((original.amount - price.amount) / original.amount * 100).toDouble()
        }
    }
}

/**
 * Money value object
 * Ensures type safety and prevents calculation errors
 */
data class Money(
    val amount: BigDecimal,
    val currency: Currency = Currency.IRR
) : Comparable<Money> {
    init {
        require(amount >= BigDecimal.ZERO) { "Amount must be non-negative" }
    }
    
    override fun compareTo(other: Money): Int {
        require(currency == other.currency) { "Cannot compare different currencies" }
        return amount.compareTo(other.amount)
    }
    
    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Cannot add different currencies" }
        return Money(amount + other.amount, currency)
    }
    
    operator fun minus(other: Money): Money {
        require(currency == other.currency) { "Cannot subtract different currencies" }
        return Money(amount - other.amount, currency)
    }
    
    override fun toString(): String = "$amount $currency"
}

enum class Currency {
    IRR,  // Iranian Rial
    USD,  // US Dollar
    EUR   // Euro
}

/**
 * Product image with thumbnail support
 */
data class ProductImage(
    val url: String,
    val thumbnail: String? = null,
    val altText: String = "",
    val isMain: Boolean = false,
    val order: Int = 0
)

/**
 * Category domain model
 */
data class Category(
    val id: String,
    val name: String,
    val description: String? = null,
    val icon: String? = null,
    val imageUrl: String? = null,
    val parentCategoryId: String? = null,
    val subcategories: List<Category> = emptyList(),
    val productCount: Int = 0
)

/**
 * Stock status - sealed class for type safety
 */
sealed class StockStatus {
    data class Available(val quantity: Int) : StockStatus() {
        init {
            require(quantity > 0) { "Quantity must be positive" }
        }
    }
    object OutOfStock : StockStatus()
    data class PreOrder(val availableDate: LocalDate) : StockStatus()
    object Discontinued : StockStatus()
}

/**
 * Product rating with detailed breakdown
 */
data class Rating(
    val average: Float = 0f,
    val count: Int = 0,
    val distribution: RatingDistribution = RatingDistribution(),
    val userRating: Float? = null
) {
    init {
        require(average in 0f..5f) { "Rating must be between 0 and 5" }
        require(count >= 0) { "Count must be non-negative" }
    }
}

data class RatingDistribution(
    val fiveStar: Int = 0,
    val fourStar: Int = 0,
    val threeStar: Int = 0,
    val twoStar: Int = 0,
    val oneStar: Int = 0
)

/**
 * Product review
 */
data class ProductReview(
    val id: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val rating: Float,
    val title: String,
    val content: String,
    val images: List<String> = emptyList(),
    val helpful: Int = 0,
    val unhelpful: Int = 0,
    val createdAt: LocalDateTime,
    val verified: Boolean = false
)

/**
 * Cart item domain model
 */
data class CartItem(
    val id: String = "",
    val productId: String,
    val productName: String,
    val product: Product? = null,
    val quantity: Int,
    val price: Money,
    val totalPrice: Money = price,
    val addedAt: LocalDateTime = LocalDateTime.now()
) {
    fun getTotal(): Money = Money(
        price.amount.multiply(quantity.toBigDecimal()),
        price.currency
    )
}

/**
 * Order domain model
 */
data class Order(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val subtotal: Money,
    val discount: Money? = null,
    val tax: Money,
    val shippingCost: Money,
    val total: Money,
    val status: OrderStatus = OrderStatus.PENDING,
    val shippingAddress: Address,
    val billingAddress: Address? = null,
    val paymentMethod: PaymentMethod,
    val trackingNumber: String? = null,
    val estimatedDelivery: LocalDate? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val notes: String? = null
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURNED,
    REFUNDED
}

enum class PaymentMethod {
    CARD,
    WALLET,
    BANK_TRANSFER,
    CASH_ON_DELIVERY
}

/**
 * Address domain model
 */
data class Address(
    val id: String,
    val userId: String,
    val name: String,
    val phoneNumber: String,
    val streetAddress: String,
    val city: String,
    val province: String,
    val postalCode: String,
    val country: String = "Iran",
    val isDefault: Boolean = false,
    val type: AddressType = AddressType.SHIPPING
)

enum class AddressType {
    SHIPPING,
    BILLING,
    BOTH
}

/**
 * User domain model
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String? = null,
    val avatarUrl: String? = null,
    val totalOrders: Int = 0,
    val totalSpent: Money? = null,
    val memberSince: LocalDateTime,
    val isVerified: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val preferences: UserPreferences = UserPreferences()
)

data class UserPreferences(
    val newsletter: Boolean = true,
    val notifications: Boolean = true,
    val language: String = "fa",
    val theme: String = "light"
)

/**
 * Pagination metadata
 */
data class PaginationMetadata(
    val currentPage: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
) {
    val hasNextPage: Boolean
        get() = currentPage < totalPages
    
    val hasPreviousPage: Boolean
        get() = currentPage > 1
}

/**
 * Generic result wrapper
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Exception) : Result<T>()
    object Loading : Result<Nothing>()
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    fun exceptionOrNull(): Exception? = when (this) {
        is Error -> exception
        else -> null
    }
    
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
}
