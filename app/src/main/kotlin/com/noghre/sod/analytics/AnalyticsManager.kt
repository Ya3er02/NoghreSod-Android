package com.noghre.sod.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Analytics Manager for tracking user events and behaviors
 * Centralizes Firebase Analytics event logging
 */
@Singleton
class AnalyticsManager @Inject constructor() {
    
    private val analytics: FirebaseAnalytics = Firebase.analytics
    
    // ============== Product Events ==============
    
    /**
     * Log product view event
     */
    fun logProductView(
        productId: String,
        productName: String,
        category: String,
        price: Double
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
            putDouble(FirebaseAnalytics.Param.PRICE, price)
        }
        logEvent("product_view", bundle)
        Timber.d("Product viewed: $productName ($productId)")
    }
    
    /**
     * Log add to cart event
     */
    fun logAddToCart(
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
        }
        logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle)
        Timber.d("Added to cart: $productName (qty: $quantity)")
    }
    
    /**
     * Log remove from cart event
     */
    fun logRemoveFromCart(
        productId: String,
        productName: String
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
        }
        logEvent("remove_from_cart", bundle)
        Timber.d("Removed from cart: $productName")
    }
    
    // ============== Search Events ==============
    
    /**
     * Log search event
     */
    fun logSearch(searchQuery: String, resultCount: Int) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, searchQuery)
            putLong(FirebaseAnalytics.Param.ITEM_LIST_ID, resultCount.toLong())
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
        itemCount: Int
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
            putDouble(FirebaseAnalytics.Param.VALUE, totalPrice)
            putLong(FirebaseAnalytics.Param.ITEMS, itemCount.toLong())
        }
        logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)
        Timber.d("Checkout started: Order $orderId (${itemCount} items)")
    }
    
    /**
     * Log purchase event
     */
    fun logPurchase(
        orderId: String,
        totalPrice: Double,
        itemCount: Int,
        paymentGateway: String
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
            putDouble(FirebaseAnalytics.Param.VALUE, totalPrice)
            putLong(FirebaseAnalytics.Param.ITEMS, itemCount.toLong())
            putString("payment_gateway", paymentGateway)
        }
        logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)
        Timber.i("Purchase completed: Order $orderId - ${itemCount} items - Price: ${totalPrice}")
    }
    
    /**
     * Log purchase failure event
     */
    fun logPurchaseFailure(
        orderId: String,
        totalPrice: Double,
        errorReason: String
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
            putDouble(FirebaseAnalytics.Param.VALUE, totalPrice)
            putString("error_reason", errorReason)
        }
        logEvent("purchase_failure", bundle)
        Timber.w("Purchase failed: Order $orderId - Reason: $errorReason")
    }
    
    // ============== Auth Events ==============
    
    /**
     * Log login event
     */
    fun logLogin(userId: String, method: String = "phone") {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)  // phone, otp, etc
        }
        logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        analytics.setUserId(userId)
        Timber.i("User logged in: $userId ($method)")
    }
    
    /**
     * Log signup event
     */
    fun logSignUp(userId: String, method: String = "phone") {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
        analytics.setUserId(userId)
        Timber.i("User signed up: $userId ($method)")
    }
    
    /**
     * Log logout event
     */
    fun logLogout() {
        logEvent("logout", null)
        analytics.setUserId(null)
        Timber.i("User logged out")
    }
    
    // ============== Screen Events ==============
    
    /**
     * Log screen view
     */
    fun logScreenView(screenName: String, screenClass: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        Timber.d("Screen viewed: $screenName")
    }
    
    // ============== Error/Exception Events ==============
    
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
    
    // ============== General Event Logging ==============
    
    /**
     * Log custom event
     */
    fun logEvent(eventName: String, bundle: Bundle?) {
        try {
            if (bundle != null) {
                analytics.logEvent(eventName, bundle)
            } else {
                analytics.logEvent(eventName, null)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error logging event: $eventName")
        }
    }
    
    /**
     * Set user property
     */
    fun setUserProperty(propertyName: String, value: String) {
        try {
            analytics.setUserProperty(propertyName, value)
            Timber.d("User property set: $propertyName = $value")
        } catch (e: Exception) {
            Timber.e(e, "Error setting user property: $propertyName")
        }
    }
    
    /**
     * Enable/Disable analytics collection
     */
    fun setCollectionEnabled(enabled: Boolean) {
        try {
            analytics.setAnalyticsCollectionEnabled(enabled)
            Timber.d("Analytics collection: ${if (enabled) "enabled" else "disabled"}")
        } catch (e: Exception) {
            Timber.e(e, "Error setting analytics collection")
        }
    }
}