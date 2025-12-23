package com.noghre.sod.domain.usecase.payment

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.PaymentRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

data class PaymentVerifyResult(
    val success: Boolean,
    val refId: String? = null,
    val message: String? = null
)

@ViewModelScoped
class VerifyPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) : UseCase<VerifyPaymentUseCase.Params, PaymentVerifyResult>() {

    data class Params(
        val authority: String,
        val amount: Double,
        val orderId: String
    )

    override suspend fun execute(params: Params): PaymentVerifyResult {
        return paymentRepository.verifyPayment(params.authority, params.amount, params.orderId)
    }
}
