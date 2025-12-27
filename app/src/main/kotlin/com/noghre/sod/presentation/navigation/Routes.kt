package com.noghre.sod.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe route definitions using Kotlin Serialization
 * This eliminates string-based routing errors at compile time
 */
sealed interface Route {
    
    // ============================================
    // Authentication Routes
    // ============================================
    
    @Serializable
    data object Login : Route
    
    @Serializable
    data object Register : Route
    
    @Serializable
    data class ForgotPassword(val email: String? = null) : Route
    
    // ============================================
    // Main App Routes
    // ============================================
    
    @Serializable
    data object Home : Route
    
    @Serializable
    data class ProductDetail(val productId: String) : Route
    
    @Serializable
    data class ProductList(
        val categoryId: String? = null,
        val sortBy: String = "popular",
        val minPrice: Double? = null,
        val maxPrice: Double? = null
    ) : Route
    
    @Serializable
    data class Search(val query: String) : Route
    
    // ============================================
    // Shopping Routes
    // ============================================
    
    @Serializable
    data object Cart : Route
    
    @Serializable
    data class Checkout(val cartId: String) : Route
    
    @Serializable
    data class OrderDetail(val orderId: String) : Route
    
    @Serializable
    data object Orders : Route
    
    // ============================================
    // Profile Routes
    // ============================================
    
    @Serializable
    data object Profile : Route
    
    @Serializable
    data object EditProfile : Route
    
    @Serializable
    data object Addresses : Route
    
    @Serializable
    data class AddEditAddress(val addressId: String? = null) : Route
    
    @Serializable
    data object PaymentMethods : Route
    
    @Serializable
    data object Settings : Route
    
    @Serializable
    data object Wishlist : Route
    
    @Serializable
    data object Notifications : Route
    
    @Serializable
    data object Help : Route
}

/**
 * Navigation graph definitions for nested navigation
 */
sealed interface NavGraph {
    @Serializable
    data object AuthGraph : NavGraph
    
    @Serializable
    data object MainGraph : NavGraph
    
    @Serializable
    data object ShoppingGraph : NavGraph
    
    @Serializable
    data object ProfileGraph : NavGraph
}
