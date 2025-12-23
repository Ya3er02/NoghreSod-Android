package com.noghre.sod.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Pagination metadata.
 */
data class PaginationDto(
    @SerializedName("page")
    val page: Int,

    @SerializedName("per_page")
    val perPage: Int,

    @SerializedName("total")
    val total: Int,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("has_next")
    val hasNext: Boolean,

    @SerializedName("has_prev")
    val hasPrev: Boolean
)
