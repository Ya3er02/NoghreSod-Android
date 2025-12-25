package com.noghre.sod.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.noghre.sod.presentation.screen.*

/**
 * Main navigation graph for Noghresod application.
 * Defines all routes and screen transitions.
 */
@Composable
fun NoghreSodNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationDestinations.HOME,
        modifier = modifier
    ) {
        // ============== HOME & PRODUCTS ==============

        composable(NavigationDestinations.HOME) {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate("${NavigationDestinations.PRODUCT_DETAIL}/$productId")
                },
                onCategoryClick = { category ->
                    navController.navigate("${NavigationDestinations.PRODUCTS_BY_CATEGORY}/$category")
                }
            )
        }

        composable(NavigationDestinations.PRODUCTS) {
            ProductListScreen(
                onProductClick = { productId ->
                    navController.navigate("${NavigationDestinations.PRODUCT_DETAIL}/$productId")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("${NavigationDestinations.PRODUCTS_BY_CATEGORY}/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            ProductListScreen(
                onProductClick = { productId ->
                    navController.navigate("${NavigationDestinations.PRODUCT_DETAIL}/$productId")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("${NavigationDestinations.PRODUCT_DETAIL}/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onAddToCart = { product ->
                    // Show snackbar or navigate to cart
                    navController.navigate(NavigationDestinations.CART)
                }
            )
        }

        // ============== SEARCH ==============

        composable(NavigationDestinations.SEARCH) {
            // Search screen will be implemented
        }

        // ============== FAVORITES ==============

        composable(NavigationDestinations.FAVORITES) {
            // Favorites screen will be implemented
        }

        // ============== CART & CHECKOUT ==============

        composable(NavigationDestinations.CART) {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckout = {
                    navController.navigate(NavigationDestinations.CHECKOUT)
                }
            )
        }

        composable(NavigationDestinations.CHECKOUT) {
            CheckoutScreen(
                onBackClick = { navController.popBackStack() },
                onPlaceOrder = {
                    navController.navigate(NavigationDestinations.HOME) {
                        popUpTo(NavigationDestinations.HOME) { inclusive = true }
                    }
                }
            )
        }

        // ============== ORDERS ==============

        composable(NavigationDestinations.ORDERS) {
            OrderListScreen(
                onBackClick = { navController.popBackStack() },
                onOrderClick = { orderId ->
                    navController.navigate("${NavigationDestinations.ORDER_DETAIL}/$orderId")
                }
            )
        }

        composable("${NavigationDestinations.ORDER_DETAIL}/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(
                orderId = orderId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // ============== AUTHENTICATION ==============

        composable(NavigationDestinations.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavigationDestinations.HOME) {
                        popUpTo(NavigationDestinations.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavigationDestinations.REGISTER)
                }
            )
        }

        composable(NavigationDestinations.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(NavigationDestinations.HOME) {
                        popUpTo(NavigationDestinations.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ============== PROFILE ==============

        composable(NavigationDestinations.PROFILE) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onAddressClick = { addressId ->
                    // Navigate to address detail/edit
                }
            )
        }
    }
}

/**
 * Navigation destination routes.
 */
object NavigationDestinations {
    const val HOME = "home"
    const val PRODUCTS = "products"
    const val PRODUCTS_BY_CATEGORY = "products_by_category"
    const val PRODUCT_DETAIL = "product_detail"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val ORDERS = "orders"
    const val ORDER_DETAIL = "order_detail"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile"
}
