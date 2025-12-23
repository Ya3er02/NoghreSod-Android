package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for API error details.
 */
data class ErrorDto(
    @SerializedName("code")
    val code: String,

    @SerializedName("field")
    val field: String? = null,

    @SerializedName("message")
    val message: String
)
