package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.R
import com.noghre.sod.core.exception.GlobalExceptionHandler
import com.noghre.sod.core.util.UiEvent
import com.noghre.sod.domain.common.ResourceProvider
import com.noghre.sod.domain.model.Cart
import com.noghre.sod.domain.usecase.cart.AddToCartUseCase
import com.noghre.sod.domain.usecase.cart.ApplyCouponUseCase
import com.noghre.sod.domain.usecase.cart.CalculateCartTotalUseCase
import com.noghre.sod.domain.usecase.cart.ClearCartUseCase
import com.noghre.sod.domain.usecase.cart.GetCartUseCase
import com.noghre.sod.domain.usecase.cart.RemoveCartItemUseCase
import com.noghre.sod.domain.usecase.cart.RemoveCouponUseCase
import com.noghre.sod.domain.usecase.cart.UpdateCartItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class CartUiState {
    object Idle : CartUiState()
    object Loading : CartUiState()
    object Success : CartUiState()
    data class Error(val message: String) : CartUiState()
}

data class CartTotalUiModel(
    val itemsTotal: Long,
    val discountAmount: Long,
    val subtotal: Long,
    val shippingCost: Long,
    val taxAmount: Long,
    val totalPrice: Long
)

/**
 * ✅ REFACTORED: Cart ViewModel with proper Clean Architecture
 * 
 * Key changes from old version:
 * 1. ❌ NO direct Repository injection
 * 2. ✅ ALL UseCases injected
 * 3. ✅ Business logic in domain layer
 * 4. ✅ Proper error handling with AppError
 * 5. ✅ Resource strings from ResourceProvider
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    // ✅ Inject ALL needed UseCases (NOT Repository)
    private val getCartUseCase: GetCartUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val applyCouponUseCase: ApplyCouponUseCase,
    private val removeCouponUseCase: RemoveCouponUseCase,
    private val calculateCartTotalUseCase: CalculateCartTotalUseCase,
    private val resourceProvider: ResourceProvider,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    // ==================== UI State ====================
    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Idle)
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()
    
    private val _cart = MutableStateFlow<Cart?>(null)
    val cart: StateFlow<Cart?> = _cart.asStateFlow()
    
    private val _cartTotal = MutableStateFlow<CartTotalUiModel?>(null)
    val cartTotal: StateFlow<CartTotalUiModel?> = _cartTotal.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    
    // ==================== Public Methods ====================
    
    fun loadCart() {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = CartUiState.Loading
            
            val result = getCartUseCase(Unit)
            
            if (result.isSuccess) {
                val cart = result.getOrNull()
                _cart.value = cart
                _uiState.value = CartUiState.Success
                Timber.d("Cart loaded with ${cart?.items?.size} items")
            } else {
                val error = result.exceptionOrNull()
                _uiState.value = CartUiState.Error(error?.message ?: "Unknown error")
                Timber.e(error, "Failed to load cart")
            }
        }
    }
    
    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch(exceptionHandler.handler) {
            val params = AddToCartUseCase.Params(
                productId = productId,
                quantity = quantity
            )
            val result = addToCartUseCase(params)
            
            if (result.isSuccess) {
                val updatedCart = result.getOrNull()
                _cart.value = updatedCart
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        resourceProvider.getString(R.string.success_added_to_cart)
                    )
                )
                Timber.d("Added product $productId to cart")
            } else {
                val error = result.exceptionOrNull()
                _uiEvent.emit(UiEvent.ShowError(error?.message ?: "Error adding to cart"))
            }
        }
    }
    
    fun updateCartItem(cartItemId: String, newQuantity: Int) {
        viewModelScope.launch(exceptionHandler.handler) {
            val params = UpdateCartItemUseCase.Params(
                cartItemId = cartItemId,
                newQuantity = newQuantity
            )
            val result = updateCartItemUseCase(params)
            
            if (result.isSuccess) {
                val updatedCart = result.getOrNull()
                _cart.value = updatedCart
                Timber.d("Updated cart item $cartItemId to quantity $newQuantity")
            } else {
                val error = result.exceptionOrNull()
                _uiEvent.emit(UiEvent.ShowError(error?.message ?: "Error updating quantity"))
            }
        }
    }
    
    fun removeCartItem(cartItemId: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            val result = removeCartItemUseCase(cartItemId)
            
            if (result.isSuccess) {
                val updatedCart = result.getOrNull()
                _cart.value = updatedCart
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        resourceProvider.getString(R.string.success_removed_from_cart)
                    )
                )
            } else {
                val error = result.exceptionOrNull()
                _uiEvent.emit(UiEvent.ShowError(error?.message ?: "Error removing item"))
            }
        }
    }
    
    fun clearCart() {
        viewModelScope.launch(exceptionHandler.handler) {
            val result = clearCartUseCase(Unit)
            
            if (result.isSuccess) {
                val clearedCart = result.getOrNull()
                _cart.value = clearedCart
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        resourceProvider.getString(R.string.success_cart_cleared)
                    )
                )
            }
        }
    }
    
    fun applyCoupon(couponCode: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            val result = applyCouponUseCase(couponCode)
            
            if (result.isSuccess) {
                val cartWithCoupon = result.getOrNull()
                _cart.value = cartWithCoupon
                _uiEvent.emit(UiEvent.ShowToast("Coupon applied successfully"))
                calculateTotal()
            } else {
                val error = result.exceptionOrNull()
                _uiEvent.emit(UiEvent.ShowError(error?.message ?: "Invalid coupon"))
            }
        }
    }
    
    fun removeCoupon() {
        viewModelScope.launch(exceptionHandler.handler) {
            val result = removeCouponUseCase(Unit)
            
            if (result.isSuccess) {
                val cartWithoutCoupon = result.getOrNull()
                _cart.value = cartWithoutCoupon
                _uiEvent.emit(UiEvent.ShowToast("Coupon removed"))
                calculateTotal()
            }
        }
    }
    
    fun calculateTotal(shippingCost: Long = 0) {
        viewModelScope.launch(exceptionHandler.handler) {
            val params = CalculateCartTotalUseCase.Params(
                shippingCost = shippingCost,
                includeTax = true
            )
            val result = calculateCartTotalUseCase(params)
            
            if (result.isSuccess) {
                val calculation = result.getOrNull()
                _cartTotal.value = CartTotalUiModel(
                    itemsTotal = calculation.itemsTotal,
                    discountAmount = calculation.discountAmount,
                    subtotal = calculation.subtotal,
                    shippingCost = calculation.shippingCost,
                    taxAmount = calculation.taxAmount,
                    totalPrice = calculation.totalPrice
                )
            }
        }
    }
}
