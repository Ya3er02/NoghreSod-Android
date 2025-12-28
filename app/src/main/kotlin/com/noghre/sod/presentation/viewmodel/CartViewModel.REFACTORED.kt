package com.noghre.sod.presentation.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.exception.GlobalExceptionHandler
import com.noghre.sod.core.util.UiEvent
import com.noghre.sod.domain.model.Cart
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.usecase.cart.AddToCartUseCase
import com.noghre.sod.domain.usecase.cart.ApplyCouponUseCase
import com.noghre.sod.domain.usecase.cart.CalculateCartTotalUseCase
import com.noghre.sod.domain.usecase.cart.ClearCartUseCase
import com.noghre.sod.domain.usecase.cart.GetCartUseCase
import com.noghre.sod.domain.usecase.cart.RemoveCartItemUseCase
import com.noghre.sod.domain.usecase.cart.RemoveCouponUseCase
import com.noghre.sod.domain.usecase.cart.UpdateCartItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for shopping cart management.
 * 
 * ARCHITECTURE PATTERN (PROPER Clean Architecture):
 * ViewModels INJECT UseCases, NOT Repositories
 * Business logic is in UseCases (domain layer)
 * ViewModels only handle presentation state and user interactions
 * 
 * Benefits:
 * - Easy to test (mock UseCases instead of Repositories)
 * - Single Responsibility (ViewModel = presentation only)
 * - Reusable (UseCases can be used by multiple ViewModels)
 * - Maintainable (business logic in one place)
 */
@Stable
@HiltViewModel
class CartViewModelRefactored @Inject constructor(
    // ✅ CORRECT: Inject UseCases, not Repositories
    private val getCartUseCase: GetCartUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val applyCouponUseCase: ApplyCouponUseCase,
    private val removeCouponUseCase: RemoveCouponUseCase,
    private val calculateCartTotalUseCase: CalculateCartTotalUseCase,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    // ==================== UI State ====================
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private val _cart = MutableStateFlow<Cart?>(null)
    val cart: StateFlow<Cart?> = _cart.asStateFlow()
    
    private val _cartTotal = MutableStateFlow<CartTotalUiModel?>(null)
    val cartTotal: StateFlow<CartTotalUiModel?> = _cartTotal.asStateFlow()
    
    private val _uiEvent = MutableStateFlow<UiEvent?>(null)
    val uiEvent: StateFlow<UiEvent?> = _uiEvent.asStateFlow()
    
    // ==================== Presentation Logic ====================
    
    /**
     * Loads shopping cart.
     * Calls GetCartUseCase which handles all domain logic.
     */
    fun loadCart() {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = UiState.Loading
            
            // ✅ Call UseCase (not Repository)
            val result = getCartUseCase(Unit)
            
            if (result.isSuccess) {
                val cart = result.getOrNull()
                _cart.value = cart
                _uiState.value = UiState.Success
                Timber.d("Cart loaded with ${cart?.items?.size} items")
            } else {
                val error = result.exceptionOrNull()
                _uiState.value = UiState.Error(error?.message ?: "Unknown error")
                Timber.e(error, "Failed to load cart")
            }
        }
    }
    
    /**
     * Adds product to cart.
     * The business logic (inventory check, duplicate handling, etc.)
     * is in AddToCartUseCase, not in this ViewModel.
     */
    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch(exceptionHandler.handler) {
            // ✅ Call UseCase for business logic
            val params = AddToCartUseCase.Params(
                productId = productId,
                quantity = quantity
            )
            val result = addToCartUseCase(params)
            
            if (result.isSuccess) {
                val updatedCart = result.getOrNull()
                _cart.value = updatedCart
                _uiEvent.value = UiEvent.ShowToast("به سبد خرید اضافه شد")
                Timber.d("Added product $productId to cart")
            } else {
                val error = result.exceptionOrNull()
                _uiEvent.value = UiEvent.ShowError(error?.message ?: "خطا در اضافه کردن")
            }
        }
    }
    
    /**
     * Updates cart item quantity.
     * Validation is in UpdateCartItemUseCase.
     */
    fun updateCartItem(cartItemId: String, newQuantity: Int) {
        viewModelScope.launch(exceptionHandler.handler) {
            // ✅ UseCase handles quantity validation
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
                _uiEvent.value = UiEvent.ShowError(error?.message ?: "خطا در به‌روزرسانی")
            }
        }
    }
    
    /**
     * Removes item from cart.
     */
    fun removeCartItem(cartItemId: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            val result = removeCartItemUseCase(cartItemId)
            
            if (result.isSuccess) {
                val updatedCart = result.getOrNull()
                _cart.value = updatedCart
                _uiEvent.value = UiEvent.ShowToast("از سبد خرید حذف شد")
            } else {
                val error = result.exceptionOrNull()
                _uiEvent.value = UiEvent.ShowError(error?.message ?: "خطا در حذف")
            }
        }
    }
    
    /**
     * Clears entire cart.
     */
    fun clearCart() {
        viewModelScope.launch(exceptionHandler.handler) {
            val result = clearCartUseCase(Unit)
            
            if (result.isSuccess) {
                val clearedCart = result.getOrNull()
                _cart.value = clearedCart
                _uiEvent.value = UiEvent.ShowToast("سبد خرید خالی شد")
            }
        }
    }
    
    /**
     * Applies coupon to cart.
     */
    fun applyCoupon(couponCode: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            val result = applyCouponUseCase(couponCode)
            
            if (result.isSuccess) {
                val cartWithCoupon = result.getOrNull()
                _cart.value = cartWithCoupon
                _uiEvent.value = UiEvent.ShowToast("کد تخفیف اعمال شد")
                calculateTotal()
            } else {
                val error = result.exceptionOrNull()
                _uiEvent.value = UiEvent.ShowError(error?.message ?: "کد نامعتبر")
            }
        }
    }
    
    /**
     * Calculates cart total including tax and shipping.
     */
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

// ==================== UI Models ====================

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    object Success : UiState()
    data class Error(val message: String) : UiState()
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
 * ==================== REFACTORING CHECKLIST ====================
 * 
 * This is the CORRECT pattern. When refactoring other ViewModels:
 * 
 * ✅ 1. Remove Repository injection
 *        BEFORE: private val cartRepository: CartRepository
 *        AFTER:  private val getCartUseCase: GetCartUseCase
 * 
 * ✅ 2. Inject all UseCases needed for that ViewModel
 *        Example: For Cart you need:
 *        - GetCartUseCase
 *        - AddToCartUseCase
 *        - UpdateCartItemUseCase
 *        - etc.
 * 
 * ✅ 3. Replace all Repository calls with UseCase calls
 *        BEFORE: cartRepository.getCart()
 *        AFTER:  getCartUseCase(Unit)
 * 
 * ✅ 4. Handle Results properly
 *        result.isSuccess -> do something
 *        result.exceptionOrNull() -> handle error
 * 
 * ✅ 5. Don't put business logic in ViewModel
 *        BEFORE: if (quantity < 1) error...  ❌ WRONG
 *        AFTER:  updateCartItemUseCase(params)  ✅ CORRECT
 * 
 * ✅ 6. Use Timber for logging
 *        Timber.d() for debug
 *        Timber.e(exception, "message") for errors
 * 
 * ✅ 7. Emit UI events for user feedback
 *        UiEvent.ShowToast("success")
 *        UiEvent.ShowError("error")
 * 
 * ✅ 8. Test by creating mock UseCases
 *        @Test
 *        fun test() {
 *            val mockGetCartUseCase = mockk<GetCartUseCase>()
 *            coEvery { mockGetCartUseCase(any()) } returns Result.Success(mockCart)
 *            val viewModel = CartViewModel(...)
 *        }
 */
