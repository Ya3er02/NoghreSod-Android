package com.noghre.sod.domain.usecase.order

import com.noghre.sod.core.util.AppError
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.repository.OrderRepository
import com.noghre.sod.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Creates a new order from cart.
 * Part of Order Management domain logic.
 * 
 * Business Rules:
 * - Cart must not be empty
 * - User must be authenticated
 * - All items must be in stock
 */
class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<CreateOrderUseCase.Params, Order>(dispatcher) {
    
    data class Params(
        val cartId: String,
        val shippingAddressId: String,
        val shippingMethodId: String,
        val paymentMethodId: String,
        val insuranceId: String? = null,
        val couponCode: String? = null
    )
    
    override suspend fun execute(params: Params): Order {
        return orderRepository.createOrder(
            cartId = params.cartId,
            shippingAddressId = params.shippingAddressId,
            shippingMethodId = params.shippingMethodId,
            paymentMethodId = params.paymentMethodId,
            insuranceId = params.insuranceId,
            couponCode = params.couponCode
        ).getOrThrow()
    }
}

/**
 * Retrieves all user orders.
 * Part of Order Management domain logic.
 */
class GetOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Unit, List<Order>>(dispatcher) {
    
    override suspend fun execute(params: Unit): List<Order> {
        return orderRepository.getUserOrders()
            .getOrThrow()
    }
}

/**
 * Retrieves a specific order by ID.
 * Part of Order Management domain logic.
 */
class GetOrderByIdUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<String, Order>(dispatcher) {
    
    override suspend fun execute(params: String): Order {
        return orderRepository.getOrderById(params)
            .getOrThrow()
    }
}

/**
 * Cancels an existing order.
 * Part of Order Management domain logic.
 * 
 * Business Rules:
 * - Only pending orders can be cancelled
 * - Order must belong to current user
 * - Cancellation reason is required
 */
class CancelOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<CancelOrderUseCase.Params, Order>(dispatcher) {
    
    data class Params(
        val orderId: String,
        val reason: String
    )
    
    override suspend fun execute(params: Params): Order {
        when {
            params.reason.isBlank() -> throw AppError.Validation(
                message = "لطفا علت باطلی دستور را متذکر کنید",
                fieldName = "reason"
            )
        }
        
        return orderRepository.cancelOrder(params.orderId, params.reason)
            .getOrThrow()
    }
}

/**
 * Gets order tracking information.
 * Part of Order Management domain logic.
 */
class GetOrderTrackingUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<String, OrderTracking>(dispatcher) {
    
    data class OrderTracking(
        val orderId: String,
        val trackingNumber: String,
        val status: String,
        val location: String,
        val expectedDelivery: Long,
        val estimatedDaysLeft: Int
    )
    
    override suspend fun execute(params: String): OrderTracking {
        return orderRepository.getOrderTracking(params)
            .getOrThrow()
    }
}
