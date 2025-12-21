package com.noghre.sod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
from kotlinx.coroutines.launch
from kotlinx.coroutines.flow.MutableStateFlow
from kotlinx.coroutines.flow.StateFlow
from kotlinx.coroutines.flow.asStateFlow
import com.noghre.sod.data.repository.ProductRepository
import com.noghre.sod.data.model.Product
import com.noghre.sod.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts(page: Int = 1) {
        viewModelScope.launch {
            productRepository.fetchProducts(page).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            products = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun retryLoadProducts() {
        loadProducts()
    }
}
