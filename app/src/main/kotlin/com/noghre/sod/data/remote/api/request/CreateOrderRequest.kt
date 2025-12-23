package com.noghre.sod.data.remote.api.request

import com.google.gson.annotations.SerializedName

/**
 * Request body for creating new order.
 */
data class CreateOrderRequest(
    @SerializedName("address_id")
    val addressId: String,

    @SerializedName("payment_method")
    val paymentMethod: String,

    @SerializedName("cart_items")
    val cartItems: List<CartItemForOrder>
)

/**
 * Cart item details for order creation.
 */
data class CartItemForOrder(
    @SerializedName("product_id")
    val productId: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("price_at_time")
    val priceAtTime: Double
)
