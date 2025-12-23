package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for User Profile data.
 */
data class UserDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)
