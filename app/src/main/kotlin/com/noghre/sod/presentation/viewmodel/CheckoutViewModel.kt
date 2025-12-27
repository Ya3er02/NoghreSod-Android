package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Address
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.model.PaymentMethod
import com.noghre.sod.domain.repository.AddressRepository
import com.noghre.sod.domain.repository.CartRepository
import com.noghre.sod.domain.repository.OrderRepository
import com.noghre.sod.domain.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * CheckoutViewModel - مدیریت فرآیند checkout چند مرحله‌ای
 * Features:
 * - Multi-step Checkout Flow
 * - Address Management
 * - Payment Method Selection
 * - Order Processing
 * - Step Navigation
 */
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val paymentRepository: PaymentRepository,
    private val addressRepository: AddressRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    // Checkout State
    private val _checkoutState = MutableStateFlow<CheckoutState>(
        CheckoutState.AddressSelection(emptyList(), null)
    )
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()

    // Current Step
    private val _currentStep = MutableStateFlow(CheckoutStep.ADDRESS)
    val currentStep: StateFlow<CheckoutStep> = _currentStep.asStateFlow()

    // Selected Address
    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress: StateFlow<Address?> = _selectedAddress.asStateFlow()

    // Selected Payment Method
    private val _selectedPaymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val selectedPaymentMethod: StateFlow<PaymentMethod?> = _selectedPaymentMethod.asStateFlow()

    init {
        loadAddresses()
        loadPaymentMethods()
    }

    /**
     * Load user addresses
     */
    private fun loadAddresses() {
        viewModelScope.launch {
            addressRepository.getUserAddresses()
                .onSuccess { addresses ->
                    _checkoutState.value = CheckoutState.AddressSelection(
                        addresses = addresses,
                        selectedAddress = addresses.firstOrNull()
                    )
                }
                .onFailure { error ->
                    _checkoutState.value = CheckoutState.Error(
                        message = error.message ?: "Failed to load addresses"
                    )
                }
        }
    }

    /**
     * Load available payment methods
     */
    private fun loadPaymentMethods() {
        viewModelScope.launch {
            paymentRepository.getPaymentMethods()
                .onSuccess { methods ->
                    // Store for later use
                }
        }
    }

    /**
     * Select shipping address
     */
    fun selectAddress(address: Address) {
        _selectedAddress.value = address
        val currentState = _checkoutState.value as? CheckoutState.AddressSelection
        if (currentState != null) {
            _checkoutState.value = currentState.copy(selectedAddress = address)
        }
    }

    /**
     * Select payment method
     */
    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    /**
     * Proceed to next checkout step
     */
    fun proceedToNextStep() {
        val nextStep = when (_currentStep.value) {
            CheckoutStep.ADDRESS -> {
                if (_selectedAddress.value == null) {
                    _checkoutState.value = CheckoutState.Error(
                        message = "Please select an address"
                    )
                    return
                }
                CheckoutStep.PAYMENT
            }
            CheckoutStep.PAYMENT -> {
                if (_selectedPaymentMethod.value == null) {
                    _checkoutState.value = CheckoutState.Error(
                        message = "Please select a payment method"
                    )
                    return
                }
                CheckoutStep.REVIEW
            }
            CheckoutStep.REVIEW -> {
                placeOrder()
                return
            }
        }
        _currentStep.value = nextStep
    }

    /**
     * Go back to previous step
     */
    fun goBack() {
        val prevStep = when (_currentStep.value) {
            CheckoutStep.ADDRESS -> return // Already at first step
            CheckoutStep.PAYMENT -> CheckoutStep.ADDRESS
            CheckoutStep.REVIEW -> CheckoutStep.PAYMENT
        }
        _currentStep.value = prevStep
    }

    /**
     * Place order and complete checkout
     */
    private fun placeOrder() {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Processing

            val selectedAddress = _selectedAddress.value
            val selectedPayment = _selectedPaymentMethod.value

            if (selectedAddress == null || selectedPayment == null) {
                _checkoutState.value = CheckoutState.Error(
                    message = "Missing required information"
                )
                return@launch
            }

            val cartId = cartRepository.getCurrentCartId()

            orderRepository.createOrder(
                cartId = cartId,
                shippingAddressId = selectedAddress.id,
                paymentMethod = selectedPayment
            )
                .onSuccess { order ->
                    _checkoutState.value = CheckoutState.Success(order)
                    cartRepository.clearCart()
                }
                .onFailure { error ->
                    _checkoutState.value = CheckoutState.Error(
                        message = error.message ?: "Failed to place order"
                    )
                }
        }
    }

    /**
     * Cancel checkout and return to cart
     */
    fun cancelCheckout() {
        _currentStep.value = CheckoutStep.ADDRESS
        _selectedAddress.value = null
        _selectedPaymentMethod.value = null
    }
}

// Checkout State
sealed interface CheckoutState {
    data class AddressSelection(
        val addresses: List<Address>,
        val selectedAddress: Address?
    ) : CheckoutState

    data class PaymentSelection(
        val methods: List<PaymentMethod>,
        val selectedMethod: PaymentMethod?
    ) : CheckoutState

    data class Review(
        val address: Address,
        val paymentMethod: PaymentMethod
    ) : CheckoutState

    data object Processing : CheckoutState
    data class Success(val order: Order) : CheckoutState
    data class Error(val message: String) : CheckoutState
}

// Checkout Steps
enum class CheckoutStep {
    ADDRESS, PAYMENT, REVIEW
}
