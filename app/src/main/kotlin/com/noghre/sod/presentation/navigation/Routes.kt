package com.noghre.sod.presentation.navigation

object Routes {
    // Authentication
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password"

    // Home & Products
    const val HOME = "home"
    const val PRODUCT_LIST = "product_list"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val SEARCH = "search"
    const val SEARCH_RESULTS = "search_results"
    const val CATEGORY = "category/{category}"

    // Cart & Checkout
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val PAYMENT = "payment/{orderId}"
    const val ORDER_CONFIRMATION = "order_confirmation/{orderId}"

    // Orders
    const val ORDERS = "orders"
    const val ORDER_DETAIL = "order_detail/{orderId}"
    const val ORDER_TRACKING = "order_tracking/{orderId}"
    const val RETURN_REQUEST = "return_request/{orderId}"

    // User Profile
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val ADDRESSES = "addresses"
    const val ADD_ADDRESS = "add_address"
    const val EDIT_ADDRESS = "edit_address/{addressId}"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val SECURITY = "security"

    // Navigation arguments
    fun getProductDetail(productId: String) = "product_detail/$productId"
    fun getOrderDetail(orderId: String) = "order_detail/$orderId"
    fun getOrderTracking(orderId: String) = "order_tracking/$orderId"
    fun getReturnRequest(orderId: String) = "return_request/$orderId"
    fun getCategory(category: String) = "category/$category"
    fun getPayment(orderId: String) = "payment/$orderId"
    fun getOrderConfirmation(orderId: String) = "order_confirmation/$orderId"
    fun getEditAddress(addressId: String) = "edit_address/$addressId"
}
