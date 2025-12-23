package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

data class OrderDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("orderNumber")
    val orderNumber: String,
    @SerializedName("items")
    val items: List<OrderItemDto> = emptyList(),
    @SerializedName("totalAmount")
    val totalAmount: Long,
    @SerializedName("discountAmount")
    val discountAmount: Long = 0,
    @SerializedName("shippingCost")
    val shippingCost: Long = 0,
    @SerializedName("taxAmount")
    val taxAmount: Long = 0,
    @SerializedName("status")
    val status: String,
    @SerializedName("paymentMethod")
    val paymentMethod: String? = null,
    @SerializedName("trackingCode")
    val trackingCode: String? = null,
    @SerializedName("deliveryDate")
    val deliveryDate: Long? = null,
    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),
    @SerializedName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis(),
    @SerializedName("notes")
    val notes: String? = null,
    @SerializedName("shippingAddress")
    val shippingAddress: String? = null
)

data class OrderItemDto(
    @SerializedName("productId")
    val productId: String,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Long,
    @SerializedName("totalPrice")
    val totalPrice: Long
)