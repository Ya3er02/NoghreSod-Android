package com.noghre.sod.domain.usecase.order

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.repository.OrderRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) : UseCase<GetOrdersUseCase.Params, List<Order>>() {

    data class Params(
        val page: Int = 1,
        val status: String? = null
    )

    override suspend fun execute(params: Params): List<Order> {
        return orderRepository.getOrders(page = params.page, status = params.status)
    }
}
