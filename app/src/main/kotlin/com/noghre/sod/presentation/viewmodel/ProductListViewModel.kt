package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.error.GlobalExceptionHandler
import com.noghre.sod.core.util.onError
import com.noghre.sod.core.util.onSuccess
import com.noghre.sod.domain.model.Product
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
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()
    
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    init {
        loadProducts()
    }
    
    fun loadProducts(categoryId: String? = null) {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = UiState.Loading
            Timber.d("Loading products for category: $categoryId")
            
            val result = if (categoryId != null) {
                productRepository.getProductsByCategory(categoryId)
            } else {
                productRepository.getProducts()
            }
            
            result
                .onSuccess { products ->
                    Timber.d("Products loaded successfully: ${products.size}")
                    _uiState.value = if (products.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(products)
                    }
                }
                .onError { error ->
                    Timber.e("Failed to load products: ${error.message}")
                    _uiState.value = UiState.Error(error)
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onProductClick(productId: String) {
        viewModelScope.launch {
            Timber.d("Product clicked: $productId")
            _events.send(UiEvent.Navigate("product_detail/$productId"))
        }
    }
    
    fun onFavoriteClick(productId: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Toggling favorite for product: $productId")
            
            productRepository.toggleFavorite(productId)
                .onSuccess {
                    Timber.d("Favorite toggled successfully")
                    _events.send(UiEvent.ShowToast("به علاقه‌مندی‌ها اضافه شد"))
                    loadProducts() // Refresh to update UI
                }
                .onError { error ->
                    Timber.e("Failed to toggle favorite: ${error.message}")
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onRetryClick() {
        Timber.d("Retry clicked")
        loadProducts()
    }
    
    fun onRefresh() {
        Timber.d("Pull to refresh triggered")
        loadProducts()
    }
}
