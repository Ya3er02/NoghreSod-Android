package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Authentication Response.
 */
data class AuthResponseDto(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String,

    @SerializedName("token_type")
    val tokenType: String = "Bearer",

    @SerializedName("expires_in")
    val expiresIn: Long,

    @SerializedName("user")
    val user: UserDto
)
