package com.noghre.sod.domain.usecase.payment

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.PaymentInitResponse
import com.noghre.sod.domain.repository.PaymentRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class InitiatePaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) : UseCase<InitiatePaymentUseCase.Params, PaymentInitResponse>() {

    data class Params(
        val orderId: String,
        val amount: Double
    )

    override suspend fun execute(params: Params): PaymentInitResponse {
        if (params.amount <= 0) {
            throw IllegalArgumentException("مبلغ باید بیشتر از 0 باشد")
        }
        
        return paymentRepository.initiatePayment(params.orderId, params.amount)
    }
}
