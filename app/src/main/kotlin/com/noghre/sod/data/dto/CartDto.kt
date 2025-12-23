package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Cart API responses.
 */
data class CartDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("items")
    val items: List<CartItemDto> = emptyList(),

    @SerializedName("total_price")
    val totalPrice: Double = 0.0,

    @SerializedName("discount_amount")
    val discountAmount: Double = 0.0,

    @SerializedName("final_price")
    val finalPrice: Double = 0.0,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)
