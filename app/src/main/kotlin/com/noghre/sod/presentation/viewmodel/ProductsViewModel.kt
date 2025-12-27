package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.ProductFilters
import com.noghre.sod.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ProductsViewModel - مدیریت لیست محصولات با Pagination و Search
 * Features:
 * - State Management با StateFlow
 * - Pagination Support
 * - Search with Debouncing
 * - Filter/Sort Management
 * - Retry Mechanism
 * - Process Death Recovery
 */
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsRepository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // State Management
    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Initial)
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    // Pagination State
    private val _paginationState = MutableStateFlow(PaginationState())
    val paginationState: StateFlow<PaginationState> = _paginationState.asStateFlow()

    // Search Query with Debouncing
    private val searchQuery = MutableStateFlow("")

    // Loading State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeSearchQuery()
        restoreStateIfNeeded()
        loadProducts()
    }

    /**
     * Observe search query with debouncing (300ms)
     * Prevents excessive API calls while typing
     */
    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isNotEmpty()) {
                    searchProducts(query)
                } else {
                    resetAndLoadProducts()
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Load products with optional filters
     */
    fun loadProducts(filters: ProductFilters = ProductFilters()) {
        viewModelScope.launch {
            _isLoading.value = true
            _uiState.value = ProductsUiState.Loading
            _paginationState.value = PaginationState()

            productsRepository.getProducts(
                page = 1,
                filters = filters
            )
                .onSuccess { response ->
                    _paginationState.update {
                        it.copy(
                            currentPage = 1,
                            hasMorePages = response.hasNextPage,
                            isLoadingMore = false
                        )
                    }
                    _uiState.value = ProductsUiState.Success(
                        products = response.products,
                        filters = filters
                    )
                }
                .onFailure { error ->
                    _uiState.value = ProductsUiState.Error(
                        message = error.message ?: "Failed to load products",
                        type = ErrorType.NETWORK_ERROR
                    )
                }
                .also {
                    _isLoading.value = false
                }
        }
    }

    /**
     * Load next page with pagination and error handling
     */
    fun loadNextPage() {
        val paginationState = _paginationState.value
        if (paginationState.isLoadingMore || !paginationState.hasMorePages) {
            return
        }

        viewModelScope.launch {
            _paginationState.update { it.copy(isLoadingMore = true) }

            val currentState = _uiState.value as? ProductsUiState.Success ?: return@launch
            val nextPage = paginationState.currentPage + 1

            productsRepository.getProducts(
                page = nextPage,
                filters = currentState.filters
            )
                .onSuccess { response ->
                    _paginationState.update {
                        it.copy(
                            currentPage = nextPage,
                            hasMorePages = response.hasNextPage,
                            isLoadingMore = false
                        )
                    }
                    appendProducts(response.products)
                }
                .onFailure { error ->
                    _paginationState.update { it.copy(isLoadingMore = false) }
                    handleError(error)
                }
        }
    }

    /**
     * Append new products to existing list
     */
    private fun appendProducts(newProducts: List<Product>) {
        val currentState = _uiState.value as? ProductsUiState.Success ?: return
        _uiState.value = currentState.copy(
            products = currentState.products + newProducts
        )
    }

    /**
     * Search products with debounced query
     */
    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    /**
     * Perform actual search
     */
    private fun searchProducts(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _paginationState.value = PaginationState()

            productsRepository.searchProducts(query)
                .onSuccess { products ->
                    _uiState.value = ProductsUiState.Success(
                        products = products,
                        filters = ProductFilters()
                    )
                }
                .onFailure { error ->
                    _uiState.value = ProductsUiState.Error(
                        message = "Search failed: ${error.message}",
                        type = ErrorType.NETWORK_ERROR
                    )
                }
                .also { _isLoading.value = false }
        }
    }

    /**
     * Reset search and reload products
     */
    private fun resetAndLoadProducts() {
        loadProducts()
    }

    /**
     * Apply filters to products
     */
    fun applyFilters(filters: ProductFilters) {
        savedStateHandle[KEY_FILTERS] = filters
        loadProducts(filters)
    }

    /**
     * Retry mechanism for failed operations
     */
    fun retry() {
        when (val state = _uiState.value) {
            is ProductsUiState.Error -> loadProducts()
            is ProductsUiState.Success -> {
                if (_paginationState.value.hasMorePages) {
                    loadNextPage()
                }
            }
            else -> Unit
        }
    }

    /**
     * Restore state after process death
     */
    private fun restoreStateIfNeeded() {
        savedStateHandle.get<ProductFilters>(KEY_FILTERS)?.let { filters ->
            val currentState = _uiState.value
            if (currentState is ProductsUiState.Initial) {
                loadProducts(filters)
            }
        }
    }

    /**
     * Handle errors from repository
     */
    private fun handleError(error: Exception) {
        _uiState.value = ProductsUiState.Error(
            message = error.message ?: "Unknown error occurred",
            type = ErrorType.NETWORK_ERROR
        )
    }

    companion object {
        private const val KEY_FILTERS = "product_filters"
    }
}

// UI State
sealed interface ProductsUiState {
    data object Initial : ProductsUiState
    data object Loading : ProductsUiState

    data class Success(
        val products: List<Product>,
        val filters: ProductFilters,
        val retryableError: String? = null
    ) : ProductsUiState

    data class Error(
        val message: String,
        val type: ErrorType
    ) : ProductsUiState
}

// Pagination State
data class PaginationState(
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true,
    val isLoadingMore: Boolean = false
)

// Error Types
enum class ErrorType {
    NETWORK_ERROR, TIMEOUT, SERVER_ERROR, VALIDATION_ERROR
}
