package com.noghre.sod.domain.usecase.order

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.OrderTrackingInfo
import com.noghre.sod.domain.repository.OrderRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class TrackOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) : UseCase<TrackOrderUseCase.Params, OrderTrackingInfo>() {

    data class Params(val orderId: String)

    override suspend fun execute(params: Params): OrderTrackingInfo {
        return orderRepository.trackOrder(params.orderId)
    }
}
