package com.noghre.sod.analytics

/**
 * Constants for analytics events.
 * Use with AnalyticsHelper to track user interactions.
 *
 * Example:
 * ```
 * analyticsHelper.logEvent(AnalyticsEvents.PRODUCT_VIEW, mapOf(
 *     "product_id" to productId,
 *     "category" to category
 * ))
 * ```
 */
object AnalyticsEvents {
    // Screen Views
    const val SCREEN_HOME = "home_screen"
    const val SCREEN_PRODUCT_DETAIL = "product_detail_screen"
    const val SCREEN_PRODUCT_LIST = "product_list_screen"
    const val SCREEN_CART = "cart_screen"
    const val SCREEN_CHECKOUT = "checkout_screen"
    const val SCREEN_PROFILE = "profile_screen"
    const val SCREEN_SETTINGS = "settings_screen"
    const val SCREEN_FAVORITES = "favorites_screen"

    // User Actions
    const val PRODUCT_VIEW = "product_view"
    const val PRODUCT_ADD_TO_CART = "add_to_cart"
    const val PRODUCT_REMOVE_FROM_CART = "remove_from_cart"
    const val PRODUCT_UPDATE_QUANTITY = "update_quantity"
    const val PRODUCT_SEARCH = "product_search"
    const val PRODUCT_FILTER = "product_filter"
    const val PRODUCT_WISHLIST_ADD = "wishlist_add"
    const val PRODUCT_WISHLIST_REMOVE = "wishlist_remove"

    // Checkout
    const val CHECKOUT_START = "checkout_start"
    const val CHECKOUT_COMPLETE = "checkout_complete"
    const val CHECKOUT_ABANDONED = "checkout_abandoned"
    const val PAYMENT_METHOD_SELECT = "payment_method_select"
    const val DISCOUNT_APPLIED = "discount_applied"

    // Authentication
    const val LOGIN = "login"
    const val LOGIN_FAILED = "login_failed"
    const val SIGNUP = "signup"
    const val SIGNUP_FAILED = "signup_failed"
    const val LOGOUT = "logout"
    const val PASSWORD_RESET = "password_reset"

    // Order
    const val ORDER_PLACED = "order_placed"
    const val ORDER_CANCELLED = "order_cancelled"
    const val ORDER_SHIPPED = "order_shipped"
    const val ORDER_DELIVERED = "order_delivered"
    const val ORDER_RETURNED = "order_returned"

    // Errors
    const val ERROR_NETWORK = "error_network"
    const val ERROR_API = "error_api"
    const val ERROR_PAYMENT = "error_payment"
    const val ERROR_UNKNOWN = "error_unknown"

    // App
    const val APP_LAUNCH = "app_launch"
    const val APP_BACKGROUND = "app_background"
    const val APP_CRASH = "app_crash"
}
