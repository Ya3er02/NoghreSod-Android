package com.noghre.sod.di

import com.noghre.sod.data.local.dao.CartDao
import com.noghre.sod.data.local.dao.CategoryDao
import com.noghre.sod.data.local.dao.OrderDao
import com.noghre.sod.data.local.dao.ProductDao
import com.noghre.sod.data.local.dao.UserDao
import com.noghre.sod.data.local.database.NoghreSodDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing Room DAOs.
 * Separates database access layer from business logic.
 *
 * Each DAO is provided as a Singleton and derived from the main database instance.
 * This ensures consistent access to database operations throughout the application.
 *
 * DAOs provided:
 * - ProductDao: Database operations for products
 * - UserDao: Database operations for user profiles
 * - CartDao: Database operations for cart items
 * - OrderDao: Database operations for orders
 * - CategoryDao: Database operations for categories
 *
 * @author Yaser
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provide ProductDao instance for database access.
     * Used for CRUD operations on products and product-related data.
     *
     * @param database The main Room database instance
     * @return ProductDao instance
     */
    @Provides
    @Singleton
    fun provideProductDao(database: NoghreSodDatabase): ProductDao {
        return database.productDao()
    }
    
    /**
     * Provide UserDao instance for database access.
     * Used for CRUD operations on user profiles.
     *
     * @param database The main Room database instance
     * @return UserDao instance
     */
    @Provides
    @Singleton
    fun provideUserDao(database: NoghreSodDatabase): UserDao {
        return database.userDao()
    }
    
    /**
     * Provide CartDao instance for database access.
     * Used for CRUD operations on shopping cart items.
     *
     * @param database The main Room database instance
     * @return CartDao instance
     */
    @Provides
    @Singleton
    fun provideCartDao(database: NoghreSodDatabase): CartDao {
        return database.cartDao()
    }
    
    /**
     * Provide OrderDao instance for database access.
     * Used for CRUD operations on orders.
     *
     * @param database The main Room database instance
     * @return OrderDao instance
     */
    @Provides
    @Singleton
    fun provideOrderDao(database: NoghreSodDatabase): OrderDao {
        return database.orderDao()
    }
    
    /**
     * Provide CategoryDao instance for database access.
     * Used for CRUD operations on product categories.
     *
     * @param database The main Room database instance
     * @return CategoryDao instance
     */
    @Provides
    @Singleton
    fun provideCategoryDao(database: NoghreSodDatabase): CategoryDao {
        return database.categoryDao()
    }
}
