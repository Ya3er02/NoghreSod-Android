package com.noghre.sod.core.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🍀 Remote Configuration Manager
 *
 * Firebase Remote Config integration:
 * - Feature flags (A/B testing, feature rollout)
 * - Dynamic pricing configuration
 * - API endpoint switching
 * - UI theme/language settings
 * - Rate limiting configuration
 * - Cache duration settings
 * - Maintenance mode toggle
 * - Analytics tracking flags
 * - Payment method availability
 * - Minimum app version requirement
 *
 * Benefits:
 * - Update config without app release
 * - A/B test new features
 * - Control feature rollout
 * - Disable features for debugging
 * - Centralized configuration management
 *
 * @since 1.0.0
 */
@Singleton
class RemoteConfigManager @Inject constructor() {
    
    private val remoteConfig: FirebaseRemoteConfig by lazy {
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync {
                minimumFetchIntervalInSeconds = 3600 // 1 hour
            }
            
            // Set default values (fallback if fetch fails)
            setDefaultsAsync(mapOf(
                // Feature Flags
                CONFIG_BIOMETRIC_AUTH_ENABLED to true,
                CONFIG_DARK_MODE_ENABLED to true,
                CONFIG_MAINTENANCE_MODE to false,
                CONFIG_DEBUG_LOGGING to false,
                
                // Pricing
                CONFIG_PRICE_CURRENCY to "IRR",
                CONFIG_TAX_PERCENTAGE to 9,
                CONFIG_SHIPPING_COST to 50000,
                
                // API
                CONFIG_API_BASE_URL to "https://api.noghresod.ir",
                CONFIG_API_TIMEOUT_SECONDS to 30,
                CONFIG_MAX_RETRIES to 3,
                
                // Payment
                CONFIG_PAYMENT_METHODS_ENABLED to "razorpay,stripe,bank_transfer",
                CONFIG_MIN_PAYMENT_AMOUNT to 100000,
                CONFIG_MAX_PAYMENT_AMOUNT to 100000000,
                
                // Limits
                CONFIG_MAX_SEARCH_RESULTS to 100,
                CONFIG_ITEMS_PER_PAGE to 20,
                CONFIG_IMAGE_CACHE_SIZE_MB to 100,
                
                // Version
                CONFIG_MIN_APP_VERSION to "1.0.0",
                CONFIG_RECOMMENDED_VERSION to "1.0.0",
                CONFIG_FORCE_UPDATE to false
            ))
        }
    }
    
    companion object {
        // Feature Flags
        const val CONFIG_BIOMETRIC_AUTH_ENABLED = "biometric_auth_enabled"
        const val CONFIG_DARK_MODE_ENABLED = "dark_mode_enabled"
        const val CONFIG_MAINTENANCE_MODE = "maintenance_mode"
        const val CONFIG_DEBUG_LOGGING = "debug_logging"
        
        // Pricing Configuration
        const val CONFIG_PRICE_CURRENCY = "price_currency"
        const val CONFIG_TAX_PERCENTAGE = "tax_percentage"
        const val CONFIG_SHIPPING_COST = "shipping_cost"
        
        // API Configuration
        const val CONFIG_API_BASE_URL = "api_base_url"
        const val CONFIG_API_TIMEOUT_SECONDS = "api_timeout_seconds"
        const val CONFIG_MAX_RETRIES = "max_retries"
        
        // Payment Configuration
        const val CONFIG_PAYMENT_METHODS_ENABLED = "payment_methods_enabled"
        const val CONFIG_MIN_PAYMENT_AMOUNT = "min_payment_amount"
        const val CONFIG_MAX_PAYMENT_AMOUNT = "max_payment_amount"
        
        // Limit Configuration
        const val CONFIG_MAX_SEARCH_RESULTS = "max_search_results"
        const val CONFIG_ITEMS_PER_PAGE = "items_per_page"
        const val CONFIG_IMAGE_CACHE_SIZE_MB = "image_cache_size_mb"
        
        // Version Configuration
        const val CONFIG_MIN_APP_VERSION = "min_app_version"
        const val CONFIG_RECOMMENDED_VERSION = "recommended_version"
        const val CONFIG_FORCE_UPDATE = "force_update"
    }
    
    /**
     * Fetch and activate remote config
     */
    suspend fun fetchAndActivate(): Boolean {
        return try {
            remoteConfig.fetchAndActivate().await()
            Timber.d("🍀 Remote config fetched and activated")
            true
        } catch (e: Exception) {
            Timber.e(e, "🍀 Failed to fetch remote config")
            false
        }
    }
    
    /**
     * Fetch remote config (without activation)
     */
    suspend fun fetch(): Boolean {
        return try {
            remoteConfig.fetch().await()
            Timber.d("🍀 Remote config fetched")
            true
        } catch (e: Exception) {
            Timber.e(e, "🍀 Failed to fetch remote config")
            false
        }
    }
    
    /**
     * Activate fetched remote config
     */
    fun activate() {
        remoteConfig.activate()
        Timber.d("🍀 Remote config activated")
    }
    
    // ==================== FEATURE FLAGS ====================
    
    /**
     * Check if feature is enabled
     */
    fun isFeatureEnabled(flag: String): Boolean {
        val enabled = remoteConfig.getBoolean(flag)
        Timber.d("🍀 Feature '$flag': $enabled")
        return enabled
    }
    
    fun isBiometricAuthEnabled(): Boolean = isFeatureEnabled(CONFIG_BIOMETRIC_AUTH_ENABLED)
    fun isDarkModeEnabled(): Boolean = isFeatureEnabled(CONFIG_DARK_MODE_ENABLED)
    fun isMaintenanceModeEnabled(): Boolean = isFeatureEnabled(CONFIG_MAINTENANCE_MODE)
    fun isDebugLoggingEnabled(): Boolean = isFeatureEnabled(CONFIG_DEBUG_LOGGING)
    
    // ==================== PRICING ====================
    
    fun getPriceCurrency(): String = remoteConfig.getString(CONFIG_PRICE_CURRENCY)
    fun getTaxPercentage(): Double = remoteConfig.getDouble(CONFIG_TAX_PERCENTAGE)
    fun getShippingCost(): Long = remoteConfig.getLong(CONFIG_SHIPPING_COST)
    
    fun calculateTax(amount: Long): Long {
        val taxPercent = getTaxPercentage()
        return (amount * taxPercent / 100).toLong()
    }
    
    // ==================== API CONFIGURATION ====================
    
    fun getApiBaseUrl(): String = remoteConfig.getString(CONFIG_API_BASE_URL)
    fun getApiTimeoutSeconds(): Long = remoteConfig.getLong(CONFIG_API_TIMEOUT_SECONDS)
    fun getMaxRetries(): Long = remoteConfig.getLong(CONFIG_MAX_RETRIES)
    
    // ==================== PAYMENT CONFIGURATION ====================
    
    fun getEnabledPaymentMethods(): List<String> {
        val methods = remoteConfig.getString(CONFIG_PAYMENT_METHODS_ENABLED)
        return methods.split(",").map { it.trim() }
    }
    
    fun isPaymentMethodEnabled(method: String): Boolean {
        return getEnabledPaymentMethods().contains(method)
    }
    
    fun getMinPaymentAmount(): Long = remoteConfig.getLong(CONFIG_MIN_PAYMENT_AMOUNT)
    fun getMaxPaymentAmount(): Long = remoteConfig.getLong(CONFIG_MAX_PAYMENT_AMOUNT)
    
    fun isPaymentAmountValid(amount: Long): Boolean {
        val min = getMinPaymentAmount()
        val max = getMaxPaymentAmount()
        return amount in min..max
    }
    
    // ==================== LIMITS ====================
    
    fun getMaxSearchResults(): Long = remoteConfig.getLong(CONFIG_MAX_SEARCH_RESULTS)
    fun getItemsPerPage(): Long = remoteConfig.getLong(CONFIG_ITEMS_PER_PAGE)
    fun getImageCacheSizeMb(): Long = remoteConfig.getLong(CONFIG_IMAGE_CACHE_SIZE_MB)
    
    // ==================== VERSION MANAGEMENT ====================
    
    fun getMinAppVersion(): String = remoteConfig.getString(CONFIG_MIN_APP_VERSION)
    fun getRecommendedVersion(): String = remoteConfig.getString(CONFIG_RECOMMENDED_VERSION)
    fun isForceUpdateRequired(): Boolean = remoteConfig.getBoolean(CONFIG_FORCE_UPDATE)
    
    /**
     * Compare app versions
     * Returns: -1 if v1 < v2, 0 if equal, 1 if v1 > v2
     */
    fun compareVersions(v1: String, v2: String): Int {
        val parts1 = v1.split(".")
        val parts2 = v2.split(".")
        
        for (i in 0 until maxOf(parts1.size, parts2.size)) {
            val major1 = parts1.getOrNull(i)?.toIntOrNull() ?: 0
            val major2 = parts2.getOrNull(i)?.toIntOrNull() ?: 0
            
            when {
                major1 < major2 -> return -1
                major1 > major2 -> return 1
            }
        }
        
        return 0
    }
    
    /**
     * Check if app update is required
     */
    fun isUpdateRequired(currentVersion: String): Boolean {
        val minVersion = getMinAppVersion()
        return compareVersions(currentVersion, minVersion) < 0
    }
    
    /**
     * Check if update is recommended
     */
    fun isUpdateRecommended(currentVersion: String): Boolean {
        val recommendedVersion = getRecommendedVersion()
        return compareVersions(currentVersion, recommendedVersion) < 0
    }
    
    // ==================== GENERIC METHODS ====================
    
    /**
     * Get boolean value
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return try {
            remoteConfig.getBoolean(key)
        } catch (e: Exception) {
            Timber.w("Config key '$key' not found, using default: $defaultValue")
            defaultValue
        }
    }
    
    /**
     * Get string value
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return try {
            remoteConfig.getString(key)
        } catch (e: Exception) {
            Timber.w("Config key '$key' not found, using default: $defaultValue")
            defaultValue
        }
    }
    
    /**
     * Get long value
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return try {
            remoteConfig.getLong(key)
        } catch (e: Exception) {
            Timber.w("Config key '$key' not found, using default: $defaultValue")
            defaultValue
        }
    }
    
    /**
     * Get double value
     */
    fun getDouble(key: String, defaultValue: Double = 0.0): Double {
        return try {
            remoteConfig.getDouble(key)
        } catch (e: Exception) {
            Timber.w("Config key '$key' not found, using default: $defaultValue")
            defaultValue
        }
    }
    
    /**
     * Get all config parameters
     */
    fun getAllParameters(): Map<String, Any> {
        return remoteConfig.all.mapValues { (_, value) ->
            value.asString()
        }
    }
    
    /**
     * Get last fetch time
     */
    fun getLastFetchTimeMillis(): Long {
        return remoteConfig.info.fetchTimeMillis
    }
    
    /**
     * Get fetch status
     */
    fun getFetchStatus(): String {
        return when (remoteConfig.info.lastFetchStatus) {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig.LAST_FETCH_STATUS_NO_FETCH_YET -> "No fetch yet"
            com.google.firebase.remoteconfig.FirebaseRemoteConfig.LAST_FETCH_STATUS_SUCCESS -> "Success"
            com.google.firebase.remoteconfig.FirebaseRemoteConfig.LAST_FETCH_STATUS_THROTTLED -> "Throttled"
            com.google.firebase.remoteconfig.FirebaseRemoteConfig.LAST_FETCH_STATUS_FAILURE -> "Failure"
            else -> "Unknown"
        }
    }
}
