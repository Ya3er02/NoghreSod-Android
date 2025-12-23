package com.noghre.sod.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Category DTOs for product categorization
 */

@Serializable
data class CategoryDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("name_fa")
    val nameFA: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("description_fa")
    val descriptionFA: String? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("display_order")
    val displayOrder: Int = 0,
    @SerialName("product_count")
    val productCount: Int = 0
)

@Serializable
data class CategoryListResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: List<CategoryDto> = emptyList(),
    @SerialName("message")
    val message: String? = null
)

/**
 * Coupon/Discount DTOs for promotional codes and discounts
 */

@Serializable
data class CouponDto(
    @SerialName("id")
    val id: String,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("discount_type")
    val discountType: String, // PERCENTAGE or FIXED
    @SerialName("discount_value")
    val discountValue: Long,
    @SerialName("max_discount_amount")
    val maxDiscountAmount: Long? = null,
    @SerialName("min_order_amount")
    val minOrderAmount: Long = 0,
    @SerialName("max_uses")
    val maxUses: Int? = null,
    @SerialName("used_count")
    val usedCount: Int = 0,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("start_date")
    val startDate: Long,
    @SerialName("expiry_date")
    val expiryDate: Long,
    @SerialName("is_exclusive")
    val isExclusive: Boolean = false
)

@Serializable
data class ValidateCouponRequestDto(
    @SerialName("code")
    val code: String,
    @SerialName("cart_total")
    val cartTotal: Long
)

@Serializable
data class ValidateCouponResponseDto(
    @SerialName("valid")
    val valid: Boolean,
    @SerialName("coupon")
    val coupon: CouponDto? = null,
    @SerialName("discount_amount")
    val discountAmount: Long? = null,
    @SerialName("message")
    val message: String? = null
)

/**
 * Promotional Banner DTO
 */

@Serializable
data class BannerDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("image")
    val image: String,
    @SerialName("target_type")
    val targetType: String? = null, // PRODUCT, CATEGORY, URL
    @SerialName("target_id")
    val targetId: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("display_order")
    val displayOrder: Int = 0,
    @SerialName("start_date")
    val startDate: Long? = null,
    @SerialName("end_date")
    val endDate: Long? = null
)

@Serializable
data class BannerListResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: List<BannerDto> = emptyList()
)

/**
 * Offer/Deal DTO
 */

@Serializable
data class OfferDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("title_fa")
    val titleFA: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("discount_percentage")
    val discountPercentage: Int,
    @SerialName("products")
    val products: List<String> = emptyList(), // Product IDs
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("start_date")
    val startDate: Long,
    @SerialName("end_date")
    val endDate: Long,
    @SerialName("display_order")
    val displayOrder: Int = 0
)

@Serializable
data class OfferListResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: List<OfferDto> = emptyList()
)
