package com.noghre.sod.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.noghre.sod.core.config.AppConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
 * All public APIs are safe to be called from any thread; writes are dispatched
 * onto the provided [ioDispatcher] to avoid blocking the main thread.
 *
 * GDPR/Privacy:
 * - Actual enable/disable of collection is controlled via [setCollectionEnabled]
 *   and [setCrashlyticsCollectionEnabled] which should be wired to user consent
 *   (e.g., from DataStore preferences).
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
 */
@Singleton
class AnalyticsManager @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val ioDispatcher: CoroutineDispatcher,
) {

    private val analytics: FirebaseAnalytics = firebaseAnalytics
    private val crashlytics: FirebaseCrashlytics = firebaseCrashlytics
    private val scope = CoroutineScope(ioDispatcher)

    private val currency: String = AppConfig.Pricing.CURRENCY_CODE

    // ============== Product Events ==============

    fun logProductView(
        productId: String,
        productName: String,
        category: String,
        price: Double,
        material: String? = null,
        gemType: String? = null,
    ) {
        scope.launch {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, productId)
                putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
                putDouble(FirebaseAnalytics.Param.PRICE, price)
                putString(FirebaseAnalytics.Param.CURRENCY, currency)
                material?.let { putString("material", it) }
                gemType?.let { putString("gem_type", it) }
            }
            logEventInternal("product_view", bundle)
            Timber.d("Product viewed: $productName (ID: $productId) - Price: $price $currency")
        }
    }

    fun logAddToCart(
        productId: String,
        productName: String,
        price: Double,
        quantity: Int,
        category: String? = null,
    ) {
        scope.launch {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, productId)
                putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
                putDouble(FirebaseAnalytics.Param.PRICE, price)
                putLong(FirebaseAnalytics.Param.QUANTITY, quantity.toLong())
                putDouble(FirebaseAnalytics.Param.VALUE, price * quantity)
                putString(FirebaseAnalytics.Param.CURRENCY, currency)
                category?.let { putString(FirebaseAnalytics.Param.ITEM_CATEGORY, it) }
            }
            logEventInternal(FirebaseAnalytics.Event.ADD_TO_CART, bundle)
            Timber.d("Added to cart: $productName (qty: $quantity) - Total: ${price * quantity} $currency")
        }
    }

    fun logRemoveFromCart(
        productId: String,
        productName: String,
        price: Double,
        quantity: Int,
    ) {
        scope.launch {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, productId)
                putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
                putDouble(FirebaseAnalytics.Param.PRICE, price)
                putLong(FirebaseAnalytics.Param.QUANTITY, quantity.toLong())
                putString(FirebaseAnalytics.Param.CURRENCY, currency)
            }
            logEventInternal("remove_from_cart", bundle)
            Timber.d("Removed from cart: $productName (qty: $quantity)")
        }
    }

    fun logProductFilter(
        filterType: String,
        filterValue: String,
        resultCount: Int,
    ) {
        scope.launch {
            val bundle = Bundle().apply {
                putString("filter_type", filterType)
                putString("filter_value", filterValue)
                putLong(FirebaseAnalytics.Param.ITEM_LIST_ID, resultCount.toLong())
            }
            logEventInternal("product_filter", bundle)
            Timber.d("Filter applied: $filterType = $filterValue (Results: $resultCount)")
        }
    }

    // ============== Search Events ==============

    fun logSearch(
        searchQuery: String,
        resultCount: Int,
        filters: Map<String, String>? = null,
    ) {
        scope.launch {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.SEARCH_TERM, searchQuery)
                putLong(FirebaseAnalytics.Param.ITEM_LIST_ID, resultCount.toLong())
                filters?.forEach { (key, value) ->
                    putString("filter_$key", value)
                }
            }
            logEventInternal(FirebaseAnalytics.Event.SEARCH, bundle)
            Timber.d("Search performed: '$searchQuery' (results: $resultCount)")
        }
    }

    // ============== Checkout Events ==============

    fun logBeginCheckout(
        orderId: String,
        totalPrice: Double,
        itemCount: Int,
        cartValue: Double? = null,
    ) {
        scope.launch {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
                putDouble(FirebaseAnalytics.Param.VALUE, totalPrice)
                putLong(FirebaseAnalytics.Param.ITEMS, itemCount.toLong())
                putString(FirebaseAnalytics.Param.CURRENCY, currency)
                cartValue?.let { putDouble("cart_value", it) }
            }
            logEventInternal(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)
            Timber.d("Checkout started: Order $orderId ($itemCount items) - Total: $totalPrice $currency")
        }
    }

    fun logPurchase(
        orderId: String,
        totalPrice: Double,
        itemCount: Int,
        paymentGateway: String,
        shippingCost: Double? = null,
        discountAmount: Double? = null,
    ) {
        scope.launch {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
                putDouble(FirebaseAnalytics.Param.VALUE, totalPrice)
                putLong(FirebaseAnalytics.Param.ITEMS, itemCount.toLong())
                putString("payment_gateway", paymentGateway)
                putString(FirebaseAnalytics.Param.CURRENCY, currency)
                shippingCost?.let { putDouble("shipping", it) }
                discountAmount?.let { putDouble("discount", it) }
            }
            logEventInternal(FirebaseAnalytics.Event.PURCHASE, bundle)
            Timber.i("Purchase completed: Order $orderId - $itemCount items - $totalPrice $currency via $paymentGateway")
        }
    }

    fun logPurchaseFailure(
        orderId: String,
        totalPrice: Double,
        errorReason: String,
        paymentGateway: String? = null,
    ) {
        scope.launch {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
                putDouble(FirebaseAnalytics.Param.VALUE, totalPrice)
                putString("error_reason", errorReason)
                putString(FirebaseAnalytics.Param.CURRENCY, currency)
                paymentGateway?.let { putString("payment_gateway", it) }
            }
            logEventInternal("purchase_failure", bundle)
            Timber.w("Purchase failed: Order $orderId - Reason: $errorReason")
        }
    }

    // ============== Auth Events ==============

    fun logLogin(
        userId: String,
        method: String = "phone",
        successful: Boolean = true,
    ) {
        scope.launch {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, method)
                putBoolean("successful", successful)
            }
            logEventInternal(FirebaseAnalytics.Event.LOGIN, bundle)
            if (successful) {
                setUserIdInternal(userId)
                Timber.i("User logged in: $userId (method: $method)")
            } else {
                recordExceptionInternal(
                    Exception("Login failed for method: $method"),
                    "Login attempt failed",
                    fatal = false,
                )
                Timber.w("Login failed: $userId (method: $method)")
            }
        }
    }

    fun logSignUp(
        userId: String,
        method: String = "phone",
        successful: Boolean = true,
    ) {
        scope.launch {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, method)
                putBoolean("successful", successful)
            }
            logEventInternal(FirebaseAnalytics.Event.SIGN_UP, bundle)
            if (successful) {
                setUserIdInternal(userId)
                Timber.i("User signed up: $userId (method: $method)")
            } else {
                recordExceptionInternal(
                    Exception("Signup failed for method: $method"),
                    "Signup attempt failed",
                    fatal = false,
                )
            }
        }
    }

    fun logLogout() {
        scope.launch {
            logEventInternal("logout", null)
            analytics.setUserId(null)
            crashlytics.setUserId("")
            Timber.i("User logged out")
        }
    }

    // ============== Screen Events ==============

    fun logScreenView(
        screenName: String,
        screenClass: String? = null,
    ) {
        scope.launch {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                putString(
                    FirebaseAnalytics.Param.SCREEN_CLASS,
                    screenClass ?: screenName,
                )
            }
            logEventInternal(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
            Timber.d("Screen viewed: $screenName")
        }
    }

    // ============== Error/Exception Events ==============

    fun recordException(
        throwable: Throwable,
        message: String? = null,
        fatal: Boolean = false,
    ) {
        scope.launch {
            recordExceptionInternal(throwable, message, fatal)
        }
    }

    private fun recordExceptionInternal(
        throwable: Throwable,
        message: String? = null,
        fatal: Boolean = false,
    ) {
        Timber.e(throwable, message ?: throwable.message)
        try {
            crashlytics.recordException(throwable)
            message?.let { crashlytics.log(it) }
            crashlytics.setCustomKey("fatal", fatal)

            logExceptionInternal(
                exceptionName = throwable::class.simpleName ?: "Unknown",
                description = message ?: throwable.message ?: "",
                fatal = fatal,
            )
        } catch (e: Exception) {
            Timber.e(e, "Error recording exception")
        }
    }

    fun logException(
        exceptionName: String,
        description: String,
        fatal: Boolean = false,
    ) {
        scope.launch {
            logExceptionInternal(exceptionName, description, fatal)
        }
    }

    private fun logExceptionInternal(
        exceptionName: String,
        description: String,
        fatal: Boolean = false,
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, exceptionName)
            putString(FirebaseAnalytics.Param.CONTENT, description)
            putBoolean("fatal", fatal)
        }
        logEventInternal("exception", bundle)
        Timber.e("Exception logged: $exceptionName - $description (fatal: $fatal)")
    }

    // ============== User Tracking ==============

    fun setUserId(userId: String) {
        scope.launch { setUserIdInternal(userId) }
    }

    private fun setUserIdInternal(userId: String) {
        try {
            analytics.setUserId(userId)
            crashlytics.setUserId(userId)
            Timber.i("User ID set: $userId")
        } catch (e: Exception) {
            Timber.e(e, "Error setting user ID")
        }
    }

    fun setUserProperty(
        propertyName: String,
        value: String,
    ) {
        scope.launch {
            try {
                analytics.setUserProperty(propertyName, value)
                Timber.d("User property set: $propertyName = $value")
            } catch (e: Exception) {
                Timber.e(e, "Error setting user property: $propertyName")
            }
        }
    }

    fun setCrashlyticsKey(
        key: String,
        value: String,
    ) {
        scope.launch {
            try {
                crashlytics.setCustomKey(key, value)
                Timber.d("Crashlytics key set: $key = $value")
            } catch (e: Exception) {
                Timber.e(e, "Error setting Crashlytics key: $key")
            }
        }
    }

    fun setCrashlyticsKey(
        key: String,
        value: Int,
    ) {
        scope.launch {
            try {
                crashlytics.setCustomKey(key, value)
            } catch (e: Exception) {
                Timber.e(e, "Error setting Crashlytics key: $key")
            }
        }
    }

    fun setCrashlyticsKey(
        key: String,
        value: Double,
    ) {
        scope.launch {
            try {
                crashlytics.setCustomKey(key, value)
            } catch (e: Exception) {
                Timber.e(e, "Error setting Crashlytics key: $key")
            }
        }
    }

    fun setCrashlyticsKey(
        key: String,
        value: Boolean,
    ) {
        scope.launch {
            try {
                crashlytics.setCustomKey(key, value)
            } catch (e: Exception) {
                Timber.e(e, "Error setting Crashlytics key: $key")
            }
        }
    }

    // ============== General Event Logging ==============

    fun logEvent(
        eventName: String,
        bundle: Bundle?,
    ) {
        scope.launch { logEventInternal(eventName, bundle) }
    }

    private fun logEventInternal(
        eventName: String,
        bundle: Bundle?,
    ) {
        try {
            analytics.logEvent(eventName, bundle)
            Timber.d("Event logged: $eventName")
        } catch (e: Exception) {
            Timber.e(e, "Error logging event: $eventName")
        }
    }

    fun setCollectionEnabled(enabled: Boolean) {
        scope.launch {
            try {
                analytics.setAnalyticsCollectionEnabled(enabled)
                Timber.d("Analytics collection: ${if (enabled) "enabled" else "disabled"}")
            } catch (e: Exception) {
                Timber.e(e, "Error setting analytics collection")
            }
        }
    }

    fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        scope.launch {
            try {
                crashlytics.setCrashlyticsCollectionEnabled(enabled)
                Timber.d("Crashlytics collection: ${if (enabled) "enabled" else "disabled"}")
            } catch (e: Exception) {
                Timber.e(e, "Error setting Crashlytics collection")
            }
        }
    }
}
