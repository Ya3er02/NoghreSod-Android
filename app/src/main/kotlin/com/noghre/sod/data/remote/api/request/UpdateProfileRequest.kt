package com.noghre.sod.data.remote.api.request

import com.google.gson.annotations.SerializedName

/**
 * Request body for updating user profile.
 */
data class UpdateProfileRequest(
    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("email")
    val email: String? = null
)
