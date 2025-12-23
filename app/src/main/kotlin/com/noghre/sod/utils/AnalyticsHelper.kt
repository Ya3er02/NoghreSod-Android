package com.noghre.sod.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsHelper(context: Context) {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logScreenView(screenName: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        })
    }

    fun logProductView(productId: String, productName: String, price: Long) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
            putDouble(FirebaseAnalytics.Param.VALUE, price.toDouble())
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
        })
    }

    fun logAddToCart(productId: String, productName: String, quantity: Int, price: Long) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
            putInt(FirebaseAnalytics.Param.QUANTITY, quantity)
            putDouble(FirebaseAnalytics.Param.VALUE, (price * quantity).toDouble())
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
        })
    }

    fun logRemoveFromCart(productId: String, productName: String, quantity: Int, price: Long) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART, Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
            putInt(FirebaseAnalytics.Param.QUANTITY, quantity)
            putDouble(FirebaseAnalytics.Param.VALUE, (price * quantity).toDouble())
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
        })
    }

    fun logPurchase(orderId: String, totalAmount: Long, itemCount: Int) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, Bundle().apply {
            putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
            putDouble(FirebaseAnalytics.Param.VALUE, totalAmount.toDouble())
            putInt(FirebaseAnalytics.Param.QUANTITY, itemCount)
            putString(FirebaseAnalytics.Param.CURRENCY, "IRR")
        })
    }

    fun logSearch(query: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, query)
        })
    }

    fun logLogin() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, null)
    }

    fun logSignUp() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, "email")
        })
    }
}
