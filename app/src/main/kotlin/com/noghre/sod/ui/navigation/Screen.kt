package com.noghre.sod.ui.navigation

/**
 * Navigation routes for the app
 */
sealed class Screen(open val route: String) {
    // Auth
    object Login : Screen("login")
    object Register : Screen("register")
    object OtpVerification : Screen("otp/{phone}") {
        fun createRoute(phone: String) = "otp/$phone"
    }

    // Main
    object Home : Screen("home")
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object OrderConfirmation : Screen("order_confirmation")

    // User
    object Profile : Screen("profile")
    object Orders : Screen("orders")
    object OrderDetail : Screen("order/{orderId}") {
        fun createRoute(orderId: String) = "order/$orderId"
    }
    object Addresses : Screen("addresses")
    object AddEditAddress : Screen("address/{addressId?}") {
        fun createRoute(addressId: String? = null) = if (addressId != null) "address/$addressId" else "address/null"
    }

    // Favorites & Search
    object Favorites : Screen("favorites")
    object Search : Screen("search")
    object Categories : Screen("categories")
}
