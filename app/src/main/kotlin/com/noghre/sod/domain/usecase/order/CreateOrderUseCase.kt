package com.noghre.sod.domain.usecase.order

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.repository.OrderRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) : UseCase<CreateOrderUseCase.Params, Order>() {

    data class Params(
        val addressId: String,
        val paymentMethod: String,
        val notes: String? = null
    )

    override suspend fun execute(params: Params): Order {
        return orderRepository.createOrder(
            addressId = params.addressId,
            paymentMethod = params.paymentMethod,
            notes = params.notes
        )
    }
}
