package com.noghre.sod.core.config

/**
 * Centralized configuration object for the entire application.
 * Contains all constants and configuration values required by the app.
 *
 * This class follows the Single Source of Truth (SSOT) principle,
 * allowing easy maintenance and modification of app-wide settings.
 */
object AppConfig {

    // ===== API Configuration =====
    object Api {
        /** Base URL for all API endpoints */
        const val BASE_URL = "https://api.noghresod.com/"
        
        /** Request timeout in seconds */
        const val CONNECT_TIMEOUT_SECONDS = 30L
        
        /** Read timeout in seconds */
        const val READ_TIMEOUT_SECONDS = 30L
        
        /** Write timeout in seconds */
        const val WRITE_TIMEOUT_SECONDS = 30L
        
        /** Maximum retry attempts for failed requests */
        const val MAX_RETRIES = 3
        
        /** Initial retry delay in milliseconds */
        const val INITIAL_RETRY_DELAY_MS = 1000L
        
        /** Multiplier for exponential backoff retry strategy */
        const val RETRY_BACKOFF_MULTIPLIER = 2.0
        
        /** Maximum retry delay in milliseconds */
        const val MAX_RETRY_DELAY_MS = 10000L
    }

    // ===== Token Configuration =====
    object Token {
        /** Time before token expiry to refresh (buffer time in minutes) */
        const val REFRESH_BUFFER_MINUTES = 5
        
        /** Default token expiry time in seconds (24 hours) */
        const val DEFAULT_EXPIRY_SECONDS = 86400L
    }

    // ===== Pagination Configuration =====
    object Pagination {
        /** Default page size for paginated requests */
        const val DEFAULT_PAGE_SIZE = 20
        
        /** Maximum page size to prevent abuse */
        const val MAX_PAGE_SIZE = 100
        
        /** Initial page number (usually 1 for REST APIs) */
        const val INITIAL_PAGE = 1
        
        /** Default page size for local list operations */
        const val LOCAL_PAGE_SIZE = 50
    }

    // ===== Cache Configuration =====
    object Cache {
        /** Cache validity duration in minutes for products (60 minutes) */
        const val PRODUCT_CACHE_VALIDITY_MINUTES = 60
        
        /** Cache validity duration in minutes for categories (24 hours) */
        const val CATEGORY_CACHE_VALIDITY_MINUTES = 1440
        
        /** Cache validity duration in minutes for prices (15 minutes) */
        const val PRICE_CACHE_VALIDITY_MINUTES = 15
        
        /** Cache validity duration in minutes for user data (30 minutes) */
        const val USER_CACHE_VALIDITY_MINUTES = 30
        
        /** Maximum size of memory cache in MB */
        const val MEMORY_CACHE_SIZE_MB = 100
        
        /** Maximum size of disk cache in MB */
        const val DISK_CACHE_SIZE_MB = 500
    }

    // ===== Security Configuration =====
    object Security {
        /** Enable SSL certificate pinning */
        const val ENABLE_CERTIFICATE_PINNING = true
        
        /** Enable ProGuard obfuscation in release builds */
        const val ENABLE_PROGUARD = true
        
        /** Enable root detection */
        const val ENABLE_ROOT_DETECTION = true
        
        /** Enable emulator detection */
        const val ENABLE_EMULATOR_DETECTION = true
        
        /** Enable debugger detection */
        const val ENABLE_DEBUGGER_DETECTION = true
        
        /** Enable database encryption with SQLCipher */
        const val ENABLE_DATABASE_ENCRYPTION = true
    }

    // ===== Image Loading Configuration =====
    object ImageLoading {
        /** Default placeholder for loading state */
        const val PLACEHOLDER_RES_ID = 0 // Set to actual drawable resource ID
        
        /** Default error placeholder */
        const val ERROR_PLACEHOLDER_RES_ID = 0 // Set to actual drawable resource ID
        
        /** Enable crossfade animation when loading images */
        const val ENABLE_CROSSFADE = true
        
        /** Crossfade duration in milliseconds */
        const val CROSSFADE_DURATION_MS = 300
        
        /** Maximum number of image loading retries */
        const val MAX_IMAGE_LOAD_RETRIES = 3
    }

    // ===== UI Configuration =====
    object UI {
        /** Enable debug logging in development */
        const val DEBUG_LOGGING = true
        
        /** Pull-to-refresh enabled */
        const val ENABLE_PULL_TO_REFRESH = true
        
        /** Enable animations */
        const val ENABLE_ANIMATIONS = true
        
        /** Default animation duration in milliseconds */
        const val ANIMATION_DURATION_MS = 300L
    }

    // ===== Background Tasks Configuration =====
    object BackgroundTasks {
        /** Interval for syncing products in hours */
        const val SYNC_PRODUCTS_INTERVAL_HOURS = 6
        
        /** Interval for syncing silver prices in minutes */
        const val SYNC_PRICES_INTERVAL_MINUTES = 15
        
        /** Interval for syncing user data in hours */
        const val SYNC_USER_DATA_INTERVAL_HOURS = 1
        
        /** Enable background sync */
        const val ENABLE_BACKGROUND_SYNC = true
    }

    // ===== Database Configuration =====
    object Database {
        /** Database name */
        const val NAME = "noghre_sod.db"
        
        /** Database version (increment when schema changes) */
        const val VERSION = 1
        
        /** Enable database export for debugging */
        const val ENABLE_EXPORT_SCHEMA = true
    }

    // ===== Localization Configuration =====
    object Localization {
        /** Default language code (fa for Persian) */
        const val DEFAULT_LANGUAGE = "fa"
        
        /** Enable RTL layout for Persian */
        const val ENABLE_RTL = true
        
        /** Enable Persian number formatting */
        const val ENABLE_PERSIAN_FORMATTING = true
    }

    // ===== Analytics Configuration =====
    object Analytics {
        /** Enable analytics collection */
        const val ENABLE_ANALYTICS = true
        
        /** Enable crash reporting */
        const val ENABLE_CRASH_REPORTING = true
        
        /** Enable event logging */
        const val ENABLE_EVENT_LOGGING = true
    }
}
