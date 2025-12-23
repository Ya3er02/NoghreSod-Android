package com.noghre.sod.domain.usecase.payment

import com.noghre.sod.domain.base.UseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

data class PaymentVerifyResult(
    val success: Boolean,
    val refId: String? = null,
    val cardPan: String? = null,
    val message: String? = null
)

@ViewModelScoped
class VerifyPaymentUseCase @Inject constructor() : UseCase<VerifyPaymentUseCase.Params, PaymentVerifyResult>() {

    data class Params(
        val authority: String,
        val amount: Double,
        val orderId: String
    )

    override suspend fun execute(params: Params): PaymentVerifyResult {
        return PaymentVerifyResult(
            success = false,
            message = "حواله نامعتبر"
        )
    }
}
