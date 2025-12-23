package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Address
import com.noghre.sod.domain.usecase.GetAddressesUseCase
import com.noghre.sod.domain.usecase.CreateOrderUseCase
import com.noghre.sod.domain.usecase.ProcessPaymentUseCase
import com.noghre.sod.presentation.checkout.CheckoutStep
import com.noghre.sod.presentation.checkout.CheckoutUiState
import com.noghre.sod.presentation.checkout.PaymentMethod
import com.noghre.sod.presentation.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for multi-step checkout flow.
 */
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val getAddressesUseCase: GetAddressesUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val processPaymentUseCase: ProcessPaymentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadAddresses()
    }

    /**
     * Load user addresses
     */
    fun loadAddresses() {
        viewModelScope.launch {
            try {
                val addresses = getAddressesUseCase()
                updateState { copy(addresses = addresses) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading addresses")
            }
        }
    }

    /**
     * Select delivery address
     *
     * @param address Selected address
     */
    fun selectAddress(address: Address) {
        updateState { copy(selectedAddress = address) }
    }

    /**
     * Select payment method
     *
     * @param method Payment method
     */
    fun selectPaymentMethod(method: PaymentMethod) {
        updateState { copy(paymentMethod = method) }
    }

    /**
     * Accept/reject terms
     *
     * @param accepted Terms accepted
     */
    fun setTermsAccepted(accepted: Boolean) {
        updateState { copy(termsAccepted = accepted) }
    }

    /**
     * Move to next step with validation
     */
    fun nextStep() {
        val currentStep = uiState.value.currentStep
        if (validateStep(currentStep)) {
            val nextStep = when (currentStep) {
                CheckoutStep.ADDRESS -> CheckoutStep.PAYMENT
                CheckoutStep.PAYMENT -> CheckoutStep.REVIEW
                CheckoutStep.REVIEW -> CheckoutStep.CONFIRMATION
                CheckoutStep.CONFIRMATION -> CheckoutStep.CONFIRMATION
            }
            updateState { copy(currentStep = nextStep) }
        } else {
            viewModelScope.launch {
                _events.send(UiEvent.ShowSnackbar("Please complete this step"))
            }
        }
    }

    /**
     * Move to previous step
     */
    fun previousStep() {
        val currentStep = uiState.value.currentStep
        val previousStep = when (currentStep) {
            CheckoutStep.ADDRESS -> CheckoutStep.ADDRESS
            CheckoutStep.PAYMENT -> CheckoutStep.ADDRESS
            CheckoutStep.REVIEW -> CheckoutStep.PAYMENT
            CheckoutStep.CONFIRMATION -> CheckoutStep.REVIEW
        }
        updateState { copy(currentStep = previousStep) }
    }

    /**
     * Place order
     */
    fun placeOrder() {
        val state = uiState.value

        if (!validateStep(CheckoutStep.REVIEW)) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowSnackbar("Invalid order"))
            }
            return
        }

        viewModelScope.launch {
            try {
                updateState { copy(isProcessing = true) }

                // Process payment if online
                if (state.paymentMethod == PaymentMethod.ONLINE) {
                    processPaymentUseCase(
                        amount = state.cartItems.sumOf { it.subtotal },
                        method = state.paymentMethod.name
                    )
                }

                // Create order
                createOrderUseCase(
                    items = state.cartItems,
                    address = state.selectedAddress ?: return@launch,
                    paymentMethod = state.paymentMethod.name
                )

                updateState {
                    copy(
                        isProcessing = false,
                        currentStep = CheckoutStep.CONFIRMATION
                    )
                }
                _events.send(UiEvent.ShowSnackbar("Order placed successfully"))
                _events.send(UiEvent.Navigate("order_confirmation"))
            } catch (e: Exception) {
                Timber.e(e, "Error placing order")
                updateState {
                    copy(
                        isProcessing = false,
                        error = e.message ?: "Failed to place order"
                    )
                }
                _events.send(UiEvent.ShowSnackbar("Failed to place order"))
            }
        }
    }

    /**
     * Validate current step
     *
     * @param step Step to validate
     * @return True if valid
     */
    private fun validateStep(step: CheckoutStep): Boolean {
        val state = uiState.value
        return when (step) {
            CheckoutStep.ADDRESS -> state.selectedAddress != null
            CheckoutStep.PAYMENT -> state.paymentMethod != null
            CheckoutStep.REVIEW -> state.termsAccepted
            CheckoutStep.CONFIRMATION -> true
        }
    }

    private fun updateState(block: CheckoutUiState.() -> CheckoutUiState) {
        _uiState.update(block)
    }
}
