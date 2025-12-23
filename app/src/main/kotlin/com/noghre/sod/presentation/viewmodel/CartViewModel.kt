package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.Cart
import com.noghre.sod.domain.usecase.cart.GetCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val isLoading: Boolean = false,
    val cart: Cart? = null,
    val error: String? = null,
    val itemCount: Int = 0,
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getCartUseCase.execute().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            cart = result.data,
                            itemCount = result.data.items.size,
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
        }
    }

    fun removeItem(itemId: String) {
        // TODO: Implement remove from cart
    }

    fun applyDiscountCode(code: String) {
        // TODO: Implement discount application
    }
}
