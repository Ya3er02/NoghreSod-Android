package com.noghre.sod.data.remote.dto

import com.google.gson.annotations.SerializedName

// ============== GENERIC RESPONSE WRAPPERS ==============

/**
 * Standard API response wrapper.
 * Used for single object responses.
 *
 * @param success Whether the request was successful
 * @param message Response message
 * @param data The response body
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: T
)

/**
 * Paginated API response wrapper.
 * Used for list endpoints with pagination metadata.
 *
 * @param success Whether the request was successful
 * @param message Response message
 * @param data List of items
 * @param pagination Pagination metadata
 */
data class PaginatedResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: List<T>,
    @SerializedName("pagination")
    val pagination: PaginationMeta
)

/**
 * Pagination metadata for list responses.
 *
 * @param currentPage Current page number (1-based)
 * @param totalPages Total number of pages
 * @param pageSize Number of items per page
 * @param totalItems Total number of items
 * @param hasNext Whether there is a next page
 * @param hasPrevious Whether there is a previous page
 */
data class PaginationMeta(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("totalItems")
    val totalItems: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean,
    @SerializedName("hasPrevious")
    val hasPrevious: Boolean
)

// ============== AUTHENTICATION DTOs ==============

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("rememberMe")
    val rememberMe: Boolean = false
)

data class LoginResponse(
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("expiresIn")
    val expiresIn: Long
)

data class RegisterRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null
)

// ============== PRODUCT DTOs ==============

data class ProductDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("originalPrice")
    val originalPrice: Double?,
    @SerializedName("category")
    val category: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("reviewCount")
    val reviewCount: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("weight")
    val weight: Double,
    @SerializedName("material")
    val material: String,
    @SerializedName("colors")
    val colors: List<String>,
    @SerializedName("sizes")
    val sizes: List<String>,
    @SerializedName("inStock")
    val inStock: Boolean,
    @SerializedName("createdAt")
    val createdAt: String
)

data class CategoryDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("productCount")
    val productCount: Int
)

// ============== CART DTOs ==============

data class CartDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("items")
    val items: List<CartItemDto>,
    @SerializedName("totalPrice")
    val totalPrice: Double,
    @SerializedName("itemCount")
    val itemCount: Int,
    @SerializedName("lastUpdated")
    val lastUpdated: String
)

data class CartItemDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("productId")
    val productId: String,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("selectedColor")
    val selectedColor: String?,
    @SerializedName("selectedSize")
    val selectedSize: String?,
    @SerializedName("subtotal")
    val subtotal: Double
)

data class AddToCartRequest(
    @SerializedName("productId")
    val productId: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("selectedColor")
    val selectedColor: String? = null,
    @SerializedName("selectedSize")
    val selectedSize: String? = null
)

data class UpdateCartItemRequest(
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("selectedColor")
    val selectedColor: String? = null,
    @SerializedName("selectedSize")
    val selectedSize: String? = null
)

// ============== ORDER DTOs ==============

data class OrderDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("orderNumber")
    val orderNumber: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("items")
    val items: List<OrderItemDto>,
    @SerializedName("subtotal")
    val subtotal: Double,
    @SerializedName("shippingCost")
    val shippingCost: Double,
    @SerializedName("discountAmount")
    val discountAmount: Double,
    @SerializedName("totalAmount")
    val totalAmount: Double,
    @SerializedName("status")
    val status: String,
    @SerializedName("tracking")
    val tracking: OrderTrackingDto?,
    @SerializedName("createdAt")
    val createdAt: String
)

data class OrderItemDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("productId")
    val productId: String,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unitPrice")
    val unitPrice: Double,
    @SerializedName("selectedColor")
    val selectedColor: String?,
    @SerializedName("selectedSize")
    val selectedSize: String?
)

data class OrderTrackingDto(
    @SerializedName("trackingNumber")
    val trackingNumber: String,
    @SerializedName("carrier")
    val carrier: String,
    @SerializedName("estimatedDelivery")
    val estimatedDelivery: String,
    @SerializedName("currentLocation")
    val currentLocation: String
)

data class CreateOrderRequest(
    @SerializedName("cartId")
    val cartId: String,
    @SerializedName("shippingAddressId")
    val shippingAddressId: String,
    @SerializedName("paymentMethod")
    val paymentMethod: String,
    @SerializedName("discountCode")
    val discountCode: String? = null,
    @SerializedName("note")
    val note: String? = null
)

// ============== PAYMENT DTOs ==============

data class PaymentDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("orderId")
    val orderId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("method")
    val method: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("createdAt")
    val createdAt: String
)

data class ProcessPaymentRequest(
    @SerializedName("orderId")
    val orderId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("method")
    val method: String,
    @SerializedName("returnUrl")
    val returnUrl: String
)

// ============== USER DTOs ==============

data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("lastName")
    val lastName: String?,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?,
    @SerializedName("isVerified")
    val isVerified: Boolean,
    @SerializedName("createdAt")
    val createdAt: String
)

data class UpdateProfileRequest(
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("email")
    val email: String? = null
)

data class AddressDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("fullAddress")
    val fullAddress: String,
    @SerializedName("province")
    val province: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("postalCode")
    val postalCode: String,
    @SerializedName("recipientName")
    val recipientName: String?,
    @SerializedName("recipientPhone")
    val recipientPhone: String?,
    @SerializedName("isDefault")
    val isDefault: Boolean
)

data class AddAddressRequest(
    @SerializedName("title")
    val title: String,
    @SerializedName("fullAddress")
    val fullAddress: String,
    @SerializedName("province")
    val province: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("postalCode")
    val postalCode: String,
    @SerializedName("recipientName")
    val recipientName: String,
    @SerializedName("recipientPhone")
    val recipientPhone: String,
    @SerializedName("isDefault")
    val isDefault: Boolean = false
)

// ============== REVIEW & RATING DTOs ==============

data class ReviewDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userAvatar")
    val userAvatar: String?,
    @SerializedName("productId")
    val productId: String,
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("title")
    val title: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("images")
    val images: List<String>?,
    @SerializedName("helpful")
    val helpfulCount: Int,
    @SerializedName("isVerifiedPurchase")
    val isVerifiedPurchase: Boolean,
    @SerializedName("createdAt")
    val createdAt: String
)

data class SubmitReviewRequest(
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("title")
    val title: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("images")
    val images: List<String>? = null
)

// ============== NOTIFICATION DTOs ==============

data class NotificationDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("type")
    val type: String, // "order", "promotion", "system"
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("actionUrl")
    val actionUrl: String?,
    @SerializedName("isRead")
    val isRead: Boolean,
    @SerializedName("createdAt")
    val createdAt: String
)

data class RegisterFcmTokenRequest(
    @SerializedName("token")
    val token: String,
    @SerializedName("deviceId")
    val deviceId: String,
    @SerializedName("platform")
    val platform: String = "android"
)
