package com.noghre.sod.core.ui

/**
 * State management for paginated data.
 *
 * Usage:
 * ```
 * data class ProductListUiState(
 *     val products: List<Product> = emptyList(),
 *     val paging: PagingState = PagingState()
 * )
 *
 * val state = ProductListUiState(
 *     products = listOf(...),
 *     paging = PagingState(currentPage = 1, totalPages = 10, hasMore = true)
 * )
 * ```
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
data class PagingState(
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val pageSize: Int = 20,
    val totalItems: Int = 0,
    val hasMore: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    /**
     * Next page number, or null if at the last page.
     */
    val nextPage: Int?
        get() = if (hasMore) currentPage + 1 else null

    /**
     * Whether pagination can be initiated.
     */
    val canLoadMore: Boolean
        get() = hasMore && !isLoading

    /**
     * Total number of items loaded so far.
     */
    val loadedItems: Int
        get() = currentPage * pageSize

    /**
     * Remaining items to load.
     */
    val remainingItems: Int
        get() = maxOf(0, totalItems - loadedItems)

    /**
     * Progress percentage (0-100).
     */
    val progress: Int
        get() = if (totalItems == 0) 0 else (loadedItems * 100) / totalItems

    /**
     * Create next page state for loading more.
     */
    fun nextPageLoading(): PagingState = copy(
        currentPage = currentPage + 1,
        isLoading = true,
        error = null
    )

    /**
     * Update with newly loaded items.
     */
    fun withNewPage(
        totalPages: Int,
        totalItems: Int,
        pageSize: Int = this.pageSize
    ): PagingState = copy(
        totalPages = totalPages,
        totalItems = totalItems,
        pageSize = pageSize,
        hasMore = currentPage < totalPages,
        isLoading = false,
        error = null
    )

    /**
     * Update with error.
     */
    fun withError(error: String): PagingState = copy(
        isLoading = false,
        error = error,
        currentPage = maxOf(1, currentPage - 1)  // Revert to previous page
    )

    /**
     * Reset to initial state.
     */
    fun reset(): PagingState = copy(
        currentPage = 1,
        totalPages = 1,
        totalItems = 0,
        hasMore = false,
        isLoading = false,
        error = null
    )
}
