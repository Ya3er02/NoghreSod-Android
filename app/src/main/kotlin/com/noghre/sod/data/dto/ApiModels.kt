package com.noghre.sod.data.dto

import kotlinx.serialization.Serializable

// ===== API Response Wrapper =====
/**
 * Generic API response wrapper used by all endpoints.
 * @param success Whether the request was successful
 * @param data The response data
 * @param message Optional message from server
 * @param errors Optional error details
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val message: String? = null,
    val errors: ErrorDetails? = null
)

@Serializable
data class ErrorDetails(
    val code: String? = null,
    val details: Map<String, String>? = null
)

// ===== Pagination =====
/**
 * Generic paginated response wrapper.
 * @param items List of items in current page
 * @param page Current page number (1-indexed)
 * @param limit Items per page
 * @param total Total number of items
 * @param totalPages Total number of pages
 * @param hasMore Whether there are more pages
 */
@Serializable
data class PaginatedResponse<T>(
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0,
    val totalPages: Int = 0,
    val hasMore: Boolean = false
)

// ===== Authentication =====
@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long = 86400, // 24 hours in seconds
    val tokenType: String = "Bearer",
    val user: UserDto? = null
)

// ===== Products =====
/**
 * Data transfer object for Product entity.
 * Represents a silver product/jewelry item.
 */
@Serializable
data class ProductDto(
    val id: String,
    val name: String,
    val nameEn: String? = null,
    val description: String = "",
    val descriptionEn: String? = null,
    val price: Double,
    val originalPrice: Double? = null,
    val images: List<String> = emptyList(),
    val categoryId: String = "",
    val categoryName: String? = null,
    val weight: Double = 0.0, // in grams
    val purity: String = "950", // 950, 900, 800, 750
    val stock: Int = 0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val isNew: Boolean = false,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isInWishlist: Boolean? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

// ===== Categories =====
@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val nameEn: String? = null,
    val slug: String = "",
    val icon: String? = null,
    val description: String? = null,
    val productsCount: Int = 0,
    val imageUrl: String? = null
)

// ===== Silver Price =====
/**
 * Current silver price information.
 */
@Serializable
data class SilverPriceDto(
    val pricePerGram: Double,
    val pricePerOunce: Double? = null,
    val pricePer18g: Double? = null,
    val currency: String = "IRR",
    val timestamp: Long = 0,
    val source: String = "",
    val changePercent24h: Double? = null,
    val changeAbsolute24h: Double? = null,
    val high24h: Double? = null,
    val low24h: Double? = null,
    val open24h: Double? = null,
    val close24h: Double? = null
)

/**
 * Price calculation response.
 */
@Serializable
data class PriceCalculationResponse(
    val weight: Double,
    val purity: String,
    val pricePerGram: Double,
    val totalPrice: Double,
    val currency: String = "IRR",
    val craftingCost: Double? = null,
    val totalWithCrafting: Double? = null
)

/**
 * Price trends data for charts.
 */
@Serializable
data class PriceTrendsDto(
    val timeframe: String = "30d", // 7d, 30d, 90d, 1y
    val dataPoints: List<PriceDataPoint> = emptyList(),
    val minPrice: Double = 0.0,
    val maxPrice: Double = 0.0,
    val avgPrice: Double = 0.0,
    val currentPrice: Double = 0.0
)

@Serializable
data class PriceDataPoint(
    val timestamp: Long = 0,
    val price: Double = 0.0,
    val date: String = ""
)

// ===== User =====
@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val role: String = "user",
    val avatar: String? = null,
    val bio: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false
)

// ===== Address (Future) =====
@Serializable
data class AddressDto(
    val id: String = "",
    val title: String = "",
    val fullName: String = "",
    val phone: String = "",
    val province: String = "",
    val city: String = "",
    val address: String = "",
    val postalCode: String = "",
    val isDefault: Boolean = false,
    val createdAt: Long = 0
)

// ===== Review (Future) =====
@Serializable
data class ReviewDto(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val rating: Int = 0,
    val title: String = "",
    val content: String = "",
    val images: List<String> = emptyList(),
    val helpful: Int = 0,
    val unhelpful: Int = 0,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

// ===== Cart (Not yet implemented in backend) =====
@Serializable
data class CartItemDto(
    val id: String = "",
    val productId: String = "",
    val quantity: Int = 1,
    val product: ProductDto? = null,
    val addedAt: Long = 0
)

@Serializable
data class CartDto(
    val items: List<CartItemDto> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val updatedAt: Long = 0
)

// ===== Order (Not yet implemented in backend) =====
@Serializable
data class OrderDto(
    val id: String = "",
    val orderNumber: String = "",
    val userId: String = "",
    val items: List<OrderItemDto> = emptyList(),
    val status: String = "pending", // pending, confirmed, shipped, delivered, cancelled
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val shippingAddress: AddressDto? = null,
    val billingAddress: AddressDto? = null,
    val paymentMethod: String = "",
    val trackingNumber: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val estimatedDelivery: Long? = null
)

@Serializable
data class OrderItemDto(
    val id: String = "",
    val productId: String = "",
    val quantity: Int = 1,
    val price: Double = 0.0,
    val subtotal: Double = 0.0,
    val product: ProductDto? = null
)

// ===== Notification (Future) =====
@Serializable
data class NotificationDto(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "", // info, warning, error, success
    val actionUrl: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = 0
)
