package com.noghre.sod.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Authentication response with tokens
 */
data class AuthResponse(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("expiresIn")
    val expiresIn: Long,
    @SerializedName("tokenType")
    val tokenType: String = "Bearer"
)