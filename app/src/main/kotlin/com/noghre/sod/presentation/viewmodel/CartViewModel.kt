package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.usecase.cart.ApplyCouponUseCase
import com.noghre.sod.domain.usecase.cart.RemoveCouponUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val shippingCost: Double = 0.0,
    val total: Double = 0.0,
    val appliedCoupon: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val applyCouponUseCase: ApplyCouponUseCase,
    private val removeCouponUseCase: RemoveCouponUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = applyCouponUseCase(ApplyCouponUseCase.Params(code))
                if (result.success) {
                    _uiState.value = _uiState.value.copy(
                        appliedCoupon = code,
                        discountAmount = result.discountAmount,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun removeCoupon() {
        viewModelScope.launch {
            try {
                removeCouponUseCase(Unit)
                _uiState.value = _uiState.value.copy(
                    appliedCoupon = null,
                    discountAmount = 0.0
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
