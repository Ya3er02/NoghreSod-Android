package com.noghre.sod.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Generic API response wrapper.
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: T?
)

// ============== AUTHENTICATION ==============

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("refreshToken")
    val refreshToken: String?,
    @SerializedName("user")
    val user: UserDto
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
    val phoneNumber: String
)

// ============== USER ==============

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
    @SerializedName("addresses")
    val addresses: List<AddressDto>?,
    @SerializedName("isVerified")
    val isVerified: Boolean,
    @SerializedName("createdAt")
    val createdAt: String
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
    @SerializedName("isDefault")
    val isDefault: Boolean
)

// ============== PRODUCT ==============

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
    val images: List<String>?,
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
    val colors: List<String>?,
    @SerializedName("sizes")
    val sizes: List<String>?,
    @SerializedName("inStock")
    val inStock: Boolean,
    @SerializedName("createdAt")
    val createdAt: String
)

// ============== CART ==============

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
    val itemCount: Int
)

data class CartItemDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("product")
    val product: ProductDto,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("selectedColor")
    val selectedColor: String?,
    @SerializedName("selectedSize")
    val selectedSize: String?,
    @SerializedName("subtotal")
    val subtotal: Double
)

// ============== ORDER ==============

data class OrderDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("orderNumber")
    val orderNumber: String,
    @SerializedName("items")
    val items: List<OrderItemDto>,
    @SerializedName("shippingAddress")
    val shippingAddress: AddressDto,
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
    @SerializedName("product")
    val product: ProductDto,
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
    val currentLocation: String,
    @SerializedName("events")
    val events: List<TrackingEventDto>?
)

data class TrackingEventDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("description")
    val description: String
)

// ============== PAYMENT ==============

data class PaymentDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("orderId")
    val orderId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("status")
    val status: String,
    @SerializedName("method")
    val method: String,
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("createdAt")
    val createdAt: String
)
