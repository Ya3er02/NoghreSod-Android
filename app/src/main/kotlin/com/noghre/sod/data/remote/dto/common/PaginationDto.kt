package com.noghre.sod.data.remote.dto.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Pagination Information
 * 
 * Supports both page-based and cursor-based pagination
 * for maximum flexibility in data fetching
 */
@Serializable
sealed class PaginationInfo {
    
    /**
     * Page-based pagination
     * Traditional pagination with page numbers
     * Best for: Mobile apps with "Load More" buttons
     */
    @Serializable
    @SerialName("page_based")
    data class PageBased(
        @SerialName("page")
        val page: Int,
        
        @SerialName("pageSize")
        val pageSize: Int,
        
        @SerialName("totalPages")
        val totalPages: Int,
        
        @SerialName("totalItems")
        val totalItems: Long,
        
        @SerialName("hasNextPage")
        val hasNextPage: Boolean,
        
        @SerialName("hasPreviousPage")
        val hasPreviousPage: Boolean
    ) : PaginationInfo()
    
    /**
     * Cursor-based pagination
     * More efficient for real-time data
     * Best for: Feeds, live data, large datasets
     */
    @Serializable
    @SerialName("cursor_based")
    data class CursorBased(
        @SerialName("nextCursor")
        val nextCursor: String?,
        
        @SerialName("previousCursor")
        val previousCursor: String?,
        
        @SerialName("hasMore")
        val hasMore: Boolean,
        
        @SerialName("pageSize")
        val pageSize: Int
    ) : PaginationInfo()
}

/**
 * Paginated Response
 * Generic wrapper for paginated API responses
 */
@Serializable
data class PaginatedResponseDto<T>(
    @SerialName("items")
    val items: List<T>,
    
    @SerialName("pagination")
    val pagination: PaginationInfo
)
