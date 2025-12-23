package com.noghre.sod.presentation.products

import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.model.Product

/**
 * UI state for products listing screen
 */
data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedCategory: Category? = null,
    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",
    val sortBy: ProductSortType = ProductSortType.NEWEST,
    val filterOptions: ProductFilterOptions = ProductFilterOptions(),
    val hasMorePages: Boolean = true,
    val currentPage: Int = 1
)

/**
 * Product sorting options
 */
enum class ProductSortType {
    NEWEST,
    PRICE_ASC,
    PRICE_DESC,
    RATING,
    POPULARITY,
    DISCOUNT
}

/**
 * Product filter options
 */
data class ProductFilterOptions(
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minRating: Float = 0f,
    val categories: List<String> = emptyList(),
    val availability: AvailabilityFilter = AvailabilityFilter.ALL
)

enum class AvailabilityFilter {
    ALL,
    IN_STOCK,
    ON_SALE
}
