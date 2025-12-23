package com.noghre.sod.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== Authentication Request DTOs ====================

@Serializable
data class RegisterRequestDto(
    @SerialName("email")
    val email: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("password")
    val password: String,
    @SerialName("firstName")
    val firstName: String,
    @SerialName("lastName")
    val lastName: String,
)

@Serializable
data class LoginRequestDto(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
)

@Serializable
data class LoginPhoneRequestDto(
    @SerialName("phone")
    val phone: String,
    @SerialName("code")
    val code: String,
)

@Serializable
data class PhoneRequestDto(
    @SerialName("phone")
    val phone: String,
)

@Serializable
data class VerifyOTPRequestDto(
    @SerialName("phone")
    val phone: String,
    @SerialName("code")
    val code: String,
)

@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refreshToken")
    val refreshToken: String,
)

@Serializable
data class AuthTokenDto(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("expiresIn")
    val expiresIn: Long,
    @SerialName("user")
    val user: UserDto,
)

// ==================== Product Request DTOs ====================

@Serializable
data class AddReviewRequestDto(
    @SerialName("rating")
    val rating: Float,
    @SerialName("comment")
    val comment: String?,
)

// ==================== Cart Request DTOs ====================

@Serializable
data class AddToCartRequestDto(
    @SerialName("productId")
    val productId: String,
    @SerialName("quantity")
    val quantity: Int,
)

@Serializable
data class UpdateCartItemRequestDto(
    @SerialName("quantity")
    val quantity: Int,
)

@Serializable
data class ApplyDiscountRequestDto(
    @SerialName("code")
    val code: String,
)

// ==================== Order Request DTOs ====================

@Serializable
data class CreateOrderRequestDto(
    @SerialName("shippingAddressId")
    val shippingAddressId: String,
    @SerialName("billingAddressId")
    val billingAddressId: String? = null,
    @SerialName("paymentMethod")
    val paymentMethod: String,
    @SerialName("notes")
    val notes: String? = null,
)

@Serializable
data class CancelOrderRequestDto(
    @SerialName("reason")
    val reason: String?,
)

@Serializable
data class RequestReturnDto(
    @SerialName("items")
    val items: List<String>, // Order item IDs
    @SerialName("reason")
    val reason: String,
    @SerialName("description")
    val description: String,
)

@Serializable
data class VerifyPaymentRequestDto(
    @SerialName("transactionId")
    val transactionId: String,
)

@Serializable
data class PaymentVerificationDto(
    @SerialName("isValid")
    val isValid: Boolean,
    @SerialName("transactionId")
    val transactionId: String,
    @SerialName("amount")
    val amount: Long,
    @SerialName("message")
    val message: String?,
)

// ==================== User Request DTOs ====================

@Serializable
data class UpdateProfileRequestDto(
    @SerialName("firstName")
    val firstName: String? = null,
    @SerialName("lastName")
    val lastName: String? = null,
    @SerialName("profileImage")
    val profileImage: String? = null,
    @SerialName("bio")
    val bio: String? = null,
    @SerialName("birthDate")
    val birthDate: String? = null,
)

@Serializable
data class ChangePasswordRequestDto(
    @SerialName("oldPassword")
    val oldPassword: String,
    @SerialName("newPassword")
    val newPassword: String,
)

@Serializable
data class PasswordResetRequestDto(
    @SerialName("email")
    val email: String,
)

@Serializable
data class ResetPasswordRequestDto(
    @SerialName("email")
    val email: String,
    @SerialName("code")
    val code: String,
    @SerialName("newPassword")
    val newPassword: String,
)
