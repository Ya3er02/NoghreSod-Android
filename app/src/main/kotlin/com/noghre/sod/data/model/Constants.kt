package com.noghre.sod.data.model

/**
 * Constants for Data Layer.
 */
object Constants {

    // API Configuration
    const val API_BASE_URL = "https://api.noghresod.com/api/"
    const val API_TIMEOUT_SECONDS = 30L

    // Database Configuration
    const val DATABASE_NAME = "noghre_sod.db"

    // Cache Configuration
    const val CACHE_EXPIRY_MINUTES = 5
    const val CACHE_EXPIRY_MILLIS = CACHE_EXPIRY_MINUTES * 60 * 1000L
    const val MAX_PRODUCTS_CACHE = 50
    const val MAX_CATEGORIES_CACHE = 30

    // SharedPreferences Configuration
    const val SECURE_PREFS_NAME = "noghre_sod_secure_prefs"

    // Token Configuration
    const val TOKEN_EXPIRY_BUFFER_SECONDS = 300L // 5 minutes buffer

    // Payment Methods (Iran-specific)
    object PaymentMethods {
        const val ZARINPAL = "zarinpal"
        const val IPG = "ipg"
        const val PAY_IR = "pay_ir"
        const val ON_DELIVERY = "on_delivery"
    }

    // Order Status
    object OrderStatus {
        const val PENDING = "PENDING"
        const val CONFIRMED = "CONFIRMED"
        const val SHIPPED = "SHIPPED"
        const val DELIVERED = "DELIVERED"
        const val CANCELLED = "CANCELLED"
    }

    // Phone validation (Iran format: 09XXXXXXXXX)
    const val PHONE_PATTERN = "^09\\d{9}$"
    const val PHONE_LENGTH = 11

    // Currency
    const val CURRENCY_CODE = "IRR"
    const val CURRENCY_SYMBOL = "تومان"
}
