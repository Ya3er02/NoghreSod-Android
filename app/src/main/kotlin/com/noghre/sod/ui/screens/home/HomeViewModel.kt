package com.noghre.sod.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.entities.Category
import com.noghre.sod.domain.entities.Product
import com.noghre.sod.domain.usecases.GetCategoriesUseCase
import com.noghre.sod.domain.usecases.GetProductsUseCase
import com.noghre.sod.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * 🏠 Home Screen ViewModel
 *
 * Responsibilities:
 * - Load featured products and categories
 * - Search and filter functionality
 * - Pagination support
 * - Analytics tracking
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private var currentPage = 1
    private val pageSize = 20
    private var allLoadedProducts = listOf<Product>()
    private var allCategories = listOf<Category>()
    
    private val _filterState = MutableStateFlow<FilterState>(
        FilterState(priceRange = 0f..10000f, category = null)
    )
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    /**
     * Load initial home data: products and categories
     */
    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // Load in parallel
                val productsDeferred = async { getProductsUseCase(page = 1, pageSize = pageSize) }
                val categoriesDeferred = async { getCategoriesUseCase() }
                
                val productsResult = productsDeferred.await()
                val categoriesResult = categoriesDeferred.await()
                
                when {
                    productsResult is Result.Success && categoriesResult is Result.Success -> {
                        allLoadedProducts = productsResult.data
                        allCategories = categoriesResult.data
                        currentPage = 1
                        
                        val homeData = HomeData(
                            featuredProducts = productsResult.data.take(5),
                            categories = categoriesResult.data,
                            allProducts = productsResult.data
                        )
                        _uiState.value = HomeUiState.Success(homeData)
                    }
                    productsResult is Result.Error -> 
                        _uiState.value = HomeUiState.Error(productsResult.error)
                    categoriesResult is Result.Error -> 
                        _uiState.value = HomeUiState.Error(categoriesResult.error)
                    else -> 
                        _uiState.value = HomeUiState.Error(Exception("Unknown error"))
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e)
            }
        }
    }
    
    /**
     * Load more products (pagination)
     */
    fun loadMoreProducts() {
        viewModelScope.launch {
            try {
                val result = getProductsUseCase(
                    page = currentPage + 1,
                    pageSize = pageSize
                )
                
                if (result is Result.Success) {
                    currentPage++
                    allLoadedProducts = allLoadedProducts + result.data
                    
                    if (_uiState.value is HomeUiState.Success) {
                        val currentData = (_uiState.value as HomeUiState.Success).data
                        _uiState.value = HomeUiState.Success(
                            currentData.copy(
                                allProducts = applyFilters(allLoadedProducts)
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Log error but don't change state
            }
        }
    }
    
    /**
     * Set search query and filter results
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterProducts()
    }
    
    /**
     * Filter by category
     */
    fun filterByCategory(categoryId: String) {
        _filterState.value = _filterState.value.copy(category = categoryId)
        filterProducts()
    }
    
    /**
     * Apply price range filter
     */
    fun applyFilters(priceRange: ClosedFloatingPointRange<Float>?, category: String?) {
        _filterState.value = _filterState.value.copy(
            priceRange = priceRange ?: 0f..10000f,
            category = category
        )
        filterProducts()
    }
    
    /**
     * Apply filters and search
     */
    private fun filterProducts() {
        if (_uiState.value !is HomeUiState.Success) return
        
        val currentData = (_uiState.value as HomeUiState.Success).data
        val filter = _filterState.value
        val query = _searchQuery.value
        
        val filtered = allLoadedProducts.filter { product ->
            val matchesSearch = query.isEmpty() || 
                product.name.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true)
            
            val matchesCategory = filter.category == null || 
                product.categoryId == filter.category
            
            val matchesPrice = product.price.amount in filter.priceRange
            
            matchesSearch && matchesCategory && matchesPrice
        }
        
        _uiState.value = HomeUiState.Success(
            currentData.copy(allProducts = filtered)
        )
    }
    
    /**
     * Helper to apply filters to a product list
     */
    private fun applyFilters(products: List<Product>): List<Product> {
        val filter = _filterState.value
        val query = _searchQuery.value
        
        return products.filter { product ->
            val matchesSearch = query.isEmpty() || 
                product.name.contains(query, ignoreCase = true)
            val matchesCategory = filter.category == null || 
                product.categoryId == filter.category
            val matchesPrice = product.price.amount in filter.priceRange
            
            matchesSearch && matchesCategory && matchesPrice
        }
    }
}

data class FilterState(
    val priceRange: ClosedFloatingPointRange<Float>,
    val category: String?
)
