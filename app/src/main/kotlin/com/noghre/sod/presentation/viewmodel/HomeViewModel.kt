package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.ProductSummary
import com.noghre.sod.domain.usecase.product.GetAllProductsUseCase
import com.noghre.sod.domain.usecase.product.GetFeaturedProductsUseCase
import com.noghre.sod.domain.usecase.product.GetProductsParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val featuredProducts: List<ProductSummary> = emptyList(),
    val allProducts: List<ProductSummary> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val getFeaturedProductsUseCase: GetFeaturedProductsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Load featured products
                getFeaturedProductsUseCase.execute().collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.value = _uiState.value.copy(
                                featuredProducts = result.data,
                            )
                        }
                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                error = result.exception.message,
                            )
                        }
                        is Result.Loading -> {}
                    }
                }

                // Load all products
                getAllProductsUseCase.execute(GetProductsParams()).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.value = _uiState.value.copy(
                                allProducts = result.data,
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
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false,
                )
            }
        }
    }

    fun loadMore() {
        // TODO: Implement pagination
    }
}
