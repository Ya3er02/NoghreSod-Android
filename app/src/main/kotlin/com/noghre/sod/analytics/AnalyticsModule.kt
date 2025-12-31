package com.noghre.sod.analytics

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Analytics dependencies.
 *
 * Provides singleton instances of AnalyticsManager and other analytics components
 * throughout the application.
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
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    /**
     * Provides singleton instance of AnalyticsManager.
     *
     * AnalyticsManager is responsible for:
     * - Firebase Analytics event tracking
     * - Crashlytics exception reporting
     * - User property tracking
     * - Screen view tracking
     *
     * @return Singleton AnalyticsManager instance
     */
    @Provides
    @Singleton
    fun provideAnalyticsManager(): AnalyticsManager {
        return AnalyticsManager()
    }
}
