package com.noghre.sod.data.remote.api.request

import com.google.gson.annotations.SerializedName

/**
 * Request body for user login.
 */
data class LoginRequest(
    @SerializedName("phone")
    val phone: String,

    @SerializedName("password")
    val password: String
)
