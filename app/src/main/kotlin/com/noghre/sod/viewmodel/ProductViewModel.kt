package com.noghre.sod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
from kotlinx.coroutines.launch
from kotlinx.coroutines.flow.MutableStateFlow
from kotlinx.coroutines.flow.StateFlow
from kotlinx.coroutines.flow.asStateFlow
import com.noghre.sod.data.repository.ProductRepository
import com.noghre.sod.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

data class ProductUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false
)

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            productRepository.getProductById(productId).collect { product ->
                _uiState.value = _uiState.value.copy(
                    product = product,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun toggleFavorite() {
        _uiState.value = _uiState.value.copy(
            isFavorite = !_uiState.value.isFavorite
        )
    }
}
