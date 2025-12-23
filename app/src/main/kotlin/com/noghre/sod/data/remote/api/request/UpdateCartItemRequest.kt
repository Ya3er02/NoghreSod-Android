package com.noghre.sod.data.remote.api.request

import com.google.gson.annotations.SerializedName

/**
 * Request body for updating cart item quantity.
 */
data class UpdateCartItemRequest(
    @SerializedName("quantity")
    val quantity: Int
)
