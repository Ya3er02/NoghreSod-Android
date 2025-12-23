package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.ProductDetail
import com.noghre.sod.domain.usecase.product.GetProductByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: ProductDetail? = null,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val quantity: Int = 1,
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val productId: String = savedStateHandle["productId"] ?: ""

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getProductByIdUseCase.execute(productId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            product = result.data,
                            isLoading = false,
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = result.exception.message,
                            isLoading = false,
                        )
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }

    fun toggleFavorite() {
        _uiState.value = _uiState.value.copy(
            isFavorite = !_uiState.value.isFavorite
        )
    }

    fun updateQuantity(quantity: Int) {
        if (quantity > 0) {
            _uiState.value = _uiState.value.copy(quantity = quantity)
        }
    }
}
