package com.noghre.sod.di

import com.noghre.sod.data.local.dao.AddressDao
import com.noghre.sod.data.local.dao.CartDao
import com.noghre.sod.data.local.dao.CategoryDao
import com.noghre.sod.data.local.dao.OrderDao
import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.local.dao.UserDao
import com.noghre.sod.data.local.database.NoghreSodDatabase
import com.noghre.sod.data.local.prefs.TokenManager
import com.noghre.sod.data.mapper.AddressMapper
import com.noghre.sod.data.mapper.CartMapper
import com.noghre.sod.data.mapper.CategoryMapper
import com.noghre.sod.data.mapper.OrderMapper
import com.noghre.sod.data.mapper.ProductMapper
import com.noghre.sod.data.mapper.UserMapper
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.network.NetworkMonitor
import com.noghre.sod.data.repository.AddressRepository
import com.noghre.sod.data.repository.AddressRepositoryImpl
import com.noghre.sod.data.repository.CartRepository
import com.noghre.sod.data.repository.CartRepositoryImpl
import com.noghre.sod.data.repository.CategoryRepository
import com.noghre.sod.data.repository.CategoryRepositoryImpl
import com.noghre.sod.data.repository.OrderRepository
import com.noghre.sod.data.repository.OrderRepositoryImpl
import com.noghre.sod.data.repository.ProductRepository
import com.noghre.sod.data.repository.ProductRepositoryImpl
import com.noghre.sod.data.repository.UserRepository
import com.noghre.sod.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module for repositories
 * Provides all repository implementations with their dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // DAOs
    @Provides
    @Singleton
    fun provideProductDao(database: NoghreSodDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: NoghreSodDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideCartDao(database: NoghreSodDatabase): CartDao {
        return database.cartDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: NoghreSodDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideOrderDao(database: NoghreSodDatabase): OrderDao {
        return database.orderDao()
    }

    @Provides
    @Singleton
    fun provideAddressDao(database: NoghreSodDatabase): AddressDao {
        return database.addressDao()
    }

    // Mappers
    @Provides
    @Singleton
    fun provideProductMapper(): ProductMapper = ProductMapper()

    @Provides
    @Singleton
    fun provideCategoryMapper(): CategoryMapper = CategoryMapper()

    @Provides
    @Singleton
    fun provideCartMapper(): CartMapper = CartMapper()

    @Provides
    @Singleton
    fun provideUserMapper(): UserMapper = UserMapper()

    @Provides
    @Singleton
    fun provideOrderMapper(): OrderMapper = OrderMapper()

    @Provides
    @Singleton
    fun provideAddressMapper(): AddressMapper = AddressMapper()

    // Repositories
    @Provides
    @Singleton
    fun provideProductRepository(
        apiService: NoghreSodApiService,
        productDao: ProductDao,
        networkMonitor: NetworkMonitor,
        mapper: ProductMapper
    ): ProductRepository {
        return ProductRepositoryImpl(apiService, productDao, networkMonitor, mapper)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        apiService: NoghreSodApiService,
        categoryDao: CategoryDao,
        networkMonitor: NetworkMonitor,
        mapper: CategoryMapper
    ): CategoryRepository {
        return CategoryRepositoryImpl(apiService, categoryDao, networkMonitor, mapper)
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        apiService: NoghreSodApiService,
        cartDao: CartDao,
        networkMonitor: NetworkMonitor,
        mapper: CartMapper
    ): CartRepository {
        return CartRepositoryImpl(apiService, cartDao, networkMonitor, mapper)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: NoghreSodApiService,
        userDao: UserDao,
        tokenManager: TokenManager,
        mapper: UserMapper
    ): UserRepository {
        return UserRepositoryImpl(apiService, userDao, tokenManager, mapper)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(
        apiService: NoghreSodApiService,
        orderDao: OrderDao,
        networkMonitor: NetworkMonitor,
        mapper: OrderMapper
    ): OrderRepository {
        return OrderRepositoryImpl(apiService, orderDao, networkMonitor, mapper)
    }

    @Provides
    @Singleton
    fun provideAddressRepository(
        apiService: NoghreSodApiService,
        addressDao: AddressDao,
        mapper: AddressMapper
    ): AddressRepository {
        return AddressRepositoryImpl(apiService, addressDao, mapper)
    }
}