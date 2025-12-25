package com.noghre.sod.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.ui.UiState
import com.noghre.sod.core.ui.toUiError
import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Home screen ViewModel.
 * Manages product listing with proper state management and error handling.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private companion object {
        const val TAG = "HomeViewModel"
        const val PAGE_SIZE = 20
    }

    // ===== State Management =====
    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Idle)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private var isLoadingMore = false

    // ===== Init =====
    init {
        loadHome()
    }

    // ===== Public Methods =====

    /**
     * Load home data (products and categories).
     */
    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _currentPage.value = 1
            fetchData()
        }
    }

    /**
     * Retry loading data.
     */
    fun retry() {
        loadHome()
    }

    /**
     * Load next page of products.
     */
    fun loadNextPage() {
        if (isLoadingMore || _uiState.value !is UiState.Success) return

        viewModelScope.launch {
            isLoadingMore = true
            val nextPage = (_currentPage.value + 1)
            _currentPage.value = nextPage
            fetchData(nextPage)
        }
    }

    /**
     * Filter by category.
     */
    fun selectCategory(category: Category?) {
        _selectedCategory.value = category
        _currentPage.value = 1
        loadHome()
    }

    /**
     * Search products.
     */
    fun searchProducts(query: String) {
        _searchQuery.value = query
        _currentPage.value = 1
        loadHome()
    }

    /**
     * Clear search.
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _currentPage.value = 1
        loadHome()
    }

    // ===== Private Methods =====

    /**
     * Fetch data from use case.
     */
    private suspend fun fetchData(page: Int = 1) {
        try {
            getProductsUseCase(
                page = page,
                pageSize = PAGE_SIZE,
                query = _searchQuery.value.takeIf { it.isNotBlank() },
                categoryId = _selectedCategory.value?.id,
                sortBy = "popular"
            )
                .catch { e ->
                    Log.e(TAG, "Error fetching products", e)
                    _uiState.value = UiState.Error(
                        error = e.toUiError(),
                        canRetry = true
                    )
                }
                .collect { result ->
                    result
                        .onSuccess { products ->
                            if (products.isEmpty()) {
                                _uiState.value = UiState.Empty
                            } else {
                                val currentData = (_uiState.value as? UiState.Success)?.data
                                val updatedProducts = if (page == 1) {
                                    products
                                } else {
                                    (currentData?.products ?: emptyList()) + products
                                }

                                _uiState.value = UiState.Success(
                                    HomeData(
                                        products = updatedProducts,
                                        featuredProducts = if (page == 1) {
                                            products.take(5)
                                        } else {
                                            currentData?.featuredProducts ?: emptyList()
                                        },
                                        categories = currentData?.categories ?: emptyList()
                                    )
                                )
                            }
                            isLoadingMore = false
                        }
                        .onFailure { e ->
                            Log.e(TAG, "Error loading products", e)
                            _uiState.value = UiState.Error(
                                error = e.toUiError(),
                                canRetry = true
                            )
                            isLoadingMore = false
                        }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching data", e)
            _uiState.value = UiState.Error(
                error = e.toUiError(),
                canRetry = true
            )
            isLoadingMore = false
        }
    }
}

/**
 * Data class for home screen content.
 */
data class HomeData(
    val products: List<Product>,
    val featuredProducts: List<Product>,
    val categories: List<Category>
)