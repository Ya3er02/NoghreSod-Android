package com.noghre.sod.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for Firebase Analytics events and tracking.
 *
 * Tracks user interactions, purchases, errors, and app performance.
 * All events are automatically sent to Firebase console.
 *
 * @param firebaseAnalytics Firebase Analytics instance
 */
@Singleton
class FirebaseAnalyticsManager @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    /**
     * Track product view event.
     *
     * @param productId Product ID
     * @param productName Product name
     * @param price Product price
     * @param currency Currency code (e.g., IRR)
     */
    fun trackProductView(
        productId: String,
        productName: String,
        price: Float,
        currency: String = "IRR"
    ) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
            putDouble(FirebaseAnalytics.Param.PRICE, price.toDouble())
            putString(FirebaseAnalytics.Param.CURRENCY, currency)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "product")
        })
    }

    /**
     * Track add to cart event.
     *
     * @param productId Product ID
     * @param productName Product name
     * @param price Product price
     * @param quantity Quantity added
     */
    fun trackAddToCart(
        productId: String,
        productName: String,
        price: Float,
        quantity: Int = 1
    ) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
            putDouble(FirebaseAnalytics.Param.PRICE, price.toDouble())
            putLong(FirebaseAnalytics.Param.QUANTITY, quantity.toLong())
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
        })
    }

    /**
     * Track remove from cart event.
     *
     * @param productId Product ID
     * @param productName Product name
     * @param price Product price
     */
    fun trackRemoveFromCart(
        productId: String,
        productName: String,
        price: Float
    ) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART, Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
            putDouble(FirebaseAnalytics.Param.PRICE, price.toDouble())
        })
    }

    /**
     * Track purchase event.
     *
     * @param orderId Order ID
     * @param value Total order value
     * @param tax Tax amount
     * @param shipping Shipping cost
     * @param items List of purchased items
     */
    fun trackPurchase(
        orderId: String,
        value: Float,
        tax: Float = 0f,
        shipping: Float = 0f,
        items: List<Map<String, Any>> = emptyList()
    ) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, Bundle().apply {
            putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
            putDouble(FirebaseAnalytics.Param.VALUE, value.toDouble())
            putDouble(FirebaseAnalytics.Param.TAX, tax.toDouble())
            putDouble(FirebaseAnalytics.Param.SHIPPING, shipping.toDouble())
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
            putInt(FirebaseAnalytics.Param.ITEMS, items.size)
        })
    }

    /**
     * Track checkout process.
     *
     * @param step Checkout step (1=shipping, 2=payment, 3=review, 4=confirmation)
     * @param cartValue Total cart value
     */
    fun trackCheckout(step: Int, cartValue: Float) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, Bundle().apply {
            putInt(FirebaseAnalytics.Param.CHECKOUT_STEP, step)
            putDouble(FirebaseAnalytics.Param.VALUE, cartValue.toDouble())
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
        })
    }

    /**
     * Track search event.
     *
     * @param searchQuery Search query text
     * @param resultCount Number of results found
     */
    fun trackSearch(searchQuery: String, resultCount: Int) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, searchQuery)
            putInt("result_count", resultCount)
        })
    }

    /**
     * Track user login.
     *
     * @param method Login method (email, phone, google, apple, etc)
     */
    fun trackLogin(method: String = "email") {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        })
    }

    /**
     * Track user signup.
     *
     * @param method Signup method (email, phone, google, apple, etc)
     */
    fun trackSignUp(method: String = "email") {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        })
    }

    /**
     * Track screen view.
     *
     * @param screenName Screen name
     * @param screenClass Screen class name
     */
    fun trackScreenView(screenName: String, screenClass: String? = null) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            if (screenClass != null) {
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
            }
        })
    }

    /**
     * Track error event.
     *
     * @param errorCode Error code
     * @param errorMessage Error message
     */
    fun trackError(errorCode: String, errorMessage: String) {
        firebaseAnalytics.logEvent("app_error", Bundle().apply {
            putString("error_code", errorCode)
            putString("error_message", errorMessage)
        })
    }

    /**
     * Track network error.
     *
     * @param httpCode HTTP status code
     * @param message Error message
     */
    fun trackNetworkError(httpCode: Int, message: String) {
        firebaseAnalytics.logEvent("network_error", Bundle().apply {
            putInt("http_code", httpCode)
            putString("message", message)
        })
    }

    /**
     * Track offline event.
     *
     * @param operationType Type of operation (ADD_TO_CART, REMOVE_FROM_CART, etc)
     */
    fun trackOfflineOperation(operationType: String) {
        firebaseAnalytics.logEvent("offline_operation", Bundle().apply {
            putString("operation_type", operationType)
        })
    }

    /**
     * Track sync event.
     *
     * @param operationCount Number of operations synced
     * @param successCount Number of successful operations
     * @param failureCount Number of failed operations
     */
    fun trackSync(operationCount: Int, successCount: Int, failureCount: Int) {
        firebaseAnalytics.logEvent("sync_completed", Bundle().apply {
            putInt("total_operations", operationCount)
            putInt("successful", successCount)
            putInt("failed", failureCount)
        })
    }

    /**
     * Track coupon applied.
     *
     * @param couponCode Coupon code
     * @param discount Discount amount
     */
    fun trackCouponApplied(couponCode: String, discount: Float) {
        firebaseAnalytics.logEvent("coupon_applied", Bundle().apply {
            putString("coupon_code", couponCode)
            putDouble("discount", discount.toDouble())
        })
    }

    /**
     * Track favorite toggled.
     *
     * @param productId Product ID
     * @param isFavorite Is now favorite
     */
    fun trackFavoriteToggled(productId: String, isFavorite: Boolean) {
        firebaseAnalytics.logEvent("favorite_toggled", Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putBoolean("is_favorite", isFavorite)
        })
    }

    /**
     * Set user properties (for segmentation and personalization).
     *
     * @param userId User ID
     * @param locale User locale (e.g., fa_IR)
     */
    fun setUserProperties(userId: String, locale: String = "fa_IR") {
        firebaseAnalytics.setUserId(userId)
        firebaseAnalytics.setUserProperty("locale", locale)
        firebaseAnalytics.setUserProperty("app_language", "Persian")
    }

    /**
     * Clear user properties (on logout).
     */
    fun clearUserProperties() {
        firebaseAnalytics.setUserId(null)
    }
}

/**
 * Event names constant definitions.
 */
object AnalyticsEvents {
    const val PRODUCT_VIEW = "product_view"
    const val ADD_TO_CART = "add_to_cart"
    const val REMOVE_FROM_CART = "remove_from_cart"
    const val PURCHASE = "purchase"
    const val CHECKOUT = "begin_checkout"
    const val SEARCH = "search"
    const val LOGIN = "login"
    const val SIGNUP = "sign_up"
    const val SCREEN_VIEW = "screen_view"
    const val ERROR = "app_error"
    const val NETWORK_ERROR = "network_error"
    const val OFFLINE_OPERATION = "offline_operation"
    const val SYNC_COMPLETED = "sync_completed"
    const val COUPON_APPLIED = "coupon_applied"
    const val FAVORITE_TOGGLED = "favorite_toggled"
}
