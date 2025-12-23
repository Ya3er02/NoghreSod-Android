package com.noghre.sod.presentation.checkout

import com.noghre.sod.domain.model.Address
import com.noghre.sod.presentation.cart.CartItemUi

/**
 * UI state for checkout flow
 */
data class CheckoutUiState(
    val cartItems: List<CartItemUi> = emptyList(),
    val selectedAddress: Address? = null,
    val addresses: List<Address> = emptyList(),
    val paymentMethod: PaymentMethod = PaymentMethod.ONLINE,
    val isProcessing: Boolean = false,
    val error: String? = null,
    val currentStep: CheckoutStep = CheckoutStep.ADDRESS,
    val termsAccepted: Boolean = false
)

/**
 * Checkout step in multi-step flow
 */
enum class CheckoutStep {
    ADDRESS,
    PAYMENT,
    REVIEW,
    CONFIRMATION
}

/**
 * Payment methods
 */
enum class PaymentMethod {
    ONLINE,
    CARD_TO_CARD,
    CASH_ON_DELIVERY
}
