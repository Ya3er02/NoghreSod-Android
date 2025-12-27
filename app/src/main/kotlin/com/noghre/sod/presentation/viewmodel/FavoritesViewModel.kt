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
class FavoritesViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()
    
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    init {
        loadFavorites()
    }
    
    fun loadFavorites() {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = UiState.Loading
            Timber.d("Loading favorites")
            
            productRepository.getFavorites()
                .onSuccess { products ->
                    Timber.d("Favorites loaded: ${products.size}")
                    _uiState.value = if (products.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(products)
                    }
                }
                .onError { error ->
                    Timber.e("Failed to load favorites: ${error.message}")
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
    
    fun onRemoveFromFavorites(productId: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Removing from favorites: $productId")
            
            productRepository.removeFavorite(productId)
                .onSuccess {
                    Timber.d("Removed from favorites successfully")
                    _events.send(UiEvent.ShowToast("از علاقه‌مندی‌ها حذف شد"))
                    loadFavorites()
                }
                .onError { error ->
                    Timber.e("Failed to remove from favorites: ${error.message}")
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onAddToCart(productId: String) {
        viewModelScope.launch {
            Timber.d("Add to cart: $productId")
            _events.send(UiEvent.Navigate("product_detail/$productId"))
        }
    }
    
    fun onRetryClick() {
        Timber.d("Retry clicked")
        loadFavorites()
    }
}
