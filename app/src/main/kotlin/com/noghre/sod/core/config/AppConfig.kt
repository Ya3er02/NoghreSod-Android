package com.noghre.sod.core.config

/**
 * Centralized application configuration object.
 * Contains all app constants, API settings, and feature flags.
 *
 * All values are compile-time constants (const val) for performance.
 */
object AppConfig {

    // ============ API Configuration ============
    object Api {
        // Base URLs for different environments
        const val BASE_URL = "https://api.noghresod.com"
        const val BASE_URL_DEV = "http://192.168.1.100:3000" // Local development

        // Network timeouts (in seconds)
        const val CONNECT_TIMEOUT = 30L
        const val READ_TIMEOUT = 30L
        const val WRITE_TIMEOUT = 30L

        // Retry policy
        const val MAX_RETRIES = 3
        const val INITIAL_RETRY_DELAY_MS = 1000L

        // Request/Response settings
        const val CACHE_SIZE_MB = 100L
        const val CACHE_VALIDITY_DAYS = 7
    }

    // ============ Pagination Configuration ============
    object Pagination {
        const val DEFAULT_PAGE_SIZE = 20
        const val MIN_PAGE_SIZE = 10
        const val MAX_PAGE_SIZE = 100
        const val INITIAL_LOAD_SIZE = 20
        const val PREFETCH_DISTANCE = 5
    }

    // ============ Cache Configuration ============
    object Cache {
        // Cache validity periods (in hours)
        const val PRODUCTS_CACHE_HOURS = 24
        const val CATEGORIES_CACHE_HOURS = 48
        const val PRICES_CACHE_MINUTES = 5
        const val USER_CACHE_HOURS = 1

        // Cache sizes
        const val MAX_CACHE_SIZE_MB = 100L
    }

    // ============ Authentication Configuration ============
    object Auth {
        // Token settings
        const val TOKEN_EXPIRY_BUFFER_MINUTES = 5 // Refresh 5 mins before expiry
        const val MAX_REFRESH_ATTEMPTS = 3
        const val REFRESH_RETRY_DELAY_MS = 500L

        // Session settings
        const val AUTO_LOGOUT_AFTER_MINUTES = 30 // Logout after 30 mins of inactivity
    }

    // ============ Image Loading Configuration ============
    object Images {
        const val PLACEHOLDER_FADE_DURATION_MS = 300
        const val MEMORY_CACHE_SIZE_MB = 32
        const val DISK_CACHE_SIZE_MB = 256
        const val DEFAULT_IMAGE_WIDTH = 300
        const val DEFAULT_IMAGE_HEIGHT = 300
    }

    // ============ Pricing Configuration ============
    object Pricing {
        // Price refresh interval (in minutes)
        const val PRICE_REFRESH_INTERVAL_MINUTES = 5
        // Price decimal places for display
        const val PRICE_DECIMAL_PLACES = 2
        // Currency code for analytics and pricing display
        const val CURRENCY_CODE = "IRR"
    }

    // ============ Security Configuration ============
    object Security {
        // Certificate pinning
        const val ENABLE_SSL_PINNING = true
        // Public key hash for SSL pinning
        const val SSL_PIN_HASH = "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="

        // Database encryption
        const val ENABLE_DATABASE_ENCRYPTION = true
    }

    // ============ Feature Flags ============
    object Features {
        const val ENABLE_ANALYTICS = true
        const val ENABLE_CRASH_REPORTING = true
        const val ENABLE_OFFLINE_MODE = true
        const val ENABLE_BIOMETRIC_AUTH = false // Will be enabled later
        const val ENABLE_PUSH_NOTIFICATIONS = false // Will be enabled later
    }

    // ============ Logging Configuration ============
    object Logging {
        const val ENABLE_NETWORK_LOGGING = true // Set to false in release
        const val ENABLE_DATABASE_LOGGING = false
        const val LOG_TAG = "NoghreSod"
    }

    // ============ Database Configuration ============
    object Database {
        const val DATABASE_NAME = "noghresod.db"
        const val DATABASE_VERSION = 1
        const val ENABLE_WAL = true // Write-Ahead Logging
    }

    // ============ Analytics Configuration ============
    object Analytics {
        // Max number of events to queue before dropping oldest
        const val MAX_QUEUE_SIZE = 500
        // Number of events to process in a batch
        const val BATCH_SIZE = 50
        // Maximum number of retry attempts for failed events
        const val MAX_RETRY_ATTEMPTS = 3
        // SharedFlow replay size for event monitoring
        const val EVENT_REPLAY_SIZE = 100
    }
}
