package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

data class AddressDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("province")
    val province: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("district")
    val district: String? = null,
    @SerializedName("street")
    val street: String,
    @SerializedName("postalCode")
    val postalCode: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("isDefault")
    val isDefault: Boolean = false,
    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getFullAddress(): String = buildString {
        append(street)
        append(", ")
        append(city)
        append(", ")
        append(province)
        append(" ")
        append(postalCode)
    }
}