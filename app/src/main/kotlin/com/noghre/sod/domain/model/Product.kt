package com.noghre.sod.domain.model

import java.time.LocalDateTime

/**
 * Product domain model representing jewelry items in the catalog
 * Specialized for silver jewelry and handicraft products
 */
data class Product(
    val id: String,
    val name: String,
    val nameInFarsi: String,
    val description: String,
    val descriptionInFarsi: String? = null,
    val price: Long, // Price in Toman
    val weight: Double, // Weight in grams
    val purity: PurityType, // Silver purity (925, 950, etc.)
    val code: String, // Product code for inventory
    val category: ProductCategory,
    val subcategory: String? = null,
    val images: List<String>, // List of image URLs
    val thumbnailImage: String?, // Thumbnail image URL
    val stock: Int, // Available quantity
    val laborCost: Long? = null, // Labor cost in Toman (for handmade items)
    val isHandmade: Boolean = false, // Whether product is handmade
    val isBulk: Boolean = false, // Whether available in bulk
    val isRawMaterial: Boolean = false, // Whether it's raw material for resale
    val tags: List<String> = emptyList(), // Search tags
    val rating: Float = 0f, // Average rating (0-5)
    val reviewCount: Int = 0, // Number of reviews
    val sku: String? = null, // Stock Keeping Unit
    val barcode: String? = null, // Product barcode
    val metadata: Map<String, String> = emptyMap(), // Additional metadata
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val isActive: Boolean = true,
    val isFeatured: Boolean = false, // Featured product for homepage
)

/**
 * Silver purity types
 */
enum class PurityType {
    PURITY_925, // 92.5% pure silver
    PURITY_950, // 95.0% pure silver
    PURITY_999, // 99.9% pure silver (pure)
    MIXED, // Mixed metals
}

/**
 * Product categories specific to jewelry store
 */
enum class ProductCategory {
    RINGS,           // انگشتری
    NECKLACES,       // گردنبند
    BRACELETS,       // دستبند
    EARRINGS,        // گوشواره
    ANKLETS,         // پابند
    BROOCHES,        // سنجاق سینه
    PENDANTS,        // آویز
    CHAINS,          // زنجیر
    HANDICRAFTS,     // صنایع دستی
    RAW_MATERIALS,   // مواد خام
    CUSTOM_ORDERS,   // سفارش‌های خاص
}

/**
 * Compact representation of Product for list displays
 */
data class ProductSummary(
    val id: String,
    val name: String,
    val price: Long,
    val weight: Double,
    val purity: PurityType,
    val thumbnailImage: String?,
    val rating: Float,
    val isAvailable: Boolean,
)

/**
 * Extended product details including seller information
 */
data class ProductDetail(
    val product: Product,
    val seller: SellerInfo?,
    val relatedProducts: List<ProductSummary> = emptyList(),
    val availability: AvailabilityStatus,
    val shippingInfo: ShippingInfo,
)

/**
 * Seller information
 */
data class SellerInfo(
    val sellerId: String,
    val sellerName: String,
    val sellerRating: Float,
    val sellerReviewCount: Int,
    val isVerified: Boolean,
    val responseTime: Int, // Response time in hours
    val shippingDays: Int, // Typical shipping days
)

/**
 * Product availability status
 */
enum class AvailabilityStatus {
    IN_STOCK,      // موجود
    LOW_STOCK,     // موجود محدود
    OUT_OF_STOCK,  // نا موجود
    PRE_ORDER,     // پیش‌سفارش
}

/**
 * Shipping information
 */
data class ShippingInfo(
    val domesticShipping: Boolean,
    val internationalShipping: Boolean,
    val estimatedDays: Int,
    val shippingCost: Long? = null,
    val freeShippingThreshold: Long? = null,
)
