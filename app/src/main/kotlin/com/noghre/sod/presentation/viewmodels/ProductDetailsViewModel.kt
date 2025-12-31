package com.noghre.sod.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.domain.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Product Details Screen.
 * 
 * Handles product details loading, favorites management,
 * cart operations, and related products.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _productDetailsUiState = MutableStateFlow(ProductDetailsUiState())
    val productDetailsUiState: StateFlow<ProductDetailsUiState> = _productDetailsUiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Load product details by product ID.
     */
    fun loadProductDetails(productId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Timber.d("Loading product details for: $productId")

                // Simulate loading product details
                // In real app, this would call repository
                val product = productRepository.getProductById(productId)

                _productDetailsUiState.value = _productDetailsUiState.value.copy(
                    product = product
                )

                // Check if product is in wishlist
                checkIsFavorite(productId)

                Timber.d("Product details loaded successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error loading product details")
                _error.value = e.localizedMessage ?: "خطای نامشخص رخ داد"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Check if product is in wishlist.
     */
    private fun checkIsFavorite(productId: String) {
        viewModelScope.launch {
            try {
                val isFav = wishlistRepository.isProductInWishlist(productId)
                _isFavorite.value = isFav
            } catch (e: Exception) {
                Timber.e(e, "Error checking favorite status")
            }
        }
    }

    /**
     * Toggle product favorite status.
     */
    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            try {
                if (_isFavorite.value) {
                    wishlistRepository.removeFromWishlist(productId)
                    _isFavorite.value = false
                    Timber.d("Product removed from wishlist")
                } else {
                    wishlistRepository.addToWishlist(productId)
                    _isFavorite.value = true
                    Timber.d("Product added to wishlist")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling favorite")
                _error.value = "خطا در تغیر وضعیت"
            }
        }
    }

    /**
     * Add product to cart.
     */
    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val product = _productDetailsUiState.value.product
                if (product != null) {
                    cartRepository.addToCart(product, quantity)
                    Timber.d("Product added to cart: $productId")
                    _error.value = null
                } else {
                    _error.value = "محصول یافت نشد"
                }
            } catch (e: Exception) {
                Timber.e(e, "Error adding to cart")
                _error.value = e.localizedMessage ?: "خطا در افزودن به سبد"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _error.value = null
    }
}

/**
 * UI State for Product Details Screen.
 */
data class ProductDetailsUiState(
    val product: Product? = null,
    val relatedProducts: List<Product> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val isInCart: Boolean = false,
    val isInWishlist: Boolean = false
)

/**
 * Review data class.
 */
data class Review(
    val id: String,
    val userId: String,
    val userName: String,
    val rating: Double,
    val title: String,
    val content: String,
    val createdAt: String,
    val helpfulCount: Int = 0
)
