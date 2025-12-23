package com.noghre.sod.data.repository

import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.dto.CreateOrderRequestDto
import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.*
import com.noghre.sod.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : OrderRepository {

    override suspend fun createOrder(
        shippingAddress: Address,
        billingAddress: Address?,
        paymentMethod: PaymentMethod,
        notes: String?,
    ): Result<Order> {
        return try {
            val request = CreateOrderRequestDto(
                shippingAddressId = shippingAddress.id ?: "",
                billingAddressId = billingAddress?.id,
                paymentMethod = paymentMethod.name,
                notes = notes
            )
            val response = apiService.createOrder(request)
            if (response.success && response.data != null) {
                Result.Success(response.data.toOrder())
            } else {
                Result.Error(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getOrderById(orderId: String): Flow<Result<Order>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getOrderById(orderId)
            if (response.success && response.data != null) {
                emit(Result.Success(response.data.toOrder()))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun getUserOrders(page: Int, pageSize: Int): Flow<Result<List<OrderSummary>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getUserOrders(page, pageSize)
            if (response.success && response.data != null) {
                val orders = response.data.items.map { it.toOrderSummary() }
                emit(Result.Success(orders))
            } else {
                emit(Result.Error(Exception(response.message ?: "Unknown error")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun cancelOrder(orderId: String, reason: String?): Result<Order> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun requestReturn(
        orderId: String,
        items: List<String>,
        reason: String,
    ): Result<ReturnRequest> {
        return try {
            Result.Error(Exception("Not yet implemented"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Mapper functions
    private fun com.noghre.sod.data.remote.dto.OrderDto.toOrder(): Order {
        return Order(
            id = id,
            orderNumber = orderNumber,
            items = items.map { OrderItem(it.id, it.productId, it.quantity, it.price) },
            totalPrice = total,
            status = OrderStatus.valueOf(status.uppercase()),
            paymentStatus = PaymentStatus.valueOf(paymentStatus.uppercase()),
            createdAt = createdAt,
            estimatedDeliveryDate = estimatedDelivery,
        )
    }

    private fun com.noghre.sod.data.remote.dto.OrderDto.toOrderSummary(): OrderSummary {
        return OrderSummary(
            id = id,
            orderNumber = orderNumber,
            totalPrice = total,
            status = OrderStatus.valueOf(status.uppercase()),
            createdAt = createdAt,
        )
    }
}
