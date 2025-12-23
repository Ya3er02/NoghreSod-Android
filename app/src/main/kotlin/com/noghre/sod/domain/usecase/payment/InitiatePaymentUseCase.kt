package com.noghre.sod.domain.usecase.payment

import com.noghre.sod.domain.base.UseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

data class PaymentInitResult(
    val success: Boolean,
    val paymentUrl: String? = null,
    val authority: String? = null,
    val message: String? = null
)

@ViewModelScoped
class InitiatePaymentUseCase @Inject constructor() : UseCase<InitiatePaymentUseCase.Params, PaymentInitResult>() {

    data class Params(
        val orderId: String,
        val amount: Double
    )

    override suspend fun execute(params: Params): PaymentInitResult {
        return PaymentInitResult(
            success = false,
            message = "ابلاغ نامعتبر"
        )
    }
}
