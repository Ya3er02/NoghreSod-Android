package com.noghre.sod.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * 🌟 Compose Stable Models
 * 
 * These annotations help Compose optimize recomposition by:
 * - @Immutable: Class never changes, skip all recomposition
 * - @Stable: Class changes are observable, track changes
 * 
 * Applying these annotations significantly improves Compose performance.
 */

// ============================================
// IMMUTABLE VALUE OBJECTS
// ============================================

/**
 * 📋 Product Model
 * 
 * 🔧 Fix 3.1: Added safe discount percentage calculation method
 */
@Immutable
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val discountPrice: Double? = null,
    val images: List<String>,
    val rating: Double,
    val reviewCount: Int,
    val isFavorite: Boolean = false,
    val categoryId: String,
    val stock: Int,
    val sku: String,
    val createdAt: String,
    val updatedAt: String,
) {
    /**
     * 🔧 Fix 3.1: Calculate discount percentage safely
     * 
     * Validates:
     * - discountPrice is not null
     * - price > 0 (prevents division by zero)
     * - discountPrice is in valid range [0..price]
     * - result is positive (1-99%)
     * 
     * Returns null if any validation fails (no badge shown)
     */
    fun getDiscountPercentage(): Int? {
        return if (discountPrice != null && 
                   price > 0 && 
                   discountPrice > 0 && 
                   discountPrice < price) {
            ((price - discountPrice) / price * 100).toInt().coerceIn(1, 99)
        } else {
            null
        }
    }
}

/**
 * 🛒 Cart Item Model
 */
@Immutable
data class CartItem(
    val id: String,
    val productId: String,
    val product: Product? = null,
    val quantity: Int,
    val price: Double,
    val discountPrice: Double? = null,
    val addedAt: String,
)

/**
 * 📋 Order Model
 */
@Immutable
data class Order(
    val id: String,
    val orderNumber: String,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val status: OrderStatus,
    val paymentStatus: PaymentStatus,
    val createdAt: String,
    val estimatedDeliveryDate: String? = null,
)

/**
 * Order Item Model
 */
@Immutable
data class OrderItem(
    val id: String,
    val productId: String,
    val quantity: Int,
    val price: Double,
)

/**
 * 👤 User Model
 */
@Immutable
data class User(
    val id: String,
    val email: String,
    val phone: String,
    val firstName: String,
    val lastName: String,
    val profileImage: String? = null,
    val membershipTier: String = "BASIC",
)

/**
 * 🎯 Address Model
 */
@Immutable
data class Address(
    val id: String? = null,
    val title: String,
    val recipientName: String,
    val phone: String,
    val province: String,
    val city: String,
    val street: String,
    val postalCode: String,
    val isDefault: Boolean = false,
)

/**
 * 📕 Category Model
 */
@Immutable
data class Category(
    val id: String,
    val name: String,
    val nameEn: String,
    val description: String? = null,
    val iconUrl: String? = null,
    val parentId: String? = null,
    val isActive: Boolean = true,
    val displayOrder: Int = 0,
    val productCount: Int = 0,
)

/**
 * 🜟 Review Model
 */
@Immutable
data class Review(
    val id: String,
    val productId: String,
    val userId: String,
    val rating: Int,
    val title: String,
    val comment: String,
    val images: List<String> = emptyList(),
    val verified: Boolean = false,
    val createdAt: String,
    val updatedAt: String,
)

// ============================================
// ENUMS (AUTOMATICALLY STABLE)
// ============================================

/**
 * Order Status Enum
 */
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURNED,
}

/**
 * Payment Status Enum
 */
enum class PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED,
    CANCELLED,
}

/**
 * Payment Method Enum
 */
enum class PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    BANK_TRANSFER,
    DIGITAL_WALLET,
    CASH_ON_DELIVERY,
}

/**
 * Membership Tier Enum
 */
enum class MembershipTier {
    BASIC,
    SILVER,
    GOLD,
    PLATINUM,
}

// ============================================
// SUMMARY/LIGHTWEIGHT MODELS
// ============================================

/**
 * 💫 Product Summary (for lists)
 */
@Immutable
data class ProductSummary(
    val id: String,
    val name: String,
    val price: Double,
    val discountPrice: Double? = null,
    val image: String? = null,
    val rating: Double = 0.0,
    val isFavorite: Boolean = false,
)

/**
 * 💫 Order Summary (for lists)
 */
@Immutable
data class OrderSummary(
    val id: String,
    val orderNumber: String,
    val totalPrice: Double,
    val status: OrderStatus,
    val createdAt: String,
)

/**
 * 📋 Cart Summary
 */
@Immutable
data class CartSummary(
    val itemCount: Int,
    val totalPrice: Double,
    val discountAmount: Double = 0.0,
    val finalPrice: Double = totalPrice - discountAmount,
)

// ============================================
// RETURN REQUEST MODEL
// ============================================

/**
 * 🔄 Return Request Model
 */
@Immutable
data class ReturnRequest(
    val id: String,
    val orderId: String,
    val items: List<String>,
    val reason: String,
    val status: String = "PENDING",
    val createdAt: String,
    val updatedAt: String,
)

// ============================================
// PAYMENT METHOD MODEL
// ============================================

/**
 * Payment Info
 */
@Immutable
data class PaymentInfo(
    val method: PaymentMethod,
    val cardLastFour: String? = null,
    val cardBrand: String? = null,
    val billingAddress: Address? = null,
)

// ============================================
// AUTH TOKEN MODEL
// ============================================

/**
 * Auth Token
 */
@Immutable
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: User,
)

// ============================================
// USER PREFERENCES
// ============================================

/**
 * User Preferences
 */
@Stable
data class UserPreferences(
    val language: String = "fa",
    val currency: String = "IRR",
    val notificationsEnabled: Boolean = true,
    val emailUpdates: Boolean = true,
    val theme: String = "light",
    val autoLogin: Boolean = false,
)

// ============================================
// SECURITY SETTINGS
// ============================================

/**
 * Device Info
 */
@Immutable
data class DeviceInfo(
    val id: String,
    val name: String,
    val type: String,
    val lastLogin: java.time.LocalDateTime,
    val isCurrentDevice: Boolean = false,
)

/**
 * Security Settings
 */
@Stable
data class SecuritySettings(
    val twoFactorEnabled: Boolean = false,
    val activeDevices: List<DeviceInfo> = emptyList(),
)

// ============================================
// FILTER OPTIONS
// ============================================

/**
 * Price Range Filter
 */
@Immutable
data class PriceRange(
    val min: Double,
    val max: Double,
)

/**
 * Product Filter
 */
@Immutable
data class ProductFilter(
    val categoryId: String? = null,
    val priceRange: PriceRange? = null,
    val minRating: Double = 0.0,
    val searchQuery: String? = null,
    val sortBy: String = "newest",
)
