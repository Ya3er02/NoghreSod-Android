package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("nameEn")
    val nameEn: String? = null,
    @SerializedName("profileImage")
    val profileImage: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("nationalId")
    val nationalId: String? = null,
    @SerializedName("birthDate")
    val birthDate: Long? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),
    @SerializedName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis(),
    @SerializedName("isVerified")
    val isVerified: Boolean = false,
    @SerializedName("isActive")
    val isActive: Boolean = true
)