package com.noghre.sod.data.remote.dto

import com.google.gson.annotations.SerializedName

// ==================== CART REQUESTS ====================

/**
 * Request model for adding item to cart.
 * Type-safe alternative to Map<String, Any>
 */
data class AddToCartRequest(
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("selected_color")
    val selectedColor: String? = null,
    @SerializedName("selected_size")
    val selectedSize: String? = null
) {
    init {
        require(productId.isNotBlank()) { "Product ID cannot be blank" }
        require(quantity > 0) { "Quantity must be greater than 0" }
    }
}

data class UpdateCartItemRequest(
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("selected_color")
    val selectedColor: String? = null,
    @SerializedName("selected_size")
    val selectedSize: String? = null
) {
    init {
        require(quantity > 0) { "Quantity must be greater than 0" }
    }
}

// ==================== ORDER REQUESTS ====================

data class CreateOrderRequest(
    @SerializedName("shipping_address_id")
    val shippingAddressId: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("discount_code")
    val discountCode: String? = null,
    @SerializedName("notes")
    val notes: String? = null
) {
    init {
        require(shippingAddressId.isNotBlank()) { "Shipping address ID cannot be blank" }
        require(paymentMethod.isNotBlank()) { "Payment method cannot be blank" }
    }
}

// ==================== PAYMENT REQUESTS ====================

data class ProcessPaymentRequest(
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("method")
    val method: String,
    @SerializedName("return_url")
    val returnUrl: String
) {
    init {
        require(orderId.isNotBlank()) { "Order ID cannot be blank" }
        require(amount > 0) { "Amount must be greater than 0" }
        require(method.isNotBlank()) { "Payment method cannot be blank" }
        require(returnUrl.isNotBlank()) { "Return URL cannot be blank" }
    }
}

// ==================== USER REQUESTS ====================

data class UpdateProfileRequest(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
    @SerializedName("email")
    val email: String? = null
) {
    init {
        require(firstName.isNotBlank()) { "First name cannot be blank" }
        require(lastName.isNotBlank()) { "Last name cannot be blank" }
    }
}

data class AddAddressRequest(
    @SerializedName("title")
    val title: String,
    @SerializedName("full_address")
    val fullAddress: String,
    @SerializedName("province")
    val province: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("postal_code")
    val postalCode: String,
    @SerializedName("recipient_name")
    val recipientName: String,
    @SerializedName("recipient_phone")
    val recipientPhone: String,
    @SerializedName("is_default")
    val isDefault: Boolean = false
) {
    init {
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(fullAddress.isNotBlank()) { "Address cannot be blank" }
        require(province.isNotBlank()) { "Province cannot be blank" }
        require(city.isNotBlank()) { "City cannot be blank" }
        require(postalCode.isNotBlank()) { "Postal code cannot be blank" }
    }
}

// ==================== COUPON REQUESTS ====================

data class ApplyCouponRequest(
    @SerializedName("coupon_code")
    val couponCode: String
) {
    init {
        require(couponCode.isNotBlank()) { "Coupon code cannot be blank" }
    }
}

// ==================== REVIEW REQUESTS ====================

data class SubmitReviewRequest(
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("title")
    val title: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("images")
    val images: List<String>? = null
) {
    init {
        require(rating in 1f..5f) { "Rating must be between 1 and 5" }
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(comment.isNotBlank()) { "Comment cannot be blank" }
    }
}

// ==================== PUSH NOTIFICATION REQUESTS ====================

data class RegisterFcmTokenRequest(
    @SerializedName("token")
    val token: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("platform")
    val platform: String = "android"
) {
    init {
        require(token.isNotBlank()) { "FCM token cannot be blank" }
        require(deviceId.isNotBlank()) { "Device ID cannot be blank" }
    }
}
