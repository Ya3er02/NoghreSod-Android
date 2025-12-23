package com.noghre.sod.core.config

/**
 * Centralized API endpoints configuration.
 * All API endpoint paths are defined here and organized by feature.
 * This ensures consistency and makes it easy to update endpoints when backend changes.
 */
object ApiEndpoints {

    // ===== Authentication Endpoints =====
    object Auth {
        const val LOGIN = "auth/login"
        const val REGISTER = "auth/register"
        const val REFRESH_TOKEN = "auth/refresh"
        const val LOGOUT = "auth/logout"
        const val RESET_PASSWORD = "auth/reset-password"
        const val VERIFY_EMAIL = "auth/verify-email"
    }

    // ===== Product Endpoints =====
    object Products {
        const val LIST = "api/products"
        const val DETAIL = "api/products/{id}"
        const val SEARCH = "api/products/search"
        const val BY_CATEGORY = "api/products/category/{categoryId}"
        const val TRENDING = "api/products/trending"
        const val FEATURED = "api/products/featured"
    }

    // ===== Category Endpoints =====
    object Categories {
        const val LIST = "api/categories"
        const val DETAIL = "api/categories/{id}"
    }

    // ===== Silver Price Endpoints =====
    object Prices {
        const val LIVE_PRICE = "api/prices/live"
        const val PRICE_HISTORY = "api/prices/history"
        const val CALCULATE_PRICE = "api/prices/calculate"
        const val PRICE_TRENDS = "api/prices/trends"
    }

    // ===== User Profile Endpoints =====
    object User {
        const val PROFILE = "api/user/profile"
        const val UPDATE_PROFILE = "api/user/profile"
        const val CHANGE_PASSWORD = "api/user/change-password"
        const val DELETE_ACCOUNT = "api/user/account"
    }

    // ===== Wishlist Endpoints =====
    object Wishlist {
        const val LIST = "api/user/wishlist"
        const val ADD = "api/user/wishlist/add"
        const val REMOVE = "api/user/wishlist/remove"
        const val CHECK = "api/user/wishlist/check/{productId}"
    }

    // ===== Address Endpoints (Future) =====
    object Address {
        const val LIST = "api/user/addresses"
        const val ADD = "api/user/addresses"
        const val UPDATE = "api/user/addresses/{id}"
        const val DELETE = "api/user/addresses/{id}"
        const val SET_DEFAULT = "api/user/addresses/{id}/default"
    }

    // ===== Payment Methods Endpoints (Future) =====
    object Payment {
        const val LIST_METHODS = "api/payment-methods"
        const val ADD_METHOD = "api/payment-methods"
        const val DELETE_METHOD = "api/payment-methods/{id}"
        const val SET_DEFAULT = "api/payment-methods/{id}/default"
    }

    // ===== Cart Endpoints (Not yet implemented in backend) =====
    object Cart {
        const val GET = "api/cart"
        const val ADD_ITEM = "api/cart/items"
        const val UPDATE_ITEM = "api/cart/items/{itemId}"
        const val REMOVE_ITEM = "api/cart/items/{itemId}"
        const val CLEAR = "api/cart/clear"
    }

    // ===== Order Endpoints (Not yet implemented in backend) =====
    object Orders {
        const val LIST = "api/orders"
        const val DETAIL = "api/orders/{id}"
        const val CREATE = "api/orders"
        const val CANCEL = "api/orders/{id}/cancel"
        const val RETURN = "api/orders/{id}/return"
        const val TRACKING = "api/orders/{id}/tracking"
    }

    // ===== Review Endpoints (Future) =====
    object Reviews {
        const val LIST = "api/products/{productId}/reviews"
        const val CREATE = "api/products/{productId}/reviews"
        const val UPDATE = "api/reviews/{id}"
        const val DELETE = "api/reviews/{id}"
    }

    // ===== Notification Endpoints (Future) =====
    object Notifications {
        const val LIST = "api/notifications"
        const val MARK_READ = "api/notifications/{id}/read"
        const val MARK_ALL_READ = "api/notifications/read-all"
        const val DELETE = "api/notifications/{id}"
        const val PREFERENCES = "api/user/notification-preferences"
    }

    // ===== Search Endpoints =====
    object Search {
        const val SEARCH_PRODUCTS = "api/search/products"
        const val SEARCH_SUGGESTIONS = "api/search/suggestions"
        const val TRENDING_SEARCHES = "api/search/trending"
    }

    // ===== Helper Endpoint Properties =====
    object Params {
        // Common query parameters
        const val PAGE = "page"
        const val LIMIT = "limit"
        const val SORT = "sort"
        const val ORDER = "order"
        const val CATEGORY = "category"
        const val SEARCH_QUERY = "q"
        const val FILTER = "filter"
        const val INCLUDE = "include"
        const val EXCLUDE = "exclude"

        // Sorting options
        const val SORT_NEWEST = "newest"
        const val SORT_OLDEST = "oldest"
        const val SORT_PRICE_LOW_HIGH = "price_asc"
        const val SORT_PRICE_HIGH_LOW = "price_desc"
        const val SORT_TRENDING = "trending"
        const val SORT_RATING = "rating"

        // Order options
        const val ORDER_ASC = "asc"
        const val ORDER_DESC = "desc"
    }
}
