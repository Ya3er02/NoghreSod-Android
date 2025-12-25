package com.noghre.sod.ui.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.entities.CartItem
import com.noghre.sod.domain.usecases.AddToCartUseCase
import com.noghre.sod.domain.usecases.GetProductDetailUseCase
import com.noghre.sod.domain.usecases.GetWishlistUseCase
import com.noghre.sod.domain.usecases.ToggleWishlistUseCase
import com.noghre.sod.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val toggleWishlistUseCase: ToggleWishlistUseCase,
    private val getWishlistUseCase: GetWishlistUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()
    
    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()
    
    private val _isInWishlist = MutableStateFlow(false)
    val isInWishlist: StateFlow<Boolean> = _isInWishlist.asStateFlow()
    
    /**
     * Load product details
     */
    fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading
            try {
                when (val result = getProductDetailUseCase(productId)) {
                    is Result.Success -> {
                        _uiState.value = ProductDetailUiState.Success(result.data)
                        checkIfInWishlist(productId)
                    }
                    is Result.Error -> 
                        _uiState.value = ProductDetailUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _uiState.value = ProductDetailUiState.Error(e)
            }
        }
    }
    
    /**
     * Set quantity
     */
    fun setQuantity(qty: Int) {
        if (qty > 0) {
            _quantity.value = qty
        }
    }
    
    /**
     * Add product to cart
     */
    fun addToCart(productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                val cartItem = CartItem(
                    productId = productId,
                    quantity = quantity,
                    productName = "",
                    price = 0,
                    imageUrl = ""
                )
                addToCartUseCase(cartItem)
                _quantity.value = 1 // Reset quantity
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    /**
     * Check if product is in wishlist
     */
    private fun checkIfInWishlist(productId: String) {
        viewModelScope.launch {
            try {
                val wishlist = getWishlistUseCase()
                _isInWishlist.value = wishlist.any { it.id == productId }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    /**
     * Toggle wishlist
     */
    fun toggleWishlist(productId: String) {
        viewModelScope.launch {
            try {
                toggleWishlistUseCase(productId)
                _isInWishlist.value = !_isInWishlist.value
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
