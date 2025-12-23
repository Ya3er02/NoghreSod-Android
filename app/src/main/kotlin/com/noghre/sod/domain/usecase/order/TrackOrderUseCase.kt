package com.noghre.sod.domain.usecase.order

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.OrderStatusHistory
import com.noghre.sod.domain.repository.OrderRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class TrackOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) : UseCase<TrackOrderUseCase.Params, List<OrderStatusHistory>>() {

    data class Params(val orderId: String)

    override suspend fun execute(params: Params): List<OrderStatusHistory> {
        if (params.orderId.isEmpty()) {
            throw IllegalArgumentException("شناسه سفارش نامعتبر است")
        }
        return orderRepository.getOrderTracking(params.orderId)
    }
}
