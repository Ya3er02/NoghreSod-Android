package com.noghre.sod.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.domain.repository.OrderRepository
import com.noghre.sod.presentation.screens.checkout.CheckoutUiState
import com.noghre.sod.presentation.screens.checkout.PaymentMethod
import com.noghre.sod.presentation.screens.checkout.ShippingMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Checkout Screen.
 * 
 * Handles order processing, payment methods,
 * shipping options, and coupon codes.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _checkoutUiState = MutableStateFlow(CheckoutUiState())
    val checkoutUiState: StateFlow<CheckoutUiState> = _checkoutUiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCheckoutData()
    }

    /**
     * Load checkout data including cart and available options.
     */
    private fun loadCheckoutData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Timber.d("Loading checkout data")

                // Load cart items
                val cartItems = cartRepository.getCartItems()

                // Calculate subtotal
                var subtotal = 0.0
                cartItems.forEach { item ->
                    subtotal += item.price * item.quantity
                }

                val tax = subtotal * 0.09 // 9% tax

                // Create shipping methods
                val shippingMethods = listOf(
                    ShippingMethod(
                        id = "standard",
                        name = "راه معمولی",
                        estimatedDays = "3-5 روز",
                        cost = 5000.0
                    ),
                    ShippingMethod(
                        id = "express",
                        name = "تحويل سريع",
                        estimatedDays = "1-2 روز",
                        cost = 15000.0
                    ),
                    ShippingMethod(
                        id = "overnight",
                        name = "شب به روز",
                        estimatedDays = "فردا صبح",
                        cost = 30000.0
                    )
                )

                // Create payment methods
                val paymentMethods = listOf(
                    PaymentMethod(id = "zarinpal", name = "زرین پال"),
                    PaymentMethod(id = "nextpay", name = "نکست پی"),
                    PaymentMethod(id = "bazarpay", name = "بازار پی"),
                    PaymentMethod(id = "cash", name = "پرداخت در محل")
                )

                _checkoutUiState.value = CheckoutUiState(
                    cartItems = cartItems,
                    subtotal = subtotal,
                    tax = tax,
                    shippingCost = 5000.0, // Default standard shipping
                    total = subtotal + tax + 5000.0,
                    shippingMethods = shippingMethods,
                    paymentMethods = paymentMethods
                )

                Timber.d("Checkout data loaded successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error loading checkout data")
                _error.value = e.localizedMessage ?: "خطا در بارگیری دادهه"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Select shipping method.
     */
    fun selectShippingMethod(methodId: String) {
        val method = _checkoutUiState.value.shippingMethods.find { it.id == methodId }
        if (method != null) {
            val newTotal = _checkoutUiState.value.subtotal +
                    _checkoutUiState.value.tax +
                    method.cost -
                    _checkoutUiState.value.discount

            _checkoutUiState.value = _checkoutUiState.value.copy(
                selectedShippingMethod = methodId,
                shippingCost = method.cost,
                total = newTotal
            )
            Timber.d("Shipping method selected: ${method.name}")
        }
    }

    /**
     * Select payment method.
     */
    fun selectPaymentMethod(methodId: String) {
        _checkoutUiState.value = _checkoutUiState.value.copy(
            selectedPaymentMethod = methodId
        )
        Timber.d("Payment method selected: $methodId")
    }

    /**
     * Apply coupon code.
     */
    fun applyCoupon(code: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Timber.d("Applying coupon: $code")

                // Simulate coupon validation
                // In real app, this would call API
                val discount = when (code.uppercase()) {
                    "SAVE10" -> _checkoutUiState.value.subtotal * 0.1
                    "SAVE20" -> _checkoutUiState.value.subtotal * 0.2
                    else -> {
                        _error.value = "کد تخفیف نامعتبر"
                        return@launch
                    }
                }

                val newTotal = _checkoutUiState.value.subtotal +
                        _checkoutUiState.value.tax +
                        _checkoutUiState.value.shippingCost -
                        discount

                _checkoutUiState.value = _checkoutUiState.value.copy(
                    discount = discount,
                    total = newTotal
                )

                Timber.d("Coupon applied: discount = $discount")
            } catch (e: Exception) {
                Timber.e(e, "Error applying coupon")
                _error.value = "خطا در اعمال کد"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Place order.
     */
    fun placeOrder() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val state = _checkoutUiState.value
                if (state.address == null || state.selectedPaymentMethod == null) {
                    _error.value = "لطفا مالومات را معرفی کنید"
                    return@launch
                }

                Timber.d("Placing order with payment method: ${state.selectedPaymentMethod}")

                // Create order through repository
                val orderId = orderRepository.createOrder(
                    cartItems = state.cartItems,
                    address = state.address,
                    shippingMethod = state.selectedShippingMethod ?: "standard",
                    paymentMethod = state.selectedPaymentMethod,
                    total = state.total
                )

                // Clear cart
                cartRepository.clearCart()

                Timber.d("Order placed successfully: $orderId")
            } catch (e: Exception) {
                Timber.e(e, "Error placing order")
                _error.value = e.localizedMessage ?: "خطا در ثبت سفارش"
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
