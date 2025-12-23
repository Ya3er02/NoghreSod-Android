package com.noghre.sod.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Cart-related DTOs for API communication
 */

@Serializable
data class AddToCartRequestDto(
    @SerialName("product_id")
    val productId: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("weight")
    val weight: Double,
    @SerialName("labor_cost")
    val laborCost: Long = 0
)

@Serializable
data class UpdateCartItemDto(
    @SerialName("quantity")
    val quantity: Int
)

@Serializable
data class CartItemResponseDto(
    @SerialName("id")
    val id: String,
    @SerialName("product_id")
    val productId: String,
    @SerialName("product_name")
    val productName: String,
    @SerialName("product_image")
    val productImage: String,
    @SerialName("purity")
    val purity: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("weight")
    val weight: Double,
    @SerialName("unit_price")
    val unitPrice: Long,
    @SerialName("labor_cost")
    val laborCost: Long = 0,
    @SerialName("total_price")
    val totalPrice: Long
)

@Serializable
data class CartSummaryDto(
    @SerialName("items")
    val items: List<CartItemResponseDto>,
    @SerialName("total_items")
    val totalItems: Int,
    @SerialName("total_weight")
    val totalWeight: Double,
    @SerialName("subtotal")
    val subtotal: Long,
    @SerialName("tax")
    val tax: Long,
    @SerialName("shipping_cost")
    val shippingCost: Long = 0,
    @SerialName("discount_amount")
    val discountAmount: Long = 0,
    @SerialName("total")
    val total: Long
)

@Serializable
data class ApplyCouponRequestDto(
    @SerialName("coupon_code")
    val couponCode: String
)

@Serializable
data class ApplyCouponResponseDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("discount_amount")
    val discountAmount: Long,
    @SerialName("discount_percentage")
    val discountPercentage: Int,
    @SerialName("new_total")
    val newTotal: Long,
    @SerialName("message")
    val message: String? = null
)
