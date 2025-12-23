package com.noghre.sod.data.dto

import kotlinx.serialization.Serializable

/**
 * Generic wrapper for API responses.
 * Used for single object responses.
 */
@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val status: String = "success",
    val error: String? = null
)

/**
 * Generic wrapper for paginated API responses.
 * Used for list endpoints with pagination.
 */
@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)
