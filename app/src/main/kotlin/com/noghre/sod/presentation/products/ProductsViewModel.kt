package com.noghre.sod.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.noghre.sod.core.result.Result
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import com.noghre.sod.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for products list screen.
 * Manages loading, searching, filtering, and displaying products.
 *
 * Features:
 * - Infinite scrolling with Paging 3
 * - Search with debounce
 * - Category filtering
 * - Manual refresh
 * - Offline-first caching
 * - Error handling with cached fallback
 */
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    // ============ STATE PROPERTIES ============

    /**
     * Current UI state for products list.
     * Updated whenever products are loaded, searched, or filtered.
     */
    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    /**
     * Paginated products flow for infinite scrolling.
     * Automatically handles loading more items.
     */
    private val _pagingData = MutableStateFlow<PagingData<Product>>(PagingData.empty())
    val pagingData: StateFlow<PagingData<Product>> = _pagingData.asStateFlow()

    /**
     * Current search query.
     */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Current selected category filter (null = all categories).
     */
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    /**
     * Whether a refresh operation is in progress.
     */
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // ============ INIT ============

    init {
        // Combine search query and category, debounce, and search
        setupSearchAndFilter()
        // Load initial products
        loadProducts()
    }

    // ============ SEARCH AND FILTER SETUP ============

    /**
     * Sets up search with debounce and category filtering.
     * Combines search query and category changes into a single flow.
     */
    @OptIn(FlowPreview::class)
    private fun setupSearchAndFilter() {
        // Combine search query and category into search/filter flow
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Wait 300ms after user stops typing
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isEmpty()) {
                        // No search, apply category filter
                        productRepository.getProducts(
                            category = _selectedCategory.value
                        )
                    } else {
                        // Search products
                        productRepository.searchProducts(query)
                    }
                }
                .collect { result ->
                    _uiState.value = when (result) {
                        is Result.Loading -> UiState.Loading(result.data)
                        is Result.Success -> UiState.Success(result.data)
                        is Result.Error -> UiState.Error(result.error, result.data)
                    }
                }
        }
    }

    // ============ PUBLIC ACTIONS ============

    /**
     * Loads initial products list.
     * Used when screen first appears.
     */
    fun loadProducts() {
        viewModelScope.launch {
            productRepository.getProducts(
                category = _selectedCategory.value
            ).collect { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> UiState.Loading(result.data)
                    is Result.Success -> UiState.Success(result.data)
                    is Result.Error -> UiState.Error(result.error, result.data)
                }
            }
        }
    }

    /**
     * Loads paginated products for infinite scrolling.
     * Used when infinite scroll list is displayed.
     */
    fun loadPaginatedProducts() {
        viewModelScope.launch {
            productRepository.getProductsPaged(
                category = _selectedCategory.value
            ).collect { pagingData ->
                _pagingData.value = pagingData
            }
        }
    }

    /**
     * Updates search query.
     * Triggers debounced search automatically.
     *
     * @param query Search string
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Clears search query.
     */
    fun clearSearch() {
        _searchQuery.value = ""
    }

    /**
     * Filters products by category.
     * null = show all categories
     *
     * @param categoryId Category ID or null for all
     */
    fun filterByCategory(categoryId: String?) {
        _selectedCategory.value = categoryId
        loadProducts()
    }

    /**
     * Manually refreshes products cache.
     * Shows pull-to-refresh indicator.
     */
    fun refreshProducts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                productRepository.refreshProducts().collect { result ->
                    _uiState.value = when (result) {
                        is Result.Loading -> UiState.Loading(result.data)
                        is Result.Success -> UiState.Success(result.data ?: emptyList())
                        is Result.Error -> UiState.Error(result.error, result.data)
                    }
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /**
     * Retries loading products after an error.
     */
    fun retryLoad() {
        clearSearch()
        loadProducts()
    }

    /**
     * Clears all cached products.
     */
    fun clearCache() {
        viewModelScope.launch {
            productRepository.clearCache()
            _uiState.value = UiState.Idle
        }
    }
}
