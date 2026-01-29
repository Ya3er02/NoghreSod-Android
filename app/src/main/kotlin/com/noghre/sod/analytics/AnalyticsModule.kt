package com.noghre.sod.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Singleton

/**
 * Hilt module for Analytics dependencies.
 *
 * Provides singleton instances of:
 * - FirebaseAnalytics
 * - FirebaseCrashlytics
 * - AnalyticsManager
 * - AnalyticsRepository
 * - AnalyticsTracker
 *
 * All instances are thread-safe and follow dependency injection best practices.
 *
 * Usage:
 * ```
 * @Inject
 * lateinit var analyticsManager: AnalyticsManager
 *
 * // In your screen/ViewModel:
 * analyticsManager.logProductView(productId, name, category, price)
 * ```
 *
 * @author NoghreSod Team
 * @version 2.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    /**
     * Provides singleton instance of FirebaseAnalytics.
     *
     * @return Singleton FirebaseAnalytics instance
     */
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics {
        return try {
            FirebaseAnalytics.getInstance()
        } catch (e: Exception) {
            Timber.e(e, "Error initializing FirebaseAnalytics")
            throw e
        }
    }

    /**
     * Provides singleton instance of FirebaseCrashlytics.
     *
     * @return Singleton FirebaseCrashlytics instance
     */
    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
        return try {
            FirebaseCrashlytics.getInstance()
        } catch (e: Exception) {
            Timber.e(e, "Error initializing FirebaseCrashlytics")
            throw e
        }
    }

    /**
     * Provides IO CoroutineDispatcher for async operations.
     *
     * Used for non-blocking I/O operations in analytics.
     *
     * @return CoroutineDispatcher for IO operations
     */
    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    /**
     * Provides singleton instance of AnalyticsManager.
     *
     * AnalyticsManager is responsible for:
     * - Firebase Analytics event tracking (thread-safe via ioDispatcher)
     * - Crashlytics exception reporting
     * - User property tracking
     * - Screen view tracking
     * - GDPR-compliant consent management
     *
     * All operations are dispatched to IO thread to avoid blocking main thread.
     *
     * @param firebaseAnalytics FirebaseAnalytics instance
     * @param firebaseCrashlytics FirebaseCrashlytics instance
     * @param ioDispatcher CoroutineDispatcher for IO operations
     * @return Singleton AnalyticsManager instance
     */
    @Provides
    @Singleton
    fun provideAnalyticsManager(
        firebaseAnalytics: FirebaseAnalytics,
        firebaseCrashlytics: FirebaseCrashlytics,
        ioDispatcher: CoroutineDispatcher,
    ): AnalyticsManager {
        return AnalyticsManager(
            firebaseAnalytics = firebaseAnalytics,
            firebaseCrashlytics = firebaseCrashlytics,
            ioDispatcher = ioDispatcher,
        )
    }

    /**
     * Provides singleton instance of AnalyticsRepository.
     *
     * AnalyticsRepository handles:
     * - Event queue management for offline capability
     * - Event batching for efficient Firebase syncing
     * - Event stream for monitoring and debugging
     * - Proper error handling and requeuing of failed events
     *
     * @param analyticsManager AnalyticsManager instance
     * @param ioDispatcher CoroutineDispatcher for IO operations
     * @return Singleton AnalyticsRepository instance
     */
    @Provides
    @Singleton
    fun provideAnalyticsRepository(
        analyticsManager: AnalyticsManager,
        ioDispatcher: CoroutineDispatcher,
    ): AnalyticsRepository {
        return AnalyticsRepository(
            analyticsManager = analyticsManager,
            ioDispatcher = ioDispatcher,
        )
    }

    /**
     * Provides singleton instance of AnalyticsTracker utility.
     *
     * AnalyticsTracker provides convenient methods for:
     * - Button click tracking
     * - Form submission tracking
     * - Field focus tracking
     * - Scroll behavior tracking
     * - Share action tracking
     * - Error/exception tracking from user actions
     *
     * @param analyticsManager AnalyticsManager instance
     * @return Singleton AnalyticsTracker instance
     */
    @Provides
    @Singleton
    fun provideAnalyticsTracker(
        analyticsManager: AnalyticsManager,
    ): AnalyticsTracker {
        return AnalyticsTracker(analyticsManager = analyticsManager)
    }
}
