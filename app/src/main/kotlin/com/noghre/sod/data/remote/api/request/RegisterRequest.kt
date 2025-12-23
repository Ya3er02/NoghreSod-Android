package com.noghre.sod.data.remote.api.request

import com.google.gson.annotations.SerializedName

/**
 * Request body for user registration.
 */
data class RegisterRequest(
    @SerializedName("phone")
    val phone: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)
