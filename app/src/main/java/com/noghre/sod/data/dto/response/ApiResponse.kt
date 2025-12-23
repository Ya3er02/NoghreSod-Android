package com.noghre.sod.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Generic API response wrapper for all endpoints
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: T? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("errors")
    val errors: Map<String, String>? = null
)

data class PaginatedResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: List<T> = emptyList(),
    @SerializedName("pagination")
    val pagination: PaginationInfo? = null,
    @SerializedName("message")
    val message: String? = null
)

data class PaginationInfo(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("totalItems")
    val totalItems: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean,
    @SerializedName("hasPrevious")
    val hasPrevious: Boolean
)