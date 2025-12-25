package com.noghre.sod.data.repository

import com.noghre.sod.domain.model.*
import com.noghre.sod.data.remote.api.NoghreSodApi
import com.noghre.sod.data.local.dao.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

// ============== CART REPOSITORY ==============

interface ICartRepository {
    suspend fun getCart(userId: String): Flow<Result<Cart>>
    suspend fun addToCart(cartId: String, product: Product, quantity: Int): Flow<Result<Cart>>
    suspend fun updateCartItem(itemId: String, quantity: Int): Flow<Result<Cart>>
    suspend fun removeFromCart(itemId: String): Flow<Result<Cart>>
    suspend fun clearCart(cartId: String): Flow<Result<Unit>>
}

class CartRepository @Inject constructor(
    private val api: NoghreSodApi,
    private val cartDao: CartDao,
    private val productDao: ProductDao
) : ICartRepository {

    override suspend fun getCart(userId: String): Flow<Result<Cart>> = flow {
        try {
            val response = api.getCart()
            if (response.isSuccessful && response.body()?.success == true) {
                val cart = response.body()?.data?.toDomain()
                if (cart != null) {
                    emit(Result.success(cart))
                } else {
                    emit(Result.failure(Exception("Cart not found")))
                }
            } else {
                emit(Result.failure(Exception("Failed to get cart")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting cart")
            emit(Result.failure(e))
        }
    }

    override suspend fun addToCart(cartId: String, product: Product, quantity: Int): Flow<Result<Cart>> = flow {
        try {
            val response = api.addToCart(mapOf(
                "cartId" to cartId,
                "productId" to product.id,
                "quantity" to quantity
            ))
            if (response.isSuccessful && response.body()?.success == true) {
                val cart = response.body()?.data?.toDomain()
                if (cart != null) {
                    emit(Result.success(cart))
                } else {
                    emit(Result.failure(Exception("Failed to add item")))
                }
            } else {
                emit(Result.failure(Exception("Add to cart failed")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error adding to cart")
            emit(Result.failure(e))
        }
    }

    override suspend fun updateCartItem(itemId: String, quantity: Int): Flow<Result<Cart>> = flow {
        try {
            val response = api.updateCartItem(itemId, mapOf("quantity" to quantity))
            if (response.isSuccessful && response.body()?.success == true) {
                val cart = response.body()?.data?.toDomain()
                if (cart != null) {
                    emit(Result.success(cart))
                } else {
                    emit(Result.failure(Exception("Failed to update item")))
                }
            } else {
                emit(Result.failure(Exception("Update failed")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating cart item")
            emit(Result.failure(e))
        }
    }

    override suspend fun removeFromCart(itemId: String): Flow<Result<Cart>> = flow {
        try {
            val response = api.removeFromCart(itemId)
            if (response.isSuccessful && response.body()?.success == true) {
                val cart = response.body()?.data?.toDomain()
                if (cart != null) {
                    emit(Result.success(cart))
                } else {
                    emit(Result.failure(Exception("Failed to remove item")))
                }
            } else {
                emit(Result.failure(Exception("Remove failed")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error removing from cart")
            emit(Result.failure(e))
        }
    }

    override suspend fun clearCart(cartId: String): Flow<Result<Unit>> = flow {
        try {
            val response = api.clearCart()
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Clear failed")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error clearing cart")
            emit(Result.failure(e))
        }
    }
}

// ============== ORDER REPOSITORY ==============

interface IOrderRepository {
    suspend fun getOrders(userId: String): Flow<Result<List<Order>>>
    suspend fun getOrderDetail(orderId: String): Flow<Result<Order>>
    suspend fun createOrder(order: Order): Flow<Result<Order>>
    suspend fun getOrderTracking(orderId: String): Flow<Result<Order>>
}

class OrderRepository @Inject constructor(
    private val api: NoghreSodApi
) : IOrderRepository {

    override suspend fun getOrders(userId: String): Flow<Result<List<Order>>> = flow {
        try {
            val response = api.getOrders()
            if (response.isSuccessful && response.body()?.success == true) {
                val orders = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                emit(Result.success(orders))
            } else {
                emit(Result.failure(Exception("Failed to get orders")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting orders")
            emit(Result.failure(e))
        }
    }

    override suspend fun getOrderDetail(orderId: String): Flow<Result<Order>> = flow {
        try {
            val response = api.getOrderDetail(orderId)
            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()?.data?.toDomain()
                if (order != null) {
                    emit(Result.success(order))
                } else {
                    emit(Result.failure(Exception("Order not found")))
                }
            } else {
                emit(Result.failure(Exception("Failed to get order")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting order detail")
            emit(Result.failure(e))
        }
    }

    override suspend fun createOrder(order: Order): Flow<Result<Order>> = flow {
        try {
            val response = api.createOrder(mapOf(
                "items" to order.items,
                "shippingAddress" to order.shippingAddress,
                "paymentMethod" to order.paymentMethod
            ))
            if (response.isSuccessful && response.body()?.success == true) {
                val createdOrder = response.body()?.data?.toDomain()
                if (createdOrder != null) {
                    emit(Result.success(createdOrder))
                } else {
                    emit(Result.failure(Exception("Failed to create order")))
                }
            } else {
                emit(Result.failure(Exception("Order creation failed")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error creating order")
            emit(Result.failure(e))
        }
    }

    override suspend fun getOrderTracking(orderId: String): Flow<Result<Order>> = flow {
        try {
            val response = api.getOrderTracking(orderId)
            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()?.data?.toDomain()
                if (order != null) {
                    emit(Result.success(order))
                } else {
                    emit(Result.failure(Exception("Tracking not available")))
                }
            } else {
                emit(Result.failure(Exception("Failed to get tracking")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting tracking")
            emit(Result.failure(e))
        }
    }
}

// ============== USER REPOSITORY ==============

interface IUserRepository {
    suspend fun getUserProfile(): Flow<Result<User>>
    suspend fun updateUserProfile(firstName: String, lastName: String): Flow<Result<User>>
    suspend fun addAddress(address: Address): Flow<Result<User>>
    suspend fun updateAddress(address: Address): Flow<Result<User>>
    suspend fun deleteAddress(addressId: String): Flow<Result<Unit>>
}

class UserRepository @Inject constructor(
    private val api: NoghreSodApi
) : IUserRepository {

    override suspend fun getUserProfile(): Flow<Result<User>> = flow {
        try {
            val response = api.getUserProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()?.data?.toDomain()
                if (user != null) {
                    emit(Result.success(user))
                } else {
                    emit(Result.failure(Exception("Profile not found")))
                }
            } else {
                emit(Result.failure(Exception("Failed to get profile")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting profile")
            emit(Result.failure(e))
        }
    }

    override suspend fun updateUserProfile(firstName: String, lastName: String): Flow<Result<User>> = flow {
        try {
            val response = api.updateUserProfile(mapOf(
                "firstName" to firstName,
                "lastName" to lastName
            ))
            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()?.data?.toDomain()
                if (user != null) {
                    emit(Result.success(user))
                } else {
                    emit(Result.failure(Exception("Update failed")))
                }
            } else {
                emit(Result.failure(Exception("Profile update failed")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating profile")
            emit(Result.failure(e))
        }
    }

    override suspend fun addAddress(address: Address): Flow<Result<User>> = flow {
        try {
            val response = api.addAddress(mapOf(
                "title" to address.title,
                "fullAddress" to address.fullAddress,
                "province" to address.province,
                "city" to address.city,
                "postalCode" to address.postalCode
            ))
            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()?.data?.toDomain()
                if (user != null) {
                    emit(Result.success(user))
                } else {
                    emit(Result.failure(Exception("Failed to add address")))
                }
            } else {
                emit(Result.failure(Exception("Add address failed")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error adding address")
            emit(Result.failure(e))
        }
    }

    override suspend fun updateAddress(address: Address): Flow<Result<User>> = flow {
        try {
            val response = api.updateAddress(address.id, mapOf(
                "title" to address.title,
                "fullAddress" to address.fullAddress,
                "province" to address.province,
                "city" to address.city,
                "postalCode" to address.postalCode
            ))
            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()?.data?.toDomain()
                if (user != null) {
                    emit(Result.success(user))
                } else {
                    emit(Result.failure(Exception("Update failed")))
                }
            } else {
                emit(Result.failure(Exception("Address update failed")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating address")
            emit(Result.failure(e))
        }
    }

    override suspend fun deleteAddress(addressId: String): Flow<Result<Unit>> = flow {
        try {
            val response = api.deleteAddress(addressId)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Delete failed")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error deleting address")
            emit(Result.failure(e))
        }
    }
}

// ============== PAYMENT REPOSITORY ==============

interface IPaymentRepository {
    suspend fun processPayment(orderId: String, amount: Double, method: String): Flow<Result<Payment>>
    suspend fun getPaymentStatus(paymentId: String): Flow<Result<Payment>>
}

class PaymentRepository @Inject constructor(
    private val api: NoghreSodApi
) : IPaymentRepository {

    override suspend fun processPayment(orderId: String, amount: Double, method: String): Flow<Result<Payment>> = flow {
        try {
            val response = api.processPayment(mapOf(
                "orderId" to orderId,
                "amount" to amount,
                "method" to method
            ))
            if (response.isSuccessful && response.body()?.success == true) {
                val payment = response.body()?.data?.toDomain()
                if (payment != null) {
                    emit(Result.success(payment))
                } else {
                    emit(Result.failure(Exception("Payment failed")))
                }
            } else {
                emit(Result.failure(Exception("Payment processing failed")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error processing payment")
            emit(Result.failure(e))
        }
    }

    override suspend fun getPaymentStatus(paymentId: String): Flow<Result<Payment>> = flow {
        try {
            val response = api.getPaymentStatus(paymentId)
            if (response.isSuccessful && response.body()?.success == true) {
                val payment = response.body()?.data?.toDomain()
                if (payment != null) {
                    emit(Result.success(payment))
                } else {
                    emit(Result.failure(Exception("Payment not found")))
                }
            } else {
                emit(Result.failure(Exception("Failed to get status")))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting payment status")
            emit(Result.failure(e))
        }
    }
}

// ============== DTO TO DOMAIN CONVERTERS ==============

fun com.noghre.sod.data.remote.dto.CartDto.toDomain(): Cart {
    return Cart(
        id = this.id,
        userId = this.userId,
        items = this.items.map { it.toDomain() },
        totalPrice = this.totalPrice,
        itemCount = this.itemCount
    )
}

fun com.noghre.sod.data.remote.dto.CartItemDto.toDomain(): CartItem {
    return CartItem(
        id = this.id,
        cartId = this.id,
        product = this.product.toDomain(),
        quantity = this.quantity,
        selectedColor = this.selectedColor,
        selectedSize = this.selectedSize,
        subtotal = this.subtotal
    )
}

fun com.noghre.sod.data.remote.dto.OrderDto.toDomain(): Order {
    return Order(
        id = this.id,
        userId = this.userId,
        orderNumber = this.orderNumber,
        items = this.items.map { it.toDomain() },
        shippingAddress = this.shippingAddress.toDomain(),
        paymentMethod = PaymentMethod.CREDIT_CARD,
        subtotal = this.subtotal,
        shippingCost = this.shippingCost,
        discountAmount = this.discountAmount,
        totalAmount = this.totalAmount,
        status = OrderStatus.valueOf(this.status.uppercase()),
        tracking = this.tracking?.toDomain(),
        createdAt = this.createdAt
    )
}

fun com.noghre.sod.data.remote.dto.OrderItemDto.toDomain(): OrderItem {
    return OrderItem(
        id = this.id,
        product = this.product.toDomain(),
        quantity = this.quantity,
        unitPrice = this.unitPrice,
        selectedColor = this.selectedColor,
        selectedSize = this.selectedSize
    )
}

fun com.noghre.sod.data.remote.dto.UserDto.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        phoneNumber = this.phoneNumber ?: "",
        firstName = this.firstName ?: "",
        lastName = this.lastName ?: "",
        profileImageUrl = this.profileImageUrl,
        addresses = this.addresses?.map { it.toDomain() } ?: emptyList(),
        isVerified = this.isVerified,
        createdAt = this.createdAt
    )
}

fun com.noghre.sod.data.remote.dto.AddressDto.toDomain(): Address {
    return Address(
        id = this.id,
        title = this.title,
        fullAddress = this.fullAddress,
        province = this.province,
        city = this.city,
        postalCode = this.postalCode,
        isDefault = this.isDefault
    )
}

fun com.noghre.sod.data.remote.dto.PaymentDto.toDomain(): Payment {
    return Payment(
        id = this.id,
        orderId = this.orderId,
        userId = "",
        method = PaymentMethod.CREDIT_CARD,
        amount = this.amount,
        status = PaymentStatus.valueOf(this.status.uppercase()),
        transactionId = this.transactionId,
        createdAt = this.createdAt
    )
}

fun com.noghre.sod.data.remote.dto.OrderTrackingDto.toDomain(): OrderTracking {
    return OrderTracking(
        trackingNumber = this.trackingNumber,
        carrier = this.carrier,
        estimatedDelivery = this.estimatedDelivery,
        currentLocation = this.currentLocation,
        events = this.events?.map { it.toDomain() } ?: emptyList()
    )
}

fun com.noghre.sod.data.remote.dto.TrackingEventDto.toDomain(): TrackingEvent {
    return TrackingEvent(
        status = this.status,
        timestamp = this.timestamp,
        location = this.location,
        description = this.description
    )
}
