package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.error.GlobalExceptionHandler
import com.noghre.sod.core.util.onError
import com.noghre.sod.core.util.onSuccess
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.domain.repository.ProductRepository
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val exceptionHandler: GlobalExceptionHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val productId: String = savedStateHandle.get<String>("productId") ?: ""
    
    private val _uiState = MutableStateFlow<UiState<Product>>(UiState.Idle)
    val uiState: StateFlow<UiState<Product>> = _uiState.asStateFlow()
    
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()
    
    init {
        loadProductDetail()
    }
    
    fun loadProductDetail() {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = UiState.Loading
            Timber.d("Loading product detail: $productId")
            
            productRepository.getProductById(productId)
                .onSuccess { product ->
                    Timber.d("Product detail loaded: ${product.name}")
                    _uiState.value = UiState.Success(product)
                }
                .onError { error ->
                    Timber.e("Failed to load product detail: ${error.message}")
                    _uiState.value = UiState.Error(error)
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onAddToCartClick() {
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Adding to cart: $productId, quantity: ${_quantity.value}")
            
            cartRepository.addToCart(productId, _quantity.value)
                .onSuccess {
                    Timber.d("Added to cart successfully")
                    _events.send(UiEvent.ShowToast("به سبد خرید اضافه شد"))
                }
                .onError { error ->
                    Timber.e("Failed to add to cart: ${error.message}")
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onFavoriteClick() {
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Toggling favorite: $productId")
            
            productRepository.toggleFavorite(productId)
                .onSuccess {
                    Timber.d("Favorite toggled successfully")
                    _events.send(UiEvent.ShowToast("به علاقه‌مندی‌ها اضافه شد"))
                    loadProductDetail()
                }
                .onError { error ->
                    Timber.e("Failed to toggle favorite: ${error.message}")
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onQuantityIncrease() {
        _quantity.value = (_quantity.value + 1).coerceAtMost(99)
        Timber.d("Quantity increased to: ${_quantity.value}")
    }
    
    fun onQuantityDecrease() {
        _quantity.value = (_quantity.value - 1).coerceAtLeast(1)
        Timber.d("Quantity decreased to: ${_quantity.value}")
    }
    
    fun onRetryClick() {
        Timber.d("Retry clicked")
        loadProductDetail()
    }
}
