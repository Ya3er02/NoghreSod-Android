package com.noghre.sod.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency Injection module for Analytics and Logging
 */
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @ApplicationContext context: Context
    ): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideCrashlytics(): FirebaseCrashlytics {
        return FirebaseCrashlytics.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideFirebasePerformance(): FirebasePerformance {
        return FirebasePerformance.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideAnalyticsTracker(
        analytics: FirebaseAnalytics,
        crashlytics: FirebaseCrashlytics
    ): AnalyticsTracker {
        return AnalyticsTrackerImpl(analytics, crashlytics)
    }
}

/**
 * Analytics tracking interface
 */
interface AnalyticsTracker {
    fun trackEvent(name: String, params: Map<String, Any> = emptyMap())
    fun trackError(exception: Throwable, context: String = "")
    fun trackUserAction(action: String, metadata: Map<String, String> = emptyMap())
    fun trackScreenView(screenName: String)
    fun setUserProperties(userId: String, email: String, username: String)
    fun logException(exception: Exception)
}

/**
 * Analytics tracker implementation
 */
class AnalyticsTrackerImpl(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val crashlytics: FirebaseCrashlytics
) : AnalyticsTracker {
    
    override fun trackEvent(name: String, params: Map<String, Any>) {
        val bundle = android.os.Bundle().apply {
            params.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }
    
    override fun trackError(exception: Throwable, context: String) {
        crashlytics.recordException(exception)
        if (context.isNotEmpty()) {
            crashlytics.log("Context: $context")
        }
    }
    
    override fun trackUserAction(action: String, metadata: Map<String, String>) {
        val params = metadata.toMutableMap().apply {
            put("action_type", action)
            put("timestamp", System.currentTimeMillis().toString())
        }
        trackEvent("user_action", params.mapValues { it.value as Any })
    }
    
    override fun trackScreenView(screenName: String) {
        trackEvent("screen_view", mapOf("screen_name" to screenName))
    }
    
    override fun setUserProperties(userId: String, email: String, username: String) {
        firebaseAnalytics.setUserId(userId)
        firebaseAnalytics.setUserProperty("email", email)
        firebaseAnalytics.setUserProperty("username", username)
    }
    
    override fun logException(exception: Exception) {
        crashlytics.recordException(exception)
    }
}
