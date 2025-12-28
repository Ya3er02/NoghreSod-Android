package com.noghre.sod.data.repository

import com.noghre.sod.core.error.AppException
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.remote.AnalyticsApi
import com.noghre.sod.domain.repository.AnalyticsRepository
import timber.log.Timber
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsApi: AnalyticsApi
) : AnalyticsRepository {
    
    override suspend fun trackEvent(eventName: String, properties: Map<String, String>): Result<Unit> = try {
        Timber.d("Tracking event: $eventName with properties: ${properties.size}")
        
        val eventData = mapOf(
            "name" to eventName,
            "timestamp" to System.currentTimeMillis().toString(),
            "properties" to properties.toString()
        )
        
        analyticsApi.trackEvent(eventData)
        Timber.d("Event tracked successfully: $eventName")
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error tracking event: ${e.message}")
        // Don't propagate error, analytics should not affect user experience
        Result.Success(Unit)
    }
    
    override suspend fun trackPageView(pageName: String): Result<Unit> = try {
        Timber.d("Tracking page view: $pageName")
        
        val eventData = mapOf(
            "type" to "page_view",
            "page" to pageName,
            "timestamp" to System.currentTimeMillis().toString()
        )
        
        analyticsApi.trackEvent(eventData)
        Timber.d("Page view tracked: $pageName")
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error tracking page view: ${e.message}")
        Result.Success(Unit)
    }
    
    override suspend fun trackUserAction(action: String, details: Map<String, String>): Result<Unit> = try {
        Timber.d("Tracking user action: $action")
        
        val eventData = mapOf(
            "type" to "user_action",
            "action" to action,
            "timestamp" to System.currentTimeMillis().toString(),
            "details" to details.toString()
        )
        
        analyticsApi.trackEvent(eventData)
        Timber.d("User action tracked: $action")
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error tracking user action: ${e.message}")
        Result.Success(Unit)
    }
    
    override suspend fun trackError(errorName: String, errorMessage: String, stackTrace: String): Result<Unit> = try {
        Timber.d("Tracking error: $errorName")
        Timber.e("Error details - $errorName: $errorMessage")
        
        val eventData = mapOf(
            "type" to "error",
            "name" to errorName,
            "message" to errorMessage,
            "stackTrace" to stackTrace,
            "timestamp" to System.currentTimeMillis().toString()
        )
        
        analyticsApi.trackEvent(eventData)
        Timber.d("Error tracked: $errorName")
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error tracking error event: ${e.message}")
        Result.Success(Unit)
    }
    
    override suspend fun trackConversion(conversionType: String, value: Double): Result<Unit> = try {
        Timber.d("Tracking conversion: $conversionType, value: $value")
        
        val eventData = mapOf(
            "type" to "conversion",
            "conversion_type" to conversionType,
            "value" to value.toString(),
            "timestamp" to System.currentTimeMillis().toString()
        )
        
        analyticsApi.trackEvent(eventData)
        Timber.d("Conversion tracked: $conversionType")
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error tracking conversion: ${e.message}")
        Result.Success(Unit)
    }
    
    override suspend fun setUserProperty(key: String, value: String): Result<Unit> = try {
        Timber.d("Setting user property: $key = $value")
        
        analyticsApi.setUserProperty(key, value)
        Timber.d("User property set: $key")
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error setting user property: ${e.message}")
        Result.Success(Unit)
    }
    
    override suspend fun getUserAnalytics(): Result<Map<String, String>> = try {
        Timber.d("Fetching user analytics")
        
        val analytics = analyticsApi.getUserAnalytics()
        Timber.d("User analytics fetched")
        
        Result.Success(analytics)
    } catch (e: Exception) {
        Timber.e("Error fetching user analytics: ${e.message}")
        Result.Error(AppException.NetworkException(e.message ?: "Failed to fetch analytics"))
    }
}
