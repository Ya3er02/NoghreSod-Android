package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Generic API Response wrapper for all API endpoints.
 */
data class ApiResponseDto<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("errors")
    val errors: List<ErrorDto>? = null,

    @SerializedName("pagination")
    val pagination: PaginationDto? = null
)
