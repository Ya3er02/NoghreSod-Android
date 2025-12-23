package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for individual Cart Items.
 */
data class CartItemDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("cart_id")
    val cartId: String,

    @SerializedName("product_id")
    val productId: String,

    @SerializedName("product")
    val product: ProductDto? = null,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("price_at_time")
    val priceAtTime: Double,

    @SerializedName("subtotal")
    val subtotal: Double
)
