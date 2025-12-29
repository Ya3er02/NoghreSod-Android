package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.ProductFilters
import com.noghre.sod.domain.model.SortOption
import com.noghre.sod.domain.model.SortOrder
import com.noghre.sod.domain.usecase.product.GetProductsUseCase
import com.noghre.sod.domain.usecase.product.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Unified ProductsViewModel
 *
 * Consolidated from three separate implementations:
 * - presentation/viewmodel/ProductsViewModel (base structure)
 * - presentation/products/ProductsViewModel (filter logic)
 * - presentation/viewmodel/ProductsViewModelImproved (pagination & advanced features)
 *
 * Implements MVI (Model-View-Intent) pattern for reactive state management
 * Optimized for high-resolution jewelry product images and complex filtering
 *
 * @author NoghreSod Team
 * @version 1.0.0
 * @since Refactor Phase 2
 */

// ==================== UI State ====================
sealed interface ProductsUiState {
    data object Loading : ProductsUiState
    data class Success(
        val products: List<Product>,
        val hasMore: Boolean = true,
        val currentPage: Int = 1,
        val totalCount: Int = 0
    ) : ProductsUiState
    data class Error(val message: String, val throwable: Throwable? = null) : ProductsUiState
}

// ==================== User Intents ====================
sealed interface ProductsIntent {
    data object LoadProducts : ProductsIntent
    data class SearchProducts(val query: String) : ProductsIntent
    data class ApplyFilters(val filters: ProductFilters) : ProductsIntent
    data object LoadMore : ProductsIntent
    data object Refresh : ProductsIntent
    data class SetSortOption(val sortBy: SortOption) : ProductsIntent
    data class SetSortOrder(val sortOrder: SortOrder) : ProductsIntent
}

// ==================== Effects (One-time events) ====================
sealed class ProductsEffect {
    data class ShowError(val message: String) : ProductsEffect
    data class NavigateToDetail(val productId: String) : ProductsEffect
    data object ScrollToTop : ProductsEffect
}

// ==================== ViewModel ====================
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    // ========== State Flows ==========
    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ProductsEffect>(replay = 0)
    val effects: SharedFlow<ProductsEffect> = _effects.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filters = MutableStateFlow(ProductFilters())
    val filters: StateFlow<ProductFilters> = _filters.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    // ========== Initialization ==========
    init {
        handleIntent(ProductsIntent.LoadProducts)
    }

    // ========== Intent Handler ==========
    fun handleIntent(intent: ProductsIntent) {
        when (intent) {
            is ProductsIntent.LoadProducts -> loadProducts(page = 1)
            is ProductsIntent.SearchProducts -> searchProducts(intent.query)
            is ProductsIntent.ApplyFilters -> applyFilters(intent.filters)
            is ProductsIntent.LoadMore -> loadMore()
            is ProductsIntent.Refresh -> refresh()
            is ProductsIntent.SetSortOption -> setSortOption(intent.sortBy)
            is ProductsIntent.SetSortOrder -> setSortOrder(intent.sortOrder)
        }
    }

    // ========== Core Operations ==========

    /**
     * Load products with current filters and pagination
     * Uses coroutine scope for lifecycle management
     */
    private fun loadProducts(page: Int = 1) {
        viewModelScope.launch {
            // Show loading only on first page
            if (page == 1) _uiState.value = ProductsUiState.Loading

            try {
                getProductsUseCase(
                    filters = _filters.value,
                    page = page,
                    pageSize = PAGE_SIZE
                ).collectLatest { result ->
                    result.fold(
                        onSuccess = { products ->
                            _currentPage.value = page
                            _uiState.value = ProductsUiState.Success(
                                products = products,
                                hasMore = products.size >= PAGE_SIZE,
                                currentPage = page,
                                totalCount = products.size
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = ProductsUiState.Error(
                                message = error.message ?: "Failed to load products",
                                throwable = error
                            )
                            _effects.emit(
                                ProductsEffect.ShowError(
                                    error.message ?: "Unknown error occurred"
                                )
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ProductsUiState.Error(
                    message = e.message ?: "Unexpected error",
                    throwable = e
                )
            }
        }
    }

    /**
     * Search products with debouncing
     * Minimum 2 characters required for search
     */
    private fun searchProducts(query: String) {
        _searchQuery.value = query

        viewModelScope.launch {
            if (query.length >= MIN_SEARCH_LENGTH) {
                _uiState.value = ProductsUiState.Loading

                searchProductsUseCase(query)
                    .debounce(SEARCH_DEBOUNCE_MS)
                    .collectLatest { result ->
                        result.fold(
                            onSuccess = { products ->
                                _currentPage.value = 1
                                _uiState.value = ProductsUiState.Success(
                                    products = products,
                                    hasMore = products.size >= PAGE_SIZE,
                                    currentPage = 1,
                                    totalCount = products.size
                                )
                            },
                            onFailure = { error ->
                                _uiState.value = ProductsUiState.Error(
                                    message = error.message ?: "Search failed",
                                    throwable = error
                                )
                            }
                        )
                    }
            } else if (query.isEmpty()) {
                // Reset to full product list when search cleared
                loadProducts(page = 1)
            }
        }
    }

    /**
     * Apply filters and reload products from page 1
     */
    private fun applyFilters(newFilters: ProductFilters) {
        _filters.value = newFilters
        _currentPage.value = 1
        loadProducts(page = 1)

        viewModelScope.launch {
            _effects.emit(ProductsEffect.ScrollToTop)
        }
    }

    /**
     * Load next page of products
     * Only proceeds if hasMore flag is true
     */
    private fun loadMore() {
        val currentState = _uiState.value
        if (currentState is ProductsUiState.Success && currentState.hasMore && !_isLoadingMore.value) {
            _isLoadingMore.value = true
            val nextPage = currentState.currentPage + 1
            loadProducts(page = nextPage)
            _isLoadingMore.value = false
        }
    }

    /**
     * Refresh product list from first page
     */
    private fun refresh() {
        _currentPage.value = 1
        loadProducts(page = 1)
    }

    /**
     * Update sort option and reload
     */
    private fun setSortOption(sortBy: SortOption) {
        val currentFilters = _filters.value
        _filters.value = currentFilters.copy(sortBy = sortBy)
        loadProducts(page = 1)
    }

    /**
     * Update sort order and reload
     */
    private fun setSortOrder(sortOrder: SortOrder) {
        val currentFilters = _filters.value
        _filters.value = currentFilters.copy(sortOrder = sortOrder)
        loadProducts(page = 1)
    }

    // ========== Navigation ==========
    fun navigateToProductDetail(productId: String) {
        viewModelScope.launch {
            _effects.emit(ProductsEffect.NavigateToDetail(productId))
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val MIN_SEARCH_LENGTH = 2
        private const val SEARCH_DEBOUNCE_MS = 300L
    }
}