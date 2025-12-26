package com.noghre.sod.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier for IO Dispatcher.
 * Used for I/O operations like database access and network calls.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/**
 * Qualifier for Main Dispatcher.
 * Used for UI operations and main thread operations.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

/**
 * Qualifier for Default Dispatcher.
 * Used for CPU-intensive operations.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

/**
 * Hilt module for providing Coroutine Dispatchers.
 * Enables easy testing and dispatcher injection.
 *
 * Dispatchers provided:
 * - IoDispatcher: For I/O operations (Dispatchers.IO)
 *   - Thread pool optimized for I/O operations
 *   - Used for database queries, network calls, file I/O
 *
 * - MainDispatcher: For main thread operations (Dispatchers.Main)
 *   - Main/UI thread dispatcher
 *   - Used for UI updates and critical sections
 *
 * - DefaultDispatcher: For CPU-intensive operations (Dispatchers.Default)
 *   - Shared thread pool for CPU work
 *   - Used for heavy computations and parsing
 *
 * Benefits:
 * - Easy to test by injecting test dispatchers
 * - Separation of concerns for different workloads
 * - Performance optimization by using appropriate dispatcher
 * - Centralized dispatcher configuration
 *
 * @author Yaser
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    
    /**
     * Provide IO Dispatcher for I/O operations.
     * Optimized for database queries, network calls, and file I/O.
     *
     * @return IO CoroutineDispatcher
     */
    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
    
    /**
     * Provide Main Dispatcher for UI operations.
     * Used for UI updates and main thread operations.
     *
     * @return Main CoroutineDispatcher
     */
    @Provides
    @Singleton
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }
    
    /**
     * Provide Default Dispatcher for CPU-intensive operations.
     * Used for heavy computations and parsing operations.
     *
     * @return Default CoroutineDispatcher
     */
    @Provides
    @Singleton
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher {
        return Dispatchers.Default
    }
}
