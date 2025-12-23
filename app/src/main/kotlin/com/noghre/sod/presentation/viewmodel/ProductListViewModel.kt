package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.ProductSummary
import com.noghre.sod.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductListUiState(
    val products: List<ProductSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val hasMore: Boolean = true,
    val page: Int = 1
)

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts(page: Int = 1) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = getProductsUseCase(
                page = page,
                limit = 20,
                category = _uiState.value.selectedCategory
            )
            result.onSuccess { products ->
                _uiState.value = _uiState.value.copy(
                    products = if (page == 1) products else _uiState.value.products + products,
                    isLoading = false,
                    hasMore = products.size >= 20,
                    page = page
                )
            }
            result.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    error = exception.message,
                    isLoading = false
                )
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(searchQuery = query)
            loadProducts(page = 1)
        }
    }

    fun filterByCategory(category: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedCategory = category)
            loadProducts(page = 1)
        }
    }

    fun loadMore() {
        if (_uiState.value.hasMore && !_uiState.value.isLoading) {
            loadProducts(page = _uiState.value.page + 1)
        }
    }
}
