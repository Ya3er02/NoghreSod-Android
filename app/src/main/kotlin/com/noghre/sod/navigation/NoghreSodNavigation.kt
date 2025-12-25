package com.noghre.sod.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.noghre.sod.ui.screens.auth.LoginScreen
import com.noghre.sod.ui.screens.auth.RegisterScreen
import com.noghre.sod.ui.screens.cart.*
import com.noghre.sod.ui.screens.home.HomeScreen
import com.noghre.sod.ui.screens.product.ProductDetailScreen
import com.noghre.sod.ui.screens.profile.ProfileScreen

/**
 * 🗨️ Navigation Graph for NoghreSod App
 *
 * Routes:
 * - auth/login
 * - auth/register
 * - home
 * - product/{productId}
 * - cart
 * - checkout
 * - orders
 * - profile
 */
@Composable
fun NoghreSodNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 🔐 Auth Routes
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { 
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { 
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        
        // 🏠 Home Route
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }
        
        // 📐 Product Detail Route
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            ProductDetailScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
            )
        }
        
        // 🛒 Cart Route
        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId)) {
                        popUpTo(Screen.Cart.route)
                    }
                }
            )
        }
        
        // 🛍️ Checkout Route
        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderSuccess = { orderId ->
                    navController.navigate(Screen.OrderSuccess.createRoute(orderId)) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                }
            )
        }
        
        // 💫 Orders Route
        composable(Screen.Orders.route) {
            OrdersScreen(
                onNavigateToOrderDetail = { orderId ->
                    // TODO: Navigate to order detail
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // 👤 Profile Route
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.route)
                },
                onLogout = {
                    onLogout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
            )
        }
        
        // ✅ Order Success Route
        composable(
            route = Screen.OrderSuccess.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            OrderSuccessScreen(
                orderId = orderId,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.route)
                }
            )
        }
    }
}

/**
 * Sealed class for navigation routes
 */
sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("auth/login")
    object Register : Screen("auth/register")
    
    // Main
    object Home : Screen("home")
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
    
    // Shopping
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object Orders : Screen("orders")
    
    // User
    object Profile : Screen("profile")
    object OrderSuccess : Screen("order_success/{orderId}") {
        fun createRoute(orderId: String) = "order_success/$orderId"
    }
    
    companion object {
        fun isAuthRoute(route: String?): Boolean {
            return route?.startsWith("auth") == true
        }
        
        fun isSensitiveRoute(route: String?): Boolean {
            return when {
                route == Checkout.route -> true
                route == Orders.route -> true
                route == Profile.route -> true
                else -> false
            }
        }
    }
}

/**
 * Order Success Screen (Simple completion screen)
 */
@Composable
fun OrderSuccessScreen(
    orderId: String,
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    // TODO: Implement order success screen
}
