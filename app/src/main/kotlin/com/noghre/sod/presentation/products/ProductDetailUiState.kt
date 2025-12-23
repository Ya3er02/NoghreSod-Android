package com.noghre.sod.presentation.products

import com.noghre.sod.domain.model.Product

/**
 * UI state for product detail screen
 */
data class ProductDetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val selectedImageIndex: Int = 0,
    val quantity: Int = 1,
    val relatedProducts: List<Product> = emptyList(),
    val isAddingToCart: Boolean = false,
    val isTogglingFavorite: Boolean = false
)
