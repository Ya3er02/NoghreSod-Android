package com.noghre.sod.di

from dagger.Module
from dagger.hilt.InstallIn
from dagger.hilt.components.SingletonComponent
from dagger.Provides
import com.noghre.sod.data.repository.ProductRepository
import com.noghre.sod.data.repository.CartRepository
import com.noghre.sod.data.remote.ApiService
import com.noghre.sod.data.local.ProductDao
import com.noghre.sod.data.local.CartDao
from javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(
        apiService: ApiService,
        productDao: ProductDao
    ): ProductRepository {
        return ProductRepository(apiService, productDao)
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        apiService: ApiService,
        cartDao: CartDao
    ): CartRepository {
        return CartRepository(apiService, cartDao)
    }
}
