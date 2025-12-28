package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.R
import com.noghre.sod.core.exception.GlobalExceptionHandler
import com.noghre.sod.core.util.UiEvent
import com.noghre.sod.domain.common.ResourceProvider
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.cart.AddToCartUseCase
import com.noghre.sod.domain.usecase.product.GetProductByIdUseCase
import com.noghre.sod.domain.usecase.product.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class ProductDetailUiState {
    object Idle : ProductDetailUiState()
    object Loading : ProductDetailUiState()
    data class Success(val product: Product) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    // ✅ Product UseCases
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    
    // ✅ Cart UseCase
    private val addToCartUseCase: AddToCartUseCase,
    
    // Infrastructure
    private val resourceProvider: ResourceProvider,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Idle)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()
    
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    private val _selectedQuantity = MutableStateFlow(1)
    val selectedQuantity: StateFlow<Int> = _selectedQuantity.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    
    fun loadProduct(productId: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = ProductDetailUiState.Loading
            
            val result = getProductByIdUseCase(productId)
            
            if (result.isSuccess) {
                val product = result.getOrNull()
                _product.value = product
                _uiState.value = ProductDetailUiState.Success(product!!)
                Timber.d("Loaded product: ${product.id}")
            } else {
                val error = result.exceptionOrNull()
                _uiState.value = ProductDetailUiState.Error(error?.message ?: "Failed to load product")
            }
        }
    }
    
    fun toggleFavorite() {
        viewModelScope.launch(exceptionHandler.handler) {
            val productId = _product.value?.id ?: return@launch
            
            val result = toggleFavoriteUseCase(productId)
            
            if (result.isSuccess) {
                val isFavorite = result.getOrNull() ?: false
                _isFavorite.value = isFavorite
                
                if (isFavorite) {
                    _uiEvent.emit(
                        UiEvent.ShowToast(
                            resourceProvider.getString(R.string.success_added_to_favorites)
                        )
                    )
                } else {
                    _uiEvent.emit(UiEvent.ShowToast("Removed from favorites"))
                }
            }
        }
    }
    
    fun addToCart() {
        viewModelScope.launch(exceptionHandler.handler) {
            val productId = _product.value?.id ?: return@launch
            val quantity = _selectedQuantity.value
            
            val params = AddToCartUseCase.Params(
                productId = productId,
                quantity = quantity
            )
            val result = addToCartUseCase(params)
            
            if (result.isSuccess) {
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        resourceProvider.getString(R.string.success_added_to_cart)
                    )
                )
                Timber.d("Added $quantity of $productId to cart")
            } else {
                val error = result.exceptionOrNull()
                _uiEvent.emit(UiEvent.ShowError(error?.message ?: "Failed to add to cart"))
            }
        }
    }
    
    fun setQuantity(quantity: Int) {
        if (quantity >= 1 && quantity <= 999) {
            _selectedQuantity.value = quantity
        }
    }
}
