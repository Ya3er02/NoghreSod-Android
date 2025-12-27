package com.noghre.sod.data.repository

import com.noghre.sod.core.error.*
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.remote.api.ApiService
import com.noghre.sod.data.remote.dto.CreateOrderRequestDto
import com.noghre.sod.domain.model.*
import com.noghre.sod.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * 📋 Order Repository Implementation
 * 
 * Handles order operations with comprehensive error handling.
 * All operations return Result<T> with proper error classification.
 */
class OrderRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val exceptionHandler: GlobalExceptionHandler
) : OrderRepository {

    /**
     * ➕ Create a new order
     */
    override suspend fun createOrder(
        shippingAddress: Address,
        billingAddress: Address?,
        paymentMethod: PaymentMethod,
        notes: String?,
    ): Result<Order> {
        return try {
            Timber.d("[ORDER] Creating order for address: ${shippingAddress.title}")
            
            // Validate inputs
            if (shippingAddress.id.isNullOrBlank()) {
                Timber.w("[ORDER] Invalid shipping address")
                return Result.Error(AppError.Validation(
                    message = "آدرس تحویل انتخاب نشده است",
                    field = "shippingAddress"
                ))
            }
            
            val request = CreateOrderRequestDto(
                shippingAddressId = shippingAddress.id,
                billingAddressId = billingAddress?.id,
                paymentMethod = paymentMethod.name,
                notes = notes
            )
            
            val response = apiService.createOrder(request)
            
            if (response.isSuccessful) {
                if (response.data != null) {
                    Timber.d("[ORDER] Order created successfully: ${response.data.orderNumber}")
                    Result.Success(response.data.toOrder())
                } else {
                    Timber.w("[ORDER] Order creation response is empty")
                    Result.Error(AppError.Network(
                        message = "پاسخ سرور نامعتبر است",
                        statusCode = 200
                    ))
                }
            } else {
                Timber.w("[ORDER] Order creation failed: ${response.code()}")
                Result.Error(when (response.code()) {
                    400 -> AppError.Validation(
                        message = "اطلاعات سفارش نامعتبر است",
                        field = "order"
                    )
                    402 -> AppError.Network(
                        message = "موجودی کافی نیست",
                        statusCode = 402
                    )
                    else -> AppError.Network(
                        message = response.message ?: "ایجاد سفارش ناموفق",
                        statusCode = response.code()
                    )
                })
            }
        } catch (e: Exception) {
            Timber.e(e, "[ORDER] Create order error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🔍 Get order by ID
     */
    override fun getOrderById(orderId: String): Flow<Result<Order>> = flow {
        try {
            emit(Result.Loading)
            Timber.d("[ORDER] Loading order: $orderId")
            
            if (orderId.isBlank()) {
                Timber.w("[ORDER] Invalid order ID")
                emit(Result.Error(AppError.Validation(
                    message = "شناسه سفارش نامعتبر است",
                    field = "orderId"
                )))
                return@flow
            }
            
            val response = apiService.getOrderById(orderId)
            
            if (response.isSuccessful) {
                if (response.data != null) {
                    Timber.d("[ORDER] Order loaded: ${response.data.orderNumber}")
                    emit(Result.Success(response.data.toOrder()))
                } else {
                    Timber.w("[ORDER] Order response is empty")
                    emit(Result.Error(AppError.Network(
                        message = "سفارش یافت نشد",
                        statusCode = 200
                    )))
                }
            } else {
                Timber.w("[ORDER] Get order failed: ${response.code()}")
                emit(Result.Error(when (response.code()) {
                    404 -> AppError.Network(
                        message = "سفارش یافت نشد",
                        statusCode = 404
                    )
                    else -> AppError.Network(
                        message = response.message ?: "بارگیری سفارش ناموفق",
                        statusCode = response.code()
                    )
                }))
            }
        } catch (e: Exception) {
            Timber.e(e, "[ORDER] Get order error")
            emit(Result.Error(exceptionHandler.handleException(e)))
        }
    }

    /**
     * 📦 Get user's orders with pagination
     */
    override fun getUserOrders(page: Int, pageSize: Int): Flow<Result<List<OrderSummary>>> = flow {
        try {
            emit(Result.Loading)
            Timber.d("[ORDER] Loading orders: page=$page, size=$pageSize")
            
            // Validate pagination params
            if (page < 1) {
                Timber.w("[ORDER] Invalid page number")
                emit(Result.Error(AppError.Validation(
                    message = "صفحه باید بزرگتر از صفر باشد",
                    field = "page"
                )))
                return@flow
            }
            
            if (pageSize < 1) {
                Timber.w("[ORDER] Invalid page size")
                emit(Result.Error(AppError.Validation(
                    message = "اندازه صفحه باید بزرگتر از صفر باشد",
                    field = "pageSize"
                )))
                return@flow
            }
            
            val response = apiService.getUserOrders(page, pageSize)
            
            if (response.isSuccessful) {
                if (response.data != null) {
                    val orders = response.data.items.map { it.toOrderSummary() }
                    Timber.d("[ORDER] Orders loaded: ${orders.size} items")
                    
                    if (orders.isEmpty()) {
                        emit(Result.Success(emptyList()))
                    } else {
                        emit(Result.Success(orders))
                    }
                } else {
                    Timber.w("[ORDER] Orders response is empty")
                    emit(Result.Success(emptyList()))
                }
            } else {
                Timber.w("[ORDER] Get orders failed: ${response.code()}")
                emit(Result.Error(AppError.Network(
                    message = response.message ?: "بارگیری سفارش‌ها ناموفق",
                    statusCode = response.code()
                )))
            }
        } catch (e: Exception) {
            Timber.e(e, "[ORDER] Get user orders error")
            emit(Result.Error(exceptionHandler.handleException(e)))
        }
    }

    /**
     * ❌ Cancel an order
     */
    override suspend fun cancelOrder(orderId: String, reason: String?): Result<Order> {
        return try {
            Timber.d("[ORDER] Cancelling order: $orderId")
            
            if (orderId.isBlank()) {
                Timber.w("[ORDER] Invalid order ID for cancel")
                return Result.Error(AppError.Validation(
                    message = "شناسه سفارش نامعتبر است",
                    field = "orderId"
                ))
            }
            
            // TODO: Implement when API is ready
            Result.Error(AppError.Unknown(
                message = "امکان‌سازی این بخش برنامه انجام نشده"
            ))
        } catch (e: Exception) {
            Timber.e(e, "[ORDER] Cancel order error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    /**
     * 🔄 Request return for order items
     */
    override suspend fun requestReturn(
        orderId: String,
        items: List<String>,
        reason: String,
    ): Result<ReturnRequest> {
        return try {
            Timber.d("[ORDER] Requesting return for order: $orderId, items: ${items.size}")
            
            // Validate inputs
            if (orderId.isBlank()) {
                Timber.w("[ORDER] Invalid order ID for return")
                return Result.Error(AppError.Validation(
                    message = "شناسه سفارش نامعتبر است",
                    field = "orderId"
                ))
            }
            
            if (items.isEmpty()) {
                Timber.w("[ORDER] No items selected for return")
                return Result.Error(AppError.Validation(
                    message = "حداقل یک محصول انتخاب کنید",
                    field = "items"
                ))
            }
            
            if (reason.isBlank()) {
                Timber.w("[ORDER] Return reason not provided")
                return Result.Error(AppError.Validation(
                    message = "دلیل بازگرداندن الزامی است",
                    field = "reason"
                ))
            }
            
            // TODO: Implement when API is ready
            Result.Error(AppError.Unknown(
                message = "امکان‌سازی این بخش برنامه انجام نشده"
            ))
        } catch (e: Exception) {
            Timber.e(e, "[ORDER] Request return error")
            Result.Error(exceptionHandler.handleException(e))
        }
    }

    // ============================================
    // 🔄 Mapper Functions
    // ============================================

    private fun com.noghre.sod.data.remote.dto.OrderDto.toOrder(): Order {
        return Order(
            id = id,
            orderNumber = orderNumber,
            items = items.map { OrderItem(it.id, it.productId, it.quantity, it.price) },
            totalPrice = total,
            status = try {
                OrderStatus.valueOf(status.uppercase())
            } catch (e: IllegalArgumentException) {
                Timber.w("[ORDER] Unknown order status: $status")
                OrderStatus.PENDING
            },
            paymentStatus = try {
                PaymentStatus.valueOf(paymentStatus.uppercase())
            } catch (e: IllegalArgumentException) {
                Timber.w("[ORDER] Unknown payment status: $paymentStatus")
                PaymentStatus.PENDING
            },
            createdAt = createdAt,
            estimatedDeliveryDate = estimatedDelivery,
        )
    }

    private fun com.noghre.sod.data.remote.dto.OrderDto.toOrderSummary(): OrderSummary {
        return OrderSummary(
            id = id,
            orderNumber = orderNumber,
            totalPrice = total,
            status = try {
                OrderStatus.valueOf(status.uppercase())
            } catch (e: IllegalArgumentException) {
                Timber.w("[ORDER] Unknown status for summary: $status")
                OrderStatus.PENDING
            },
            createdAt = createdAt,
        )
    }
}