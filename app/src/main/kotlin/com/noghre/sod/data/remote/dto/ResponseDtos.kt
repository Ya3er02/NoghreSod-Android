package com.noghre.sod.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generic API Response wrapper
 */
@Serializable
data class ResponseDto<T>(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: T? = null,
    @SerialName("error")
    val error: ErrorDto? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("code")
    val code: String? = null,
    @SerialName("timestamp")
    val timestamp: Long? = null,
)

/**
 * Error response DTO
 */
@Serializable
data class ErrorDto(
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("details")
    val details: String? = null,
    @SerialName("path")
    val path: String? = null,
)

/**
 * Paginated response wrapper
 */
@Serializable
data class PaginatedResponseDto<T>(
    @SerialName("items")
    val items: List<T>,
    @SerialName("page")
    val page: Int,
    @SerialName("pageSize")
    val pageSize: Int,
    @SerialName("totalItems")
    val totalItems: Long,
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("hasNextPage")
    val hasNextPage: Boolean,
    @SerialName("hasPreviousPage")
    val hasPreviousPage: Boolean,
)

/**
 * Product DTO for API communication
 */
@Serializable
data class ProductDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("nameInFarsi")
    val nameInFarsi: String,
    @SerialName("price")
    val price: Long,
    @SerialName("weight")
    val weight: Double,
    @SerialName("purity")
    val purity: String,
    @SerialName("category")
    val category: String,
    @SerialName("images")
    val images: List<String>,
    @SerialName("thumbnailImage")
    val thumbnailImage: String?,
    @SerialName("stock")
    val stock: Int,
    @SerialName("rating")
    val rating: Float = 0f,
    @SerialName("isHandmade")
    val isHandmade: Boolean = false,
)

/**
 * Product detail DTO
 */
@Serializable
data class ProductDetailDto(
    @SerialName("product")
    val product: ProductDto,
    @SerialName("description")
    val description: String,
    @SerialName("seller")
    val seller: SellerInfoDto?,
    @SerialName("relatedProducts")
    val relatedProducts: List<ProductDto>,
)

/**
 * Seller information DTO
 */
@Serializable
data class SellerInfoDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("rating")
    val rating: Float,
    @SerialName("isVerified")
    val isVerified: Boolean,
)

/**
 * Product review DTO
 */
@Serializable
data class ProductReviewDto(
    @SerialName("id")
    val id: String,
    @SerialName("userId")
    val userId: String,
    @SerialName("userName")
    val userName: String,
    @SerialName("rating")
    val rating: Float,
    @SerialName("comment")
    val comment: String?,
    @SerialName("createdAt")
    val createdAt: String,
)

/**
 * Cart DTO
 */
@Serializable
data class CartDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("items")
    val items: List<CartItemDto>,
    @SerialName("subtotal")
    val subtotal: Long,
    @SerialName("tax")
    val tax: Long,
    @SerialName("shipping")
    val shipping: Long,
    @SerialName("discount")
    val discount: Long,
    @SerialName("total")
    val total: Long,
)

/**
 * Cart item DTO
 */
@Serializable
data class CartItemDto(
    @SerialName("id")
    val id: String,
    @SerialName("productId")
    val productId: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("price")
    val price: Long,
    @SerialName("totalPrice")
    val totalPrice: Long,
)

/**
 * Cart validation DTO
 */
@Serializable
data class CartValidationDto(
    @SerialName("isValid")
    val isValid: Boolean,
    @SerialName("errors")
    val errors: List<String>,
    @SerialName("warnings")
    val warnings: List<String>,
)

/**
 * Order DTO
 */
@Serializable
data class OrderDto(
    @SerialName("id")
    val id: String,
    @SerialName("orderNumber")
    val orderNumber: String,
    @SerialName("items")
    val items: List<OrderItemDto>,
    @SerialName("status")
    val status: String,
    @SerialName("paymentStatus")
    val paymentStatus: String,
    @SerialName("total")
    val total: Long,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("estimatedDelivery")
    val estimatedDelivery: String?,
)

/**
 * Order item DTO
 */
@Serializable
data class OrderItemDto(
    @SerialName("id")
    val id: String,
    @SerialName("productId")
    val productId: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("price")
    val price: Long,
    @SerialName("totalPrice")
    val totalPrice: Long,
)

/**
 * Order tracking DTO
 */
@Serializable
data class OrderTrackingDto(
    @SerialName("orderId")
    val orderId: String,
    @SerialName("status")
    val status: String,
    @SerialName("events")
    val events: List<TrackingEventDto>,
    @SerialName("carrier")
    val carrier: String?,
    @SerialName("trackingNumber")
    val trackingNumber: String?,
)

/**
 * Tracking event DTO
 */
@Serializable
data class TrackingEventDto(
    @SerialName("status")
    val status: String,
    @SerialName("description")
    val description: String,
    @SerialName("timestamp")
    val timestamp: String,
    @SerialName("location")
    val location: String?,
)

/**
 * Return request DTO
 */
@Serializable
data class ReturnRequestDto(
    @SerialName("id")
    val id: String,
    @SerialName("orderId")
    val orderId: String,
    @SerialName("reason")
    val reason: String,
    @SerialName("status")
    val status: String,
    @SerialName("createdAt")
    val createdAt: String,
)

/**
 * User DTO
 */
@Serializable
data class UserDto(
    @SerialName("id")
    val id: String,
    @SerialName("email")
    val email: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("firstName")
    val firstName: String,
    @SerialName("lastName")
    val lastName: String,
    @SerialName("profileImage")
    val profileImage: String?,
    @SerialName("membershipTier")
    val membershipTier: String,
    @SerialName("totalOrders")
    val totalOrders: Int,
)

/**
 * Address DTO
 */
@Serializable
data class AddressDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("title")
    val title: String,
    @SerialName("recipientName")
    val recipientName: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("province")
    val province: String,
    @SerialName("city")
    val city: String,
    @SerialName("street")
    val street: String,
    @SerialName("postalCode")
    val postalCode: String,
    @SerialName("isDefault")
    val isDefault: Boolean = false,
)

/**
 * User preferences DTO
 */
@Serializable
data class UserPreferencesDto(
    @SerialName("language")
    val language: String,
    @SerialName("currency")
    val currency: String,
    @SerialName("emailNotifications")
    val emailNotifications: Boolean,
    @SerialName("pushNotifications")
    val pushNotifications: Boolean,
)

/**
 * Security settings DTO
 */
@Serializable
data class SecuritySettingsDto(
    @SerialName("twoFactorEnabled")
    val twoFactorEnabled: Boolean,
    @SerialName("lastPasswordChange")
    val lastPasswordChange: String?,
    @SerialName("activeDevices")
    val activeDevices: List<DeviceInfoDto>,
)

/**
 * Device info DTO
 */
@Serializable
data class DeviceInfoDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String,
    @SerialName("lastActive")
    val lastActive: String,
)
