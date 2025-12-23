package com.noghre.sod.domain.usecase.payment

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.PaymentRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

data class PaymentInitResult(
    val success: Boolean,
    val authority: String? = null,
    val paymentUrl: String? = null,
    val message: String? = null
)

@ViewModelScoped
class InitiatePaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) : UseCase<InitiatePaymentUseCase.Params, PaymentInitResult>() {

    data class Params(
        val orderId: String,
        val amount: Double
    )

    override suspend fun execute(params: Params): PaymentInitResult {
        if (params.amount <= 0) {
            throw IllegalArgumentException("مبلغ باید بیشتر از صفر باشد")
        }
        return paymentRepository.initiatePayment(params.orderId, params.amount)
    }
}
