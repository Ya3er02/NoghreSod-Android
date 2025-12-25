package com.noghre.sod.domain.usecase

import com.noghre.sod.domain.model.*
import com.noghre.sod.data.repository.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ============== PRODUCT USE CASES ==============

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(limit: Int = 20, offset: Int = 0): Flow<Result<List<Product>>> {
        return repository.getProducts(limit, offset)
    }
}

class SearchProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(query: String): Flow<Result<List<Product>>> {
        return repository.searchProducts(query)
    }
}

class GetProductDetailUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(productId: String): Flow<Result<Product>> {
        return repository.getProductById(productId)
    }
}

class GetProductsByCategoryUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(category: String): Flow<Result<List<Product>>> {
        return repository.getProductsByCategory(category)
    }
}

class GetFeaturedProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<Result<List<Product>>> {
        return repository.getFeaturedProducts()
    }
}

class GetCategoriesUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<Result<List<String>>> {
        return repository.getAllCategories()
    }
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(product: Product): Flow<Result<Product>> {
        return repository.toggleFavorite(product)
    }
}

class GetFavoriteProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<Result<List<Product>>> {
        return repository.getFavoriteProducts()
    }
}

// ============== CART USE CASES ==============

class GetCartUseCase @Inject constructor(
    private val repository: ICartRepository
) {
    operator fun invoke(userId: String): Flow<Result<Cart>> {
        return repository.getCart(userId)
    }
}

class AddToCartUseCase @Inject constructor(
    private val repository: ICartRepository
) {
    operator fun invoke(cartId: String, product: Product, quantity: Int): Flow<Result<Cart>> {
        return repository.addToCart(cartId, product, quantity)
    }
}

class UpdateCartItemUseCase @Inject constructor(
    private val repository: ICartRepository
) {
    operator fun invoke(itemId: String, quantity: Int): Flow<Result<Cart>> {
        return repository.updateCartItem(itemId, quantity)
    }
}

class RemoveFromCartUseCase @Inject constructor(
    private val repository: ICartRepository
) {
    operator fun invoke(itemId: String): Flow<Result<Cart>> {
        return repository.removeFromCart(itemId)
    }
}

class ClearCartUseCase @Inject constructor(
    private val repository: ICartRepository
) {
    operator fun invoke(cartId: String): Flow<Result<Unit>> {
        return repository.clearCart(cartId)
    }
}

// ============== ORDER USE CASES ==============

class GetOrdersUseCase @Inject constructor(
    private val repository: IOrderRepository
) {
    operator fun invoke(userId: String): Flow<Result<List<Order>>> {
        return repository.getOrders(userId)
    }
}

class GetOrderDetailUseCase @Inject constructor(
    private val repository: IOrderRepository
) {
    operator fun invoke(orderId: String): Flow<Result<Order>> {
        return repository.getOrderDetail(orderId)
    }
}

class CreateOrderUseCase @Inject constructor(
    private val repository: IOrderRepository
) {
    operator fun invoke(order: Order): Flow<Result<Order>> {
        return repository.createOrder(order)
    }
}

class GetOrderTrackingUseCase @Inject constructor(
    private val repository: IOrderRepository
) {
    operator fun invoke(orderId: String): Flow<Result<Order>> {
        return repository.getOrderTracking(orderId)
    }
}

// ============== USER USE CASES ==============

class GetUserProfileUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    operator fun invoke(): Flow<Result<User>> {
        return repository.getUserProfile()
    }
}

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    operator fun invoke(firstName: String, lastName: String): Flow<Result<User>> {
        return repository.updateUserProfile(firstName, lastName)
    }
}

class AddAddressUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    operator fun invoke(address: Address): Flow<Result<User>> {
        return repository.addAddress(address)
    }
}

class UpdateAddressUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    operator fun invoke(address: Address): Flow<Result<User>> {
        return repository.updateAddress(address)
    }
}

class DeleteAddressUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    operator fun invoke(addressId: String): Flow<Result<Unit>> {
        return repository.deleteAddress(addressId)
    }
}

// ============== PAYMENT USE CASES ==============

class ProcessPaymentUseCase @Inject constructor(
    private val repository: IPaymentRepository
) {
    operator fun invoke(orderId: String, amount: Double, method: String): Flow<Result<Payment>> {
        return repository.processPayment(orderId, amount, method)
    }
}

class GetPaymentStatusUseCase @Inject constructor(
    private val repository: IPaymentRepository
) {
    operator fun invoke(paymentId: String): Flow<Result<Payment>> {
        return repository.getPaymentStatus(paymentId)
    }
}

// ============== AUTHENTICATION USE CASES ==============

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<Result<User>> {
        return repository.login(email, password)
    }
}

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<Result<User>> {
        return repository.register(email, password)
    }
}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<Result<Unit>> {
        return repository.logout()
    }
}

class RefreshTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<Result<String>> {
        return repository.refreshToken()
    }
}
