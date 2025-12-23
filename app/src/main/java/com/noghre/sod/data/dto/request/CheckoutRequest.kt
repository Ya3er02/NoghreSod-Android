package com.noghre.sod.data.dto.request

import com.google.gson.annotations.SerializedName

data class CheckoutRequest(
    @SerializedName("addressId")
    val addressId: String,
    @SerializedName("paymentMethod")
    val paymentMethod: String,
    @SerializedName("notes")
    val notes: String? = null,
    @SerializedName("couponCode")
    val couponCode: String? = null
)