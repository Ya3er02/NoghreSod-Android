package com.noghre.sod.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.WishlistRepository
import com.noghre.sod.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Wishlist Screen.
 * 
 * Handles wishlist management, price tracking,
 * and cart operations from wishlist.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _wishlistUiState = MutableStateFlow(WishlistUiState())
    val wishlistUiState: StateFlow<WishlistUiState> = _wishlistUiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadWishlist()
    }

    /**
     * Load all wishlist items.
     */
    fun loadWishlist() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Timber.d("Loading wishlist items")

                val items = wishlistRepository.getWishlist()

                _wishlistUiState.value = _wishlistUiState.value.copy(
                    items = items
                )

                Timber.d("Wishlist loaded: ${items.size} items")
            } catch (e: Exception) {
                Timber.e(e, "Error loading wishlist")
                _error.value = e.localizedMessage ?: "خطای نامشخص رخ داد"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Remove product from wishlist.
     */
    fun removeFromWishlist(productId: String) {
        viewModelScope.launch {
            try {
                Timber.d("Removing product from wishlist: $productId")

                wishlistRepository.removeFromWishlist(productId)

                // Update UI state
                val updatedItems = _wishlistUiState.value.items.filter { it.id != productId }
                _wishlistUiState.value = _wishlistUiState.value.copy(
                    items = updatedItems
                )

                Timber.d("Product removed from wishlist")
            } catch (e: Exception) {
                Timber.e(e, "Error removing from wishlist")
                _error.value = e.localizedMessage ?: "خطا در حذف"
            }
        }
    }

    /**
     * Add product to cart from wishlist.
     */
    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Timber.d("Adding product to cart from wishlist: ${product.id}")

                cartRepository.addToCart(product, quantity)

                Timber.d("Product added to cart successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error adding to cart")
                _error.value = e.localizedMessage ?: "خطا در افزودن به سبد"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Share wishlist.
     */
    fun shareWishlist() {
        viewModelScope.launch {
            try {
                Timber.d("Sharing wishlist")
                // TODO: Implement share functionality
            } catch (e: Exception) {
                Timber.e(e, "Error sharing wishlist")
                _error.value = e.localizedMessage ?: "خطا در اشتراک"
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
 * UI State for Wishlist Screen.
 */
data class WishlistUiState(
    val items: List<Product> = emptyList(),
    val priceDropNotifications: Map<String, Double> = emptyMap()
)
