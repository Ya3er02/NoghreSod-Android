package com.noghre.sod.data.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("profileImage")
    val profileImage: String? = null,
    @SerializedName("nationalId")
    val nationalId: String? = null,
    @SerializedName("birthDate")
    val birthDate: Long? = null
)