package com.noghre.sod.domain.usecase.order

import com.noghre.sod.domain.Result
import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.model.Address
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.model.PaymentMethod
import com.noghre.sod.domain.repository.OrderRepository
import javax.inject.Inject

data class CreateOrderParams(
    val shippingAddress: Address,
    val billingAddress: Address? = null,
    val paymentMethod: PaymentMethod,
    val notes: String? = null,
)

class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
) : UseCase<CreateOrderParams, Order>() {

    override suspend fun execute(parameters: CreateOrderParams): Result<Order> {
        return orderRepository.createOrder(
            parameters.shippingAddress,
            parameters.billingAddress,
            parameters.paymentMethod,
            parameters.notes
        )
    }
}
