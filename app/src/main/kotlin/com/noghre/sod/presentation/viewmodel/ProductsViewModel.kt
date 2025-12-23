package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.GetCategoriesUseCase
import com.noghre.sod.domain.usecase.GetProductsUseCase
import com.noghre.sod.domain.usecase.SearchProductsUseCase
import com.noghre.sod.domain.usecase.AddToFavoritesUseCase
import com.noghre.sod.domain.usecase.RemoveFromFavoritesUseCase
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.common.UiConstants
import com.noghre.sod.presentation.products.AvailabilityFilter
import com.noghre.sod.presentation.products.ProductFilterOptions
import com.noghre.sod.presentation.products.ProductSortType
import com.noghre.sod.presentation.products.ProductsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for products listing screen.
 * Manages state for browsing, searching, filtering, and managing favorites.
 *
 * @property getProductsUseCase Use case for fetching products
 * @property getCategoriesUseCase Use case for fetching categories
 * @property searchProductsUseCase Use case for searching products
 * @property addToFavoritesUseCase Use case for adding to favorites
 * @property removeFromFavoritesUseCase Use case for removing from favorites
 */
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadProducts(forceRefresh = true)
        loadCategories()
        setupSearchDebounce()
    }

    /**
     * Load products with optional category filter and refresh
     *
     * @param categoryId Category ID to filter by (optional)
     * @param forceRefresh Force refresh from API
     */
    fun loadProducts(categoryId: String? = null, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                updateState {
                    copy(
                        isLoading = !forceRefresh && products.isEmpty(),
                        isRefreshing = forceRefresh,
                        error = null
                    )
                }

                val result = getProductsUseCase(
                    page = 1,
                    categoryId = categoryId,
                    sortBy = uiState.value.sortBy.name.lowercase()
                )

                updateState {
                    copy(
                        products = result,
                        isLoading = false,
                        isRefreshing = false,
                        hasMorePages = result.size >= UiConstants.DEFAULT_PAGE_SIZE,
                        currentPage = 1
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading products")
                updateState {
                    copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = e.message ?: "Failed to load products"
                    )
                }
                _events.send(UiEvent.ShowSnackbar("Failed to load products"))
            }
        }
    }

    /**
     * Load all available categories
     */
    fun loadCategories() {
        viewModelScope.launch {
            try {
                val result = getCategoriesUseCase()
                updateState { copy(categories = result) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading categories")
            }
        }
    }

    /**
     * Search products with debounce
     *
     * @param query Search query
     */
    fun searchProducts(query: String) {
        _searchQuery.value = query
    }

    /**
     * Select a category to filter products
     *
     * @param category Selected category
     */
    fun onCategorySelected(category: Category) {
        updateState { copy(selectedCategory = category) }
        loadProducts(categoryId = category.id, forceRefresh = true)
    }

    /**
     * Change product sorting
     *
     * @param sortType Sort type
     */
    fun onSortChanged(sortType: ProductSortType) {
        updateState { copy(sortBy = sortType) }
        loadProducts(forceRefresh = false)
    }

    /**
     * Apply filters to products
     *
     * @param filters Filter options
     */
    fun onFilterApplied(filters: ProductFilterOptions) {
        updateState { copy(filterOptions = filters) }
        loadProducts(forceRefresh = false)
    }

    /**
     * Toggle favorite status of a product
     *
     * @param productId Product ID
     */
    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            try {
                val product = uiState.value.products.find { it.id == productId }
                if (product != null) {
                    if (product.isFavorite) {
                        removeFromFavoritesUseCase(productId)
                    } else {
                        addToFavoritesUseCase(productId)
                    }

                    updateState {
                        copy(
                            products = products.map {
                                if (it.id == productId) it.copy(isFavorite = !it.isFavorite) else it
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling favorite")
                _events.send(UiEvent.ShowSnackbar("Error updating favorite"))
            }
        }
    }

    /**
     * Load more products (pagination)
     */
    fun loadMoreProducts() {
        if (!uiState.value.hasMorePages || uiState.value.isLoading) return

        viewModelScope.launch {
            try {
                val nextPage = uiState.value.currentPage + 1
                updateState { copy(isLoading = true) }

                val result = getProductsUseCase(
                    page = nextPage,
                    categoryId = uiState.value.selectedCategory?.id
                )

                updateState {
                    copy(
                        products = products + result,
                        isLoading = false,
                        currentPage = nextPage,
                        hasMorePages = result.size >= UiConstants.DEFAULT_PAGE_SIZE
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading more products")
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Refresh products (pull-to-refresh)
     */
    fun refresh() {
        loadProducts(categoryId = uiState.value.selectedCategory?.id, forceRefresh = true)
    }

    /**
     * Clear error message
     */
    fun clearError() {
        updateState { copy(error = null) }
    }

    /**
     * Setup debounced search
     */
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(UiConstants.DEBOUNCE_DELAY_MS)
                .collect { query ->
                    if (query.isNotEmpty()) {
                        performSearch(query)
                    } else {
                        loadProducts()
                    }
                }
        }
    }

    /**
     * Perform search
     */
    private suspend fun performSearch(query: String) {
        try {
            updateState { copy(isLoading = true, searchQuery = query) }
            val results = searchProductsUseCase(query)
            updateState {
                copy(
                    products = results,
                    isLoading = false,
                    currentPage = 1
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error searching products")
            updateState { copy(isLoading = false, error = e.message) }
        }
    }

    /**
     * Update state immutably
     */
    private fun updateState(block: ProductsUiState.() -> ProductsUiState) {
        _uiState.update(block)
    }
}
