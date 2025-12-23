package com.noghre.sod.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============ PRODUCT DTOs ============

/**
 * Data Transfer Object for products from the API.
 */
@Serializable
data class ProductDto(
    @SerialName("_id")
    val id: String,
    val name: String,
    val nameEn: String? = null,
    val description: String,
    val price: Double,
    val images: List<String> = emptyList(),
    @SerialName("category")
    val categoryId: String? = null,
    val weight: Double? = null,
    val purity: String? = null,
    val stock: Int = 0,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)

// ============ CATEGORY DTOs ============

/**
 * Data Transfer Object for product categories.
 */
@Serializable
data class CategoryDto(
    @SerialName("_id")
    val id: String,
    val name: String,
    val nameEn: String? = null,
    val slug: String,
    val icon: String? = null
)

// ============ PRICING DTOs ============

/**
 * Data Transfer Object for silver price information.
 */
@Serializable
data class SilverPriceDto(
    @SerialName("pricePerGram")
    val pricePerGram: Double,
    val currency: String = "IRR",
    val timestamp: Long,
    val source: String? = null
)

/**
 * Request body for price calculation endpoint.
 */
@Serializable
data class PriceCalculationRequest(
    val weight: Double,
    val purity: String
)

/**
 * Response from price calculation endpoint.
 */
@Serializable
data class PriceCalculationResponse(
    val weight: Double,
    val purity: String,
    val pricePerGram: Double,
    val totalPrice: Double,
    val currency: String = "IRR"
)

// ============ AUTHENTICATION DTOs ============

/**
 * Login request body.
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Register request body.
 */
@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val passwordConfirm: String,
    val phone: String? = null
)

/**
 * Authentication response with tokens.
 */
@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserDto
)

// ============ USER DTOs ============

/**
 * User data transfer object.
 */
@Serializable
data class UserDto(
    @SerialName("_id")
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val role: String = "user",
    val createdAt: Long? = null
)

/**
 * Update profile request body.
 */
@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val phone: String? = null
)

/**
 * Change password request body.
 */
@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val passwordConfirm: String
)
