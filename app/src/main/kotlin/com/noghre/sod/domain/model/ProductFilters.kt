package com.noghre.sod.domain.model

import kotlinx.serialization.Serializable

/**
 * Unified data class for all product filtering operations.
 * Replaces scattered filter logic across multiple ViewModels.
 *
 * Supports:
 * - Category filtering
 * - Price range filtering
 * - Weight range filtering (for silver/gold jewelry)
 * - Gemstone type filtering
 * - Plating type filtering (e.g., white gold, rose gold)
 * - Multiple sort options
 *
 * @author NoghreSod Team
 * @version 1.0.0
 * @since Refactor Phase 2
 */
@Serializable
data class ProductFilters(
    // ========== Category & Search ==========
    val category: String? = null,
    val searchQuery: String? = null,

    // ========== Price Filters (in local currency) ==========
    val minPrice: Double? = null,
    val maxPrice: Double? = null,

    // ========== Weight Filters (in grams) ==========
    val minWeight: Double? = null,
    val maxWeight: Double? = null,

    // ========== Material & Plating Filters ==========
    val gemType: String? = null,
    val platingType: String? = null,
    val hallmark: String? = null, // e.g., "925" for silver

    // ========== Sorting ==========
    val sortBy: SortOption = SortOption.RELEVANCE,
    val sortOrder: SortOrder = SortOrder.DESCENDING,

    // ========== Pagination (for use case layer) ==========
    val page: Int = 1,
    val pageSize: Int = 20
) {
    /**
     * Check if any filter is active (not default values)
     */
    fun hasActiveFilters(): Boolean {
        return !category.isNullOrEmpty()
            || !searchQuery.isNullOrEmpty()
            || minPrice != null
            || maxPrice != null
            || minWeight != null
            || maxWeight != null
            || !gemType.isNullOrEmpty()
            || !platingType.isNullOrEmpty()
            || !hallmark.isNullOrEmpty()
    }

    /**
     * Create a copy with reset pagination (back to page 1)
     */
    fun withResetPagination(): ProductFilters {
        return copy(page = 1)
    }

    /**
     * Create a copy with next page
     */
    fun nextPage(): ProductFilters {
        return copy(page = page + 1)
    }
}

/**
 * Sort options for products
 */
enum class SortOption {
    RELEVANCE,      // Default search relevance
    PRICE,          // By price (min-max)
    POPULARITY,     // By sales/views/ratings
    NEWEST,         // By creation date
    WEIGHT,         // By product weight (useful for precious metals)
    RATING          // By customer ratings
}

/**
 * Sort order direction
 */
enum class SortOrder {
    ASCENDING,      // Low to high (for price, weight)
    DESCENDING      // High to low (for price, newest)
}
