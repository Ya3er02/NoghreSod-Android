package com.noghre.sod.presentation.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noghre.sod.domain.usecase.order.CreateOrderUseCase
import com.noghre.sod.domain.usecase.payment.InitiatePaymentUseCase
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CheckoutViewModelTest {

    private val createOrderUseCase: CreateOrderUseCase = mockk()
    private val initiatePaymentUseCase: InitiatePaymentUseCase = mockk()

    @Test
    fun `checkout with valid address creates order`() {
        // TODO: Implement
    }

    @Test
    fun `invalid address shows error`() {
        // TODO: Implement
    }
}
