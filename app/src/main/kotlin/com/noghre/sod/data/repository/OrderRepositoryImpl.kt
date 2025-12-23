package com.noghre.sod.data.repository

import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.api.request.CreateOrderRequest
import com.noghre.sod.data.remote.exception.ApiException
import com.noghre.sod.data.remote.network.NetworkMonitor
import com.noghre.sod.domain.Result
import timber.log.Timber
import javax.inject.Inject

/**
 * Order Repository Implementation.
 */
class OrderRepositoryImpl @Inject constructor(
    private val api: NoghreSodApiService,
    private val networkMonitor: NetworkMonitor
) {

    /**
     * Get user's orders.
     */
    suspend fun getOrders(page: Int = 1): Result<Unit> = try {
        if (!networkMonitor.isNetworkAvailable()) {
            return Result.Error("No internet connection")
        }

        val response = api.getOrders(page = page)
        if (response.success) {
            Result.Success(Unit)
        } else {
            Result.Error(response.message ?: "Failed to fetch orders")
        }
    } catch (e: ApiException) {
        Timber.e(e, "API error fetching orders")
        Result.Error(e.message ?: "Network error")
    } catch (e: Exception) {
        Timber.e(e, "Error fetching orders")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Get order by ID.
     */
    suspend fun getOrderById(id: String): Result<Unit> = try {
        if (!networkMonitor.isNetworkAvailable()) {
            return Result.Error("No internet connection")
        }

        val response = api.getOrderById(id)
        if (response.success) {
            Result.Success(Unit)
        } else {
            Result.Error(response.message ?: "Order not found")
        }
    } catch (e: Exception) {
        Timber.e(e, "Error getting order: $id")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Create new order.
     */
    suspend fun createOrder(
        addressId: String,
        paymentMethod: String,
        cartItems: List<CartItemForOrder>
    ): Result<Unit> = try {
        if (!networkMonitor.isNetworkAvailable()) {
            return Result.Error("No internet connection")
        }

        val request = CreateOrderRequest(
            addressId = addressId,
            paymentMethod = paymentMethod,
            cartItems = cartItems.map { com.noghre.sod.data.remote.api.request.CartItemForOrder(
                productId = it.productId,
                quantity = it.quantity,
                priceAtTime = it.priceAtTime
            ) }
        )
        val response = api.createOrder(request)
        if (response.success) {
            Result.Success(Unit)
        } else {
            Result.Error(response.message ?: "Failed to create order")
        }
    } catch (e: ApiException) {
        Timber.e(e, "API error creating order")
        Result.Error(e.message ?: "Network error")
    } catch (e: Exception) {
        Timber.e(e, "Error creating order")
        Result.Error(e.message ?: "Unknown error")
    }

    /**
     * Cancel order.
     */
    suspend fun cancelOrder(id: String): Result<Unit> = try {
        if (!networkMonitor.isNetworkAvailable()) {
            return Result.Error("No internet connection")
        }

        val response = api.cancelOrder(id)
        if (response.success) {
            Result.Success(Unit)
        } else {
            Result.Error(response.message ?: "Failed to cancel order")
        }
    } catch (e: Exception) {
        Timber.e(e, "Error cancelling order: $id")
        Result.Error(e.message ?: "Unknown error")
    }
}

/**
 * Cart item for order creation.
 */
data class CartItemForOrder(
    val productId: String,
    val quantity: Int,
    val priceAtTime: Double
)
