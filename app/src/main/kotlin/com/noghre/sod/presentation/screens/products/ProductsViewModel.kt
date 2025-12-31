package com.noghre.sod.presentation.screens.products

import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.ProductFilters
import com.noghre.sod.domain.model.Result
import com.noghre.sod.domain.usecase.product.GetProductsUseCase
import com.noghre.sod.domain.usecase.product.SearchProductsUseCase
import com.noghre.sod.presentation.base.BaseViewModel
import com.noghre.sod.presentation.base.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * State for ProductsScreen.
 */
data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val filters: ProductFilters = ProductFilters(),
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val selectedProduct: Product? = null
)

/**
 * ViewModel for Products screen.
 * 
 * Manages product list, filtering, searching, and pagination.
 * Features:
 * - Load and paginate products
 * - Advanced filtering by price, category, rating
 * - Real-time search
 * - Filter management
 * - Product selection for detail view
 * 
 * @author NoghreSod Team
 * @version 1.1.0
 */
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase
) : BaseViewModel<ProductsUiState>() {

    private val _productsUiState = MutableStateFlow(ProductsUiState())
    val productsUiState = _productsUiState.asStateFlow()

    init {
        loadProducts()
    }

    /**
     * Load initial products.
     */
    fun loadProducts() {
        launchAsync(
            block = {
                val state = _productsUiState.value
                getProductsUseCase(
                    filters = state.filters,
                    page = 1
                ).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _productsUiState.value = state.copy(
                                products = result.data,
                                currentPage = 1,
                                hasMorePages = result.data.size >= 20
                            )
                        }
                        is Result.Error -> {
                            setError(result.message)
                            emitEvent(UiEvent.Error(result.exception))
                        }
                    }
                }
            }
        )
    }

    /**
     * Load next page of products.
     */
    fun loadMore() {
        val state = _productsUiState.value
        if (!state.hasMorePages || isLoading.value) return
        
        launchAsync(
            block = {
                getProductsUseCase(
                    filters = state.filters,
                    page = state.currentPage + 1
                ).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _productsUiState.value = state.copy(
                                products = state.products + result.data,
                                currentPage = state.currentPage + 1,
                                hasMorePages = result.data.size >= 20
                            )
                        }
                        is Result.Error -> setError(result.message)
                    }
                }
            }
        )
    }

    /**
     * Search products by query.
     */
    fun searchProducts(query: String) {
        if (query.isEmpty()) {
            loadProducts()
            return
        }
        
        launchAsync(
            block = {
                searchProductsUseCase(query).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _productsUiState.value = _productsUiState.value.copy(
                                products = result.data,
                                currentPage = 1
                            )
                        }
                        is Result.Error -> setError(result.message)
                    }
                }
            }
        )
    }

    /**
     * Apply filters using ProductFilters object.
     * 
     * @param filters ProductFilters object with all filter criteria
     */
    fun applyFilters(filters: ProductFilters) {
        _productsUiState.value = _productsUiState.value.copy(
            filters = filters,
            currentPage = 1
        )
        loadProducts()
    }

    /**
     * Apply filters using individual parameters.
     * Convenience method for direct parameter passing from UI.
     * 
     * @param minPrice Minimum price filter
     * @param maxPrice Maximum price filter
     * @param category Product category filter
     * @param rating Minimum rating filter
     * @param sortBy Sort field (latest, price_asc, price_desc, rating, popularity)
     */
    fun applyFilters(
        minPrice: Float = 0f,
        maxPrice: Float = Float.MAX_VALUE,
        category: String? = null,
        rating: Double = 0.0,
        sortBy: String = "latest"
    ) {
        val filters = ProductFilters(
            minPrice = minPrice.toDouble(),
            maxPrice = maxPrice.toDouble(),
            category = category,
            minRating = rating,
            sortBy = sortBy
        )
        applyFilters(filters)
    }

    /**
     * Select product for detail view.
     */
    fun selectProduct(product: Product) {
        _productsUiState.value = _productsUiState.value.copy(
            selectedProduct = product
        )
        emitEvent(UiEvent.Navigate("product/${product.id}"))
    }

    /**
     * Clear filters and reset to default.
     */
    fun clearFilters() {
        applyFilters(ProductFilters())
    }
}
