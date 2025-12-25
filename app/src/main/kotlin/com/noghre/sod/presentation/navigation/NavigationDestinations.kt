package com.noghre.sod.presentation.navigation

/**
 * Navigation destinations for Compose Navigation.
 * Defines all app routes and deep links.
 *
 * @author Yaser
 * @version 1.0.0
 */
object NavigationDestinations {

    // ============== AUTH ROUTES ==============
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val SPLASH_ROUTE = "splash"

    // ============== HOME ROUTES ==============
    const val HOME_ROUTE = "home"
    const val BOTTOM_NAV_ROUTE = "bottom_nav"

    // ============== PRODUCT ROUTES ==============
    const val PRODUCTS_ROUTE = "products"
    const val PRODUCT_DETAIL_ROUTE = "product_detail"
    const val PRODUCT_DETAIL_ARG = "product_id"
    const val PRODUCT_DETAIL_FULL_ROUTE = "$PRODUCT_DETAIL_ROUTE/{$PRODUCT_DETAIL_ARG}"

    // ============== CART ROUTES ==============
    const val CART_ROUTE = "cart"
    const val CHECKOUT_ROUTE = "checkout"

    // ============== ORDER ROUTES ==============
    const val ORDERS_ROUTE = "orders"
    const val ORDER_DETAIL_ROUTE = "order_detail"
    const val ORDER_DETAIL_ARG = "order_id"
    const val ORDER_DETAIL_FULL_ROUTE = "$ORDER_DETAIL_ROUTE/{$ORDER_DETAIL_ARG}"

    // ============== PROFILE ROUTES ==============
    const val PROFILE_ROUTE = "profile"
    const val SETTINGS_ROUTE = "settings"
    const val ADDRESSES_ROUTE = "addresses"

    // ============== SEARCH ROUTES ==============
    const val SEARCH_ROUTE = "search"
    const val SEARCH_ARG = "query"
    const val SEARCH_FULL_ROUTE = "$SEARCH_ROUTE?$SEARCH_ARG={$SEARCH_ARG}"

    // ============== FAVORITES ROUTES ==============
    const val FAVORITES_ROUTE = "favorites"

    // ============== GRAPH ROUTES ==============
    const val AUTH_GRAPH = "auth_graph"
    const val APP_GRAPH = "app_graph"

    /**
     * Navigation argument builders.
     */
    object Args {
        fun productDetailRoute(productId: String): String = "$PRODUCT_DETAIL_ROUTE/$productId"
        fun orderDetailRoute(orderId: String): String = "$ORDER_DETAIL_ROUTE/$orderId"
        fun searchRoute(query: String): String = "$SEARCH_ROUTE?$SEARCH_ARG=$query"
    }
}

/**
 * Navigation event for handling one-time events.
 */
sealed class NavigationEvent {
    data class NavigateTo(val route: String) : NavigationEvent()
    object NavigateUp : NavigationEvent()
    object NavigateToHome : NavigationEvent()
    object NavigateToLogin : NavigationEvent()
    data class NavigateToProductDetail(val productId: String) : NavigationEvent()
    data class NavigateToOrderDetail(val orderId: String) : NavigationEvent()
}
