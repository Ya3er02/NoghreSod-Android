package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Shipping/Billing Address.
 */
data class AddressDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("province")
    val province: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("postal_code")
    val postalCode: String,

    @SerializedName("address_line")
    val addressLine: String,

    @SerializedName("is_default")
    val isDefault: Boolean = false,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)
