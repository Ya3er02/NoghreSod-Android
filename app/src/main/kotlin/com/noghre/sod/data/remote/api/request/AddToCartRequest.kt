package com.noghre.sod.data.remote.api.request

import com.google.gson.annotations.SerializedName

/**
 * Request body for adding item to cart.
 */
data class AddToCartRequest(
    @SerializedName("product_id")
    val productId: String,

    @SerializedName("quantity")
    val quantity: Int
)
