package com.noghre.sod.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized Analytics & Crash Reporting Manager for NoghreSod.
 *
 * Integrates Firebase Analytics and Crashlytics for comprehensive user tracking,
 * event logging, and error monitoring.
 *
 * Features:
 * - Product tracking (views, add-to-cart, searches, filters)
 * - E-commerce tracking (checkout, purchases, cart operations)
 * - User authentication tracking (login, signup, logout)
 * - Exception and error reporting with Crashlytics
 * - User property tracking for segmentation
 * - Screen view tracking for navigation analytics
 *
 * Usage:
 * ```
 * @Inject
 * lateinit var analyticsManager: AnalyticsManager
 *
 * // Log product view
 * analyticsManager.logProductView(productId, name, category, price)
 *
 * // Log purchase
 * analyticsManager.logPurchase(orderId, totalPrice, itemCount, gateway)
 *
 * // Track errors
 * analyticsManager.recordException(throwable, "Payment failed", fatal = false)
 * ```
 *
 * @author NoghreSod Team
 * @version 2.0.0
 */
@Singleton
class AnalyticsManager @Inject constructor() {

    private val analytics: FirebaseAnalytics = Firebase.analytics
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    // ============== Product Events ==============

    /**
     * Log product view event
     *
     * @param productId Product identifier
     * @param productName Product name
     * @param category Product category (e.g., rings, necklaces)
     * @param price Product price in IRR
     * @param material Material type (e.g., 925 silver)
     * @param gemType Gem/stone type if applicable
     */
    fun logProductView(
        productId: String,
        productName: String,
        category: String,
        price: Double,
        material: String? = null,
        gemType: String? = null
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
            putDouble(FirebaseAnalytics.Param.PRICE, price)
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
            material?.let { putString("material", it) }
            gemType?.let { putString("gem_type", it) }
        }
        logEvent("product_view", bundle)
        Timber.d("Product viewed: $productName (ID: $productId) - Price: $price IRR")
    }

    /**
     * Log add to cart event
     */
    fun logAddToCart(
        productId: String,
        productName: String,
        price: Double,
        quantity: Int,
        category: String? = null
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
            putDouble(FirebaseAnalytics.Param.PRICE, price)
            putLong(FirebaseAnalytics.Param.QUANTITY, quantity.toLong())
            putDouble(FirebaseAnalytics.Param.VALUE, price * quantity)
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
            category?.let { putString(FirebaseAnalytics.Param.ITEM_CATEGORY, it) }
        }
        logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle)
        Timber.d("Added to cart: $productName (qty: $quantity) - Total: ${price * quantity} IRR")
    }

    /**
     * Log remove from cart event
     */
    fun logRemoveFromCart(
        productId: String,
        productName: String,
        price: Double,
        quantity: Int
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
            putDouble(FirebaseAnalytics.Param.PRICE, price)
            putLong(FirebaseAnalytics.Param.QUANTITY, quantity.toLong())
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
        }
        logEvent("remove_from_cart", bundle)
        Timber.d("Removed from cart: $productName")
    }

    /**
     * Log product filter applied event
     */
    fun logProductFilter(
        filterType: String,
        filterValue: String,
        resultCount: Int
    ) {
        val bundle = Bundle().apply {
            putString("filter_type", filterType)
            putString("filter_value", filterValue)
            putLong(FirebaseAnalytics.Param.ITEM_LIST_ID, resultCount.toLong())
        }
        logEvent("product_filter", bundle)
        Timber.d("Filter applied: $filterType = $filterValue (Results: $resultCount)")
    }

    // ============== Search Events ==============

    /**
     * Log search event
     */
    fun logSearch(
        searchQuery: String,
        resultCount: Int,
        filters: Map<String, String>? = null
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, searchQuery)
            putLong(FirebaseAnalytics.Param.ITEM_LIST_ID, resultCount.toLong())
            filters?.forEach { (key, value) ->
                putString("filter_$key", value)
            }
        }
        logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        Timber.d("Search performed: '$searchQuery' (results: $resultCount)")
    }

    // ============== Checkout Events ==============

    /**
     * Log begin checkout event
     */
    fun logBeginCheckout(
        orderId: String,
        totalPrice: Double,
        itemCount: Int,
        cartValue: Double? = null
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
            putDouble(FirebaseAnalytics.Param.VALUE, totalPrice)
            putLong(FirebaseAnalytics.Param.ITEMS, itemCount.toLong())
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
            cartValue?.let { putDouble("cart_value", it) }
        }
        logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)
        Timber.d("Checkout started: Order $orderId ($itemCount items) - Total: $totalPrice IRR")
    }

    /**
     * Log purchase event
     */
    fun logPurchase(
        orderId: String,
        totalPrice: Double,
        itemCount: Int,
        paymentGateway: String,
        shippingCost: Double? = null,
        discountAmount: Double? = null
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
            putDouble(FirebaseAnalytics.Param.VALUE, totalPrice)
            putLong(FirebaseAnalytics.Param.ITEMS, itemCount.toLong())
            putString("payment_gateway", paymentGateway)
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
            shippingCost?.let { putDouble("shipping", it) }
            discountAmount?.let { putDouble("discount", it) }
        }
        logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)
        Timber.i("Purchase completed: Order $orderId - $itemCount items - $totalPrice IRR via $paymentGateway")
    }

    /**
     * Log purchase failure event
     */
    fun logPurchaseFailure(
        orderId: String,
        totalPrice: Double,
        errorReason: String,
        paymentGateway: String? = null
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
            putDouble(FirebaseAnalytics.Param.VALUE, totalPrice)
            putString("error_reason", errorReason)
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
            paymentGateway?.let { putString("payment_gateway", it) }
        }
        logEvent("purchase_failure", bundle)
        Timber.w("Purchase failed: Order $orderId - Reason: $errorReason")
    }

    // ============== Auth Events ==============

    /**
     * Log login event
     *
     * @param userId User identifier
     * @param method Login method (phone, email, otp, etc)
     * @param successful Whether login was successful
     */
    fun logLogin(
        userId: String,
        method: String = "phone",
        successful: Boolean = true
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
            putBoolean("successful", successful)
        }
        logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        if (successful) {
            setUserId(userId)
            Timber.i("User logged in: $userId (method: $method)")
        } else {
            recordException(
                Exception("Login failed for method: $method"),
                "Login attempt failed"
            )
            Timber.w("Login failed: $userId (method: $method)")
        }
    }

    /**
     * Log signup event
     */
    fun logSignUp(
        userId: String,
        method: String = "phone",
        successful: Boolean = true
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
            putBoolean("successful", successful)
        }
        logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
        if (successful) {
            setUserId(userId)
            Timber.i("User signed up: $userId (method: $method)")
        } else {
            recordException(
                Exception("Signup failed for method: $method"),
                "Signup attempt failed"
            )
        }
    }

    /**
     * Log logout event
     */
    fun logLogout() {
        logEvent("logout", null)
        analytics.setUserId(null)
        crashlytics.setUserId("")
        Timber.i("User logged out")
    }

    // ============== Screen Events ==============

    /**
     * Log screen view
     */
    fun logScreenView(
        screenName: String,
        screenClass: String? = null
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(
                FirebaseAnalytics.Param.SCREEN_CLASS,
                screenClass ?: screenName
            )
        }
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        Timber.d("Screen viewed: $screenName")
    }

    // ============== Error/Exception Events ==============

    /**
     * Record exception with Crashlytics and Analytics.
     *
     * This method logs the exception to both Firebase Crashlytics (for crash reporting)
     * and Firebase Analytics (for user behavior tracking).
     *
     * @param throwable Exception to record
     * @param message Custom error message (optional)
     * @param fatal Whether the exception is fatal (causes app crash)
     */
    fun recordException(
        throwable: Throwable,
        message: String? = null,
        fatal: Boolean = false
    ) {
        Timber.e(throwable, message ?: throwable.message)
        try {
            // Crashlytics
            crashlytics.recordException(throwable)
            message?.let { crashlytics.log(it) }
            crashlytics.setCustomKey("fatal", fatal)

            // Analytics
            logException(
                exceptionName = throwable::class.simpleName ?: "Unknown",
                description = message ?: throwable.message ?: "",
                fatal = fatal
            )
        } catch (e: Exception) {
            Timber.e(e, "Error recording exception")
        }
    }

    /**
     * Log custom exception
     */
    fun logException(
        exceptionName: String,
        description: String,
        fatal: Boolean = false
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, exceptionName)
            putString(FirebaseAnalytics.Param.CONTENT, description)
            putBoolean("fatal", fatal)
        }
        logEvent("exception", bundle)
        Timber.e("Exception logged: $exceptionName - $description (fatal: $fatal)")
    }

    // ============== User Tracking ==============

    /**
     * Set user identifier for both Analytics and Crashlytics.
     *
     * Call this after successful login/signup to track user journey.
     * Call with empty string after logout.
     *
     * @param userId Unique user identifier
     */
    fun setUserId(userId: String) {
        try {
            analytics.setUserId(userId)
            crashlytics.setUserId(userId)
            Timber.i("User ID set: $userId")
        } catch (e: Exception) {
            Timber.e(e, "Error setting user ID")
        }
    }

    /**
     * Set user property for Analytics segmentation.
     *
     * Used for segmenting users by attributes like tier, region, etc.
     *
     * @param propertyName Property name
     * @param value Property value
     */
    fun setUserProperty(
        propertyName: String,
        value: String
    ) {
        try {
            analytics.setUserProperty(propertyName, value)
            Timber.d("User property set: $propertyName = $value")
        } catch (e: Exception) {
            Timber.e(e, "Error setting user property: $propertyName")
        }
    }

    /**
     * Add custom key-value pair to all crash reports.
     *
     * Useful for tracking app state at time of crash.
     *
     * @param key Custom key
     * @param value Custom value
     */
    fun setCrashlyticsKey(
        key: String,
        value: String
    ) {
        try {
            crashlytics.setCustomKey(key, value)
            Timber.d("Crashlytics key set: $key = $value")
        } catch (e: Exception) {
            Timber.e(e, "Error setting Crashlytics key: $key")
        }
    }

    /**
     * Add custom numeric key-value pair to crash reports.
     */
    fun setCrashlyticsKey(
        key: String,
        value: Int
    ) {
        try {
            crashlytics.setCustomKey(key, value)
        } catch (e: Exception) {
            Timber.e(e, "Error setting Crashlytics key: $key")
        }
    }

    /**
     * Add custom numeric key-value pair to crash reports.
     */
    fun setCrashlyticsKey(
        key: String,
        value: Double
    ) {
        try {
            crashlytics.setCustomKey(key, value)
        } catch (e: Exception) {
            Timber.e(e, "Error setting Crashlytics key: $key")
        }
    }

    /**
     * Add custom boolean key-value pair to crash reports.
     */
    fun setCrashlyticsKey(
        key: String,
        value: Boolean
    ) {
        try {
            crashlytics.setCustomKey(key, value)
        } catch (e: Exception) {
            Timber.e(e, "Error setting Crashlytics key: $key")
        }
    }

    // ============== General Event Logging ==============

    /**
     * Log custom event with Bundle.
     *
     * Low-level method for logging custom events. Consider using
     * specialized methods (logProductView, logPurchase, etc) when available.
     *
     * @param eventName Event name
     * @param bundle Event data bundle
     */
    fun logEvent(
        eventName: String,
        bundle: Bundle?
    ) {
        try {
            analytics.logEvent(eventName, bundle)
            Timber.d("Event logged: $eventName")
        } catch (e: Exception) {
            Timber.e(e, "Error logging event: $eventName")
        }
    }

    /**
     * Enable/Disable analytics collection.
     *
     * Useful for respecting user privacy preferences.
     *
     * @param enabled Whether to enable analytics collection
     */
    fun setCollectionEnabled(enabled: Boolean) {
        try {
            analytics.setAnalyticsCollectionEnabled(enabled)
            Timber.d("Analytics collection: ${if (enabled) "enabled" else "disabled"}")
        } catch (e: Exception) {
            Timber.e(e, "Error setting analytics collection")
        }
    }

    /**
     * Enable/Disable Crashlytics collection.
     *
     * Useful for respecting user privacy preferences.
     *
     * @param enabled Whether to enable Crashlytics collection
     */
    fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        try {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(enabled)
            Timber.d("Crashlytics collection: ${if (enabled) "enabled" else "disabled"}")
        } catch (e: Exception) {
            Timber.e(e, "Error setting Crashlytics collection")
        }
    }
}
