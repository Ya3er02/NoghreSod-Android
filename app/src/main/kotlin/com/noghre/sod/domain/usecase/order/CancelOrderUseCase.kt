package com.noghre.sod.domain.usecase.order

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.repository.OrderRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class CancelOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) : UseCase<CancelOrderUseCase.Params, Order>() {

    data class Params(
        val orderId: String,
        val reason: String? = null
    )

    override suspend fun execute(params: Params): Order {
        return orderRepository.cancelOrder(
            orderId = params.orderId,
            reason = params.reason
        )
    }
}
