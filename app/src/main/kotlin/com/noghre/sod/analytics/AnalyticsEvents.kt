package com.noghre.sod.analytics

/**
 * Constants for analytics events in NoghreSod.
 *
 * Use with AnalyticsManager to track user interactions and behaviors.
 * All events should use constants defined here for consistency and type-safety.
 *
 * Example:
 * ```
 * analyticsManager.logEvent(AnalyticsEvents.PRODUCT_VIEW, mapOf(
 *     AnalyticsEvents.Params.PRODUCT_ID to productId,
 *     AnalyticsEvents.Params.CATEGORY to category
 * ))
 *
 * // Or use specialized methods:
 * analyticsManager.logProductView(productId, name, category, price)
 * ```
 *
 * @author NoghreSod Team
 * @version 1.1.0
 */
object AnalyticsEvents {

    // ============== Screen Views ==============
    const val SCREEN_HOME = "home_screen"
    const val SCREEN_PRODUCT_DETAIL = "product_detail_screen"
    const val SCREEN_PRODUCT_LIST = "product_list_screen"
    const val SCREEN_CART = "cart_screen"
    const val SCREEN_CHECKOUT = "checkout_screen"
    const val SCREEN_PROFILE = "profile_screen"
    const val SCREEN_SETTINGS = "settings_screen"
    const val SCREEN_FAVORITES = "favorites_screen"
    const val SCREEN_ORDER_HISTORY = "order_history_screen"
    const val SCREEN_ADDRESS = "address_screen"
    const val SCREEN_PAYMENT = "payment_screen"
    const val SCREEN_WISHLIST = "wishlist_screen"

    // ============== Product Actions ==============
    const val PRODUCT_VIEW = "product_view"
    const val PRODUCT_ADD_TO_CART = "add_to_cart"
    const val PRODUCT_REMOVE_FROM_CART = "remove_from_cart"
    const val PRODUCT_UPDATE_QUANTITY = "update_quantity"
    const val PRODUCT_SEARCH = "product_search"
    const val PRODUCT_FILTER = "product_filter"
    const val PRODUCT_WISHLIST_ADD = "wishlist_add"
    const val PRODUCT_WISHLIST_REMOVE = "wishlist_remove"
    const val PRODUCT_SHARE = "product_share"
    const val PRODUCT_REVIEW_SUBMIT = "review_submit"
    const val PRODUCT_RATING_SUBMIT = "rating_submit"

    // ============== Jewelry-Specific Events ==============
    const val RING_SIZER_OPENED = "ring_sizer_opened"
    const val RING_SIZER_MEASURED = "ring_sizer_measured"
    const val RING_SIZER_RESULT = "ring_sizer_result"
    const val PRODUCT_ZOOM_USED = "product_zoom_used"
    const val HALLMARK_VIEWED = "hallmark_viewed"
    const val GEM_INSPECTION = "gem_inspection"
    const val MATERIAL_COMPARED = "material_compared"

    // ============== Checkout Events ==============
    const val CHECKOUT_START = "checkout_start"
    const val CHECKOUT_STEP = "checkout_step"
    const val CHECKOUT_COMPLETE = "checkout_complete"
    const val CHECKOUT_ABANDONED = "checkout_abandoned"
    const val PAYMENT_METHOD_SELECT = "payment_method_select"
    const val PAYMENT_METHOD_FAILED = "payment_method_failed"
    const val DISCOUNT_APPLIED = "discount_applied"
    const val COUPON_APPLIED = "coupon_applied"
    const val SHIPPING_METHOD_SELECT = "shipping_method_select"
    const val PROMO_CODE_ENTERED = "promo_code_entered"

    // ============== Authentication ==============
    const val LOGIN = "login"
    const val LOGIN_FAILED = "login_failed"
    const val SIGNUP = "signup"
    const val SIGNUP_FAILED = "signup_failed"
    const val LOGOUT = "logout"
    const val PASSWORD_RESET = "password_reset"
    const val PASSWORD_RESET_SUCCESS = "password_reset_success"
    const val PHONE_VERIFICATION = "phone_verification"
    const val OTP_SENT = "otp_sent"
    const val OTP_VERIFIED = "otp_verified"
    const val OTP_FAILED = "otp_failed"

    // ============== Order Events ==============
    const val ORDER_PLACED = "order_placed"
    const val ORDER_CANCELLED = "order_cancelled"
    const val ORDER_SHIPPED = "order_shipped"
    const val ORDER_DELIVERED = "order_delivered"
    const val ORDER_RETURNED = "order_returned"
    const val ORDER_RETURN_APPROVED = "order_return_approved"
    const val ORDER_RETURN_REJECTED = "order_return_rejected"
    const val ORDER_TRACKED = "order_tracked"
    const val ORDER_REFUND_INITIATED = "order_refund_initiated"
    const val ORDER_REFUND_COMPLETED = "order_refund_completed"

    // ============== Error Events ==============
    const val ERROR_NETWORK = "error_network"
    const val ERROR_API = "error_api"
    const val ERROR_PAYMENT = "error_payment"
    const val ERROR_VALIDATION = "error_validation"
    const val ERROR_UNKNOWN = "error_unknown"
    const val ERROR_TIMEOUT = "error_timeout"
    const val ERROR_AUTHENTICATION = "error_authentication"
    const val ERROR_PERMISSION = "error_permission"

    // ============== App Events ==============
    const val APP_LAUNCH = "app_launch"
    const val APP_FOREGROUND = "app_foreground"
    const val APP_BACKGROUND = "app_background"
    const val APP_CRASH = "app_crash"
    const val APP_UPDATE_AVAILABLE = "app_update_available"
    const val APP_UPDATE_INSTALLED = "app_update_installed"

    // ============== User Engagement ==============
    const val USER_PROPERTIES_UPDATED = "user_properties_updated"
    const val ADDRESS_ADDED = "address_added"
    const val ADDRESS_UPDATED = "address_updated"
    const val ADDRESS_DELETED = "address_deleted"
    const val PAYMENT_METHOD_ADDED = "payment_method_added"
    const val PAYMENT_METHOD_REMOVED = "payment_method_removed"
    const val NOTIFICATION_ENABLED = "notification_enabled"
    const val NOTIFICATION_DISABLED = "notification_disabled"
    const val LANGUAGE_CHANGED = "language_changed"
    const val THEME_CHANGED = "theme_changed"

    // ============== Price & Notification Events ==============
    const val PRICE_ALERT_SET = "price_alert_set"
    const val PRICE_ALERT_TRIGGERED = "price_alert_triggered"
    const val STOCK_ALERT_SET = "stock_alert_set"
    const val STOCK_ALERT_TRIGGERED = "stock_alert_triggered"
    const val NOTIFICATION_CLICKED = "notification_clicked"

    // ============== Parameter Constants ==============
    /**
     * Standard parameter names for analytics events.
     * Use these to ensure consistency across all events.
     */
    object Params {
        const val PRODUCT_ID = "product_id"
        const val PRODUCT_NAME = "product_name"
        const val PRODUCT_CATEGORY = "category"
        const val PRODUCT_PRICE = "price"
        const val QUANTITY = "quantity"
        const val MATERIAL = "material"
        const val GEM_TYPE = "gem_type"
        const val HALLMARK = "hallmark"
        const val ORDER_ID = "order_id"
        const val ORDER_VALUE = "order_value"
        const val ITEM_COUNT = "item_count"
        const val PAYMENT_METHOD = "payment_method"
        const val PAYMENT_GATEWAY = "payment_gateway"
        const val COUPON_CODE = "coupon_code"
        const val DISCOUNT_AMOUNT = "discount_amount"
        const val SHIPPING_COST = "shipping_cost"
        const val SCREEN_NAME = "screen_name"
        const val SCREEN_CLASS = "screen_class"
        const val ERROR_MESSAGE = "error_message"
        const val ERROR_CODE = "error_code"
        const val SEARCH_QUERY = "search_query"
        const val SEARCH_RESULTS = "search_results"
        const val FILTER_TYPE = "filter_type"
        const val FILTER_VALUE = "filter_value"
        const val SORT_BY = "sort_by"
        const val USER_ID = "user_id"
        const val SESSION_ID = "session_id"
        const val CURRENCY = "currency"
        const val LOCALE = "locale"
        const val METHOD = "method"
        const val VALUE = "value"
    }

    // ============== Event Category Constants ==============
    /**
     * Event categories for grouping and filtering analytics.
     */
    object Categories {
        const val ECOMMERCE = "ecommerce"
        const val USER_ENGAGEMENT = "user_engagement"
        const val AUTHENTICATION = "authentication"
        const val ERROR = "error"
        const val NAVIGATION = "navigation"
        const val JEWELRY_FEATURES = "jewelry_features"
    }
}
