package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Order API responses.
 */
data class OrderDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("items")
    val items: List<OrderItemDto>,

    @SerializedName("status")
    val status: String, // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

    @SerializedName("total_amount")
    val totalAmount: Double,

    @SerializedName("shipping_address")
    val shippingAddress: AddressDto,

    @SerializedName("payment_method")
    val paymentMethod: String,

    @SerializedName("payment_status")
    val paymentStatus: String,

    @SerializedName("tracking_code")
    val trackingCode: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null,

    @SerializedName("delivered_at")
    val deliveredAt: String? = null
)
