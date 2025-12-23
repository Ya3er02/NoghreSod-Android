package com.noghre.sod.data.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateAddressRequest(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("province")
    val province: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("street")
    val street: String? = null,
    @SerializedName("postalCode")
    val postalCode: String? = null,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("isDefault")
    val isDefault: Boolean? = null
)