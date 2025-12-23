package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.GetProductByIdUseCase
import com.noghre.sod.domain.usecase.AddToCartUseCase
import com.noghre.sod.domain.usecase.ToggleFavoriteUseCase
import com.noghre.sod.domain.usecase.GetRelatedProductsUseCase
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.products.ProductDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for product detail screen.
 * Manages product information, cart operations, and favorites.
 */
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getRelatedProductsUseCase: GetRelatedProductsUseCase
) : ViewModel() {

    private val productId: String? = savedStateHandle["productId"]

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

    init {
        productId?.let { loadProduct(it) }
    }

    /**
     * Load product details
     *
     * @param id Product ID
     */
    fun loadProduct(id: String) {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                val product = getProductByIdUseCase(id)
                updateState {
                    copy(
                        product = product,
                        isFavorite = product.isFavorite,
                        isLoading = false
                    )
                }
                loadRelatedProducts(product.category)
            } catch (e: Exception) {
                Timber.e(e, "Error loading product")
                updateState {
                    copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load product"
                    )
                }
                _events.send(UiEvent.ShowSnackbar("Failed to load product"))
            }
        }
    }

    /**
     * Select image by index
     *
     * @param index Image index
     */
    fun onImageSelected(index: Int) {
        updateState { copy(selectedImageIndex = index) }
    }

    /**
     * Change quantity
     *
     * @param quantity New quantity
     */
    fun onQuantityChanged(quantity: Int) {
        val product = uiState.value.product ?: return
        val validQuantity = quantity.coerceIn(1, product.stock)
        updateState { copy(quantity = validQuantity) }
    }

    /**
     * Add product to cart
     */
    fun addToCart() {
        val state = uiState.value
        val product = state.product ?: return

        if (state.quantity <= 0 || state.quantity > product.stock) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowSnackbar("Invalid quantity"))
            }
            return
        }

        viewModelScope.launch {
            try {
                updateState { copy(isAddingToCart = true) }
                addToCartUseCase(productId = product.id, quantity = state.quantity)
                updateState { copy(isAddingToCart = false) }
                _events.send(UiEvent.ShowSnackbar("Added to cart"))
            } catch (e: Exception) {
                Timber.e(e, "Error adding to cart")
                updateState { copy(isAddingToCart = false) }
                _events.send(UiEvent.ShowSnackbar("Failed to add to cart"))
            }
        }
    }

    /**
     * Toggle favorite status
     */
    fun toggleFavorite() {
        val product = uiState.value.product ?: return

        viewModelScope.launch {
            try {
                updateState { copy(isTogglingFavorite = true) }
                toggleFavoriteUseCase(product.id)
                updateState {
                    copy(
                        isFavorite = !uiState.value.isFavorite,
                        isTogglingFavorite = false
                    )
                }
                val message = if (uiState.value.isFavorite) {
                    "Added to favorites"
                } else {
                    "Removed from favorites"
                }
                _events.send(UiEvent.ShowSnackbar(message))
            } catch (e: Exception) {
                Timber.e(e, "Error toggling favorite")
                updateState { copy(isTogglingFavorite = false) }
                _events.send(UiEvent.ShowSnackbar("Error updating favorite"))
            }
        }
    }

    /**
     * Load related products
     *
     * @param category Category for related products
     */
    private fun loadRelatedProducts(category: String) {
        viewModelScope.launch {
            try {
                val products = getRelatedProductsUseCase(category)
                updateState { copy(relatedProducts = products) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading related products")
            }
        }
    }

    /**
     * Retry loading product
     */
    fun retry() {
        productId?.let { loadProduct(it) }
    }

    private fun updateState(block: ProductDetailUiState.() -> ProductDetailUiState) {
        _uiState.update(block)
    }
}
