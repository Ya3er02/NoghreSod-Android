package com.noghre.sod.domain.usecase.order

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.PaymentMethod
import com.noghre.sod.domain.repository.OrderRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

data class CreateOrderResult(
    val success: Boolean,
    val orderId: String? = null,
    val message: String? = null
)

@ViewModelScoped
class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) : UseCase<CreateOrderUseCase.Params, CreateOrderResult>() {

    data class Params(
        val addressId: String,
        val paymentMethod: PaymentMethod,
        val notes: String? = null
    )

    override suspend fun execute(params: Params): CreateOrderResult {
        if (params.addressId.isEmpty()) {
            return CreateOrderResult(false, message = "لطفا آدرس تحویل را انتخاب کنید")
        }
        return orderRepository.createOrder(params.addressId, params.paymentMethod, params.notes)
    }
}
