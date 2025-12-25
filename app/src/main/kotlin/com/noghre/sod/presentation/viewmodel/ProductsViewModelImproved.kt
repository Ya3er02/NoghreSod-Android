package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.GetProductsUseCase
import com.noghre.sod.domain.model.AppException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Improved ProductsViewModel with proper state management.
 * Uses sealed classes for UI state and immutable StateFlow.
 */
@HiltViewModel
class ProductsViewModelImproved @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    // ============== UI STATE ============== //

    /**
     * Sealed class representing all possible UI states.
     * Only immutable state is exposed to UI.
     */
    sealed class UiState<out T> {
        object Idle : UiState<Nothing>()
        object Loading : UiState<Nothing>()
        data class Success<T>(
            val data: T,
            val loadedAt: Long = System.currentTimeMillis()
        ) : UiState<T>()
        data class Error(
            val message: String,
            val exception: AppException? = null,
            val errorCode: Int = -1
        ) : UiState<Nothing>()
        object Empty : UiState<Nothing>()
    }

    // ============== PRIVATE MUTABLE STATE ============== //

    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)

    private val _currentPage = MutableStateFlow(1)

    private val _pageSize = MutableStateFlow(20)

    private val _selectedCategory = MutableStateFlow<String?>(null)

    private val _selectedProduct = MutableStateFlow<Product?>(null)

    private val _errorState = MutableStateFlow<AppException?>(null)

    private val _isLoadingMore = MutableStateFlow(false)

    // ============== PUBLIC IMMUTABLE STATE ============== //

    /**
     * UI state exposed to the composable.
     * This is immutable - UI cannot modify it directly.
     */
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    val pageSize: StateFlow<Int> = _pageSize.asStateFlow()

    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    val errorState: StateFlow<AppException?> = _errorState.asStateFlow()

    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    // ============== PUBLIC FUNCTIONS ============== //

    /**
     * Load products for the first time.
     */
    fun loadProducts() {
        _currentPage.value = 1
        fetchProducts()
    }

    /**
     * Load next page of products.
     */
    fun loadMoreProducts() {
        if (_isLoadingMore.value) return
        
        _isLoadingMore.value = true
        _currentPage.value += 1
        fetchProducts(appendMode = true)
    }

    /**
     * Filter products by category.
     */
    fun filterByCategory(category: String?) {
        _selectedCategory.value = category
        _currentPage.value = 1
        fetchProducts()
    }

    /**
     * Select a product for detail view.
     */
    fun selectProduct(product: Product) {
        _selectedProduct.value = product
        Timber.d("Product selected: ${product.name}")
    }

    /**
     * Clear product selection.
     */
    fun clearSelection() {
        _selectedProduct.value = null
    }

    /**
     * Clear error state.
     */
    fun clearError() {
        _errorState.value = null
    }

    /**
     * Retry loading after error.
     */
    fun retry() {
        _currentPage.value = 1
        _errorState.value = null
        fetchProducts()
    }

    // ============== PRIVATE FUNCTIONS ============== //

    /**
     * Fetch products from repository.
     * Handles loading state, errors, and success cases.
     */
    private fun fetchProducts(appendMode: Boolean = false) {
        viewModelScope.launch {
            try {
                // Show loading state only if not appending
                if (!appendMode) {
                    _uiState.value = UiState.Loading
                }

                // Fetch products from use case
                val products = getProductsUseCase(
                    page = _currentPage.value,
                    pageSize = _pageSize.value,
                    category = _selectedCategory.value
                )

                // Handle empty results
                if (products.isEmpty() && !appendMode) {
                    _uiState.value = UiState.Empty
                    Timber.d("No products found")
                    return@launch
                }

                // Update state based on mode
                if (appendMode) {
                    // Append to existing list
                    val currentList = (_uiState.value as? UiState.Success)?.data.orEmpty()
                    val newList = currentList + products
                    _uiState.value = UiState.Success(newList)
                } else {
                    // Replace entire list
                    _uiState.value = UiState.Success(products)
                }

                // Clear error state on success
                _errorState.value = null
                Timber.d("Loaded ${products.size} products")

            } catch (e: AppException) {
                Timber.e("AppException: ${e.message}")
                _errorState.value = e
                _uiState.value = UiState.Error(
                    message = e.getUserMessage(),
                    exception = e,
                    errorCode = (e as? AppException.ServerError)?.code ?: -1
                )
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error")
                val unknownException = AppException.UnknownError(e.message ?: "Unknown error", e)
                _errorState.value = unknownException
                _uiState.value = UiState.Error(
                    message = unknownException.getUserMessage(),
                    exception = unknownException
                )
            } finally {
                _isLoadingMore.value = false
            }
        }
    }
}
