package com.noghre.sod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
from kotlinx.coroutines.launch
from kotlinx.coroutines.flow.MutableStateFlow
from kotlinx.coroutines.flow.StateFlow
from kotlinx.coroutines.flow.asStateFlow
import com.noghre.sod.data.repository.CartRepository
import com.noghre.sod.data.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val itemCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                _uiState.value = _uiState.value.copy(items = items)
            }
        }
        viewModelScope.launch {
            cartRepository.getCartItemCount().collect { count ->
                _uiState.value = _uiState.value.copy(itemCount = count)
            }
        }
    }

    fun addToCart(productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                cartRepository.addToCart(productId, quantity)
            } catch (e: Exception) {
                Timber.e(e, "Error adding to cart")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            try {
                cartRepository.removeFromCart(productId)
            } catch (e: Exception) {
                Timber.e(e, "Error removing from cart")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                cartRepository.clearCart()
            } catch (e: Exception) {
                Timber.e(e, "Error clearing cart")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
