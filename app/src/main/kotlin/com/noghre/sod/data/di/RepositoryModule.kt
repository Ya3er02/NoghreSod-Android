package com.noghre.sod.data.di

import com.noghre.sod.data.repository.cart.CartRepositoryImpl
import com.noghre.sod.data.repository.cart.ICartRepository
import com.noghre.sod.data.repository.order.IOrderRepository
import com.noghre.sod.data.repository.order.OrderRepositoryImpl
import com.noghre.sod.data.repository.payment.IPaymentRepository
import com.noghre.sod.data.repository.payment.PaymentRepositoryImpl
import com.noghre.sod.data.repository.product.IProductRepository
import com.noghre.sod.data.repository.product.ProductRepositoryImpl
import com.noghre.sod.data.repository.user.IUserRepository
import com.noghre.sod.data.repository.user.UserRepositoryImpl
import com.noghre.sod.data.repository.wishlist.IWishlistRepository
import com.noghre.sod.data.repository.wishlist.WishlistRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for Repository dependency injection.
 *
 * Provides singleton instances of all repository implementations.
 * Binds interface contracts to their implementations for loose coupling.
 *
 * Usage in ViewModels:
 * ```
 * @HiltViewModel
 * class ProductViewModel @Inject constructor(
 *     private val productRepository: IProductRepository,
 *     private val cartRepository: ICartRepository
 * ) : ViewModel() { ... }
 * ```
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Bind product repository.
     * Manages product listing, search, filtering, and market prices.
     */
    @Singleton
    @Binds
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): IProductRepository

    /**
     * Bind cart repository.
     * Manages shopping cart operations with local caching.
     */
    @Singleton
    @Binds
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl
    ): ICartRepository

    /**
     * Bind order repository.
     * Manages order creation, tracking, and history.
     */
    @Singleton
    @Binds
    abstract fun bindOrderRepository(
        impl: OrderRepositoryImpl
    ): IOrderRepository

    /**
     * Bind user repository.
     * Manages user profile and addresses.
     */
    @Singleton
    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): IUserRepository

    /**
     * Bind payment repository.
     * Handles payment processing and verification.
     */
    @Singleton
    @Binds
    abstract fun bindPaymentRepository(
        impl: PaymentRepositoryImpl
    ): IPaymentRepository

    /**
     * Bind wishlist repository.
     * Manages wishlist and price drop notifications.
     */
    @Singleton
    @Binds
    abstract fun bindWishlistRepository(
        impl: WishlistRepositoryImpl
    ): IWishlistRepository
}
