package com.noghre.sod.domain.usecase.payment

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.PaymentVerifyResponse
import com.noghre.sod.domain.repository.PaymentRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class VerifyPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) : UseCase<VerifyPaymentUseCase.Params, PaymentVerifyResponse>() {

    data class Params(
        val authority: String,
        val amount: Double,
        val orderId: String
    )

    override suspend fun execute(params: Params): PaymentVerifyResponse {
        return paymentRepository.verifyPayment(
            authority = params.authority,
            amount = params.amount,
            orderId = params.orderId
        )
    }
}
