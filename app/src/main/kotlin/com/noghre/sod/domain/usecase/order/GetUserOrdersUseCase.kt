package com.noghre.sod.domain.usecase.order

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.base.FlowUseCase
import com.noghre.sod.domain.model.OrderSummary
import com.noghre.sod.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class PaginationParams(
    val page: Int = 0,
    val pageSize: Int = 20,
)

class GetUserOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
) : FlowUseCase<PaginationParams, List<OrderSummary>>() {

    override fun execute(parameters: PaginationParams): Flow<Result<List<OrderSummary>>> {
        return orderRepository.getUserOrders(parameters.page, parameters.pageSize)
    }
}
