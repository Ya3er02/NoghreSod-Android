package com.noghre.sod.data.remote.api.request

import com.google.gson.annotations.SerializedName

/**
 * Request body for OTP verification.
 */
data class OtpRequest(
    @SerializedName("phone")
    val phone: String,

    @SerializedName("code")
    val code: String
)
