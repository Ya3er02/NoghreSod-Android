package com.noghre.sod.data.dto.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("phone")
    val phone: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String? = null
)