package com.noghre.sod.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.noghre.sod.presentation.screens.auth.LoginScreen
import com.noghre.sod.presentation.screens.auth.RegisterScreen
import com.noghre.sod.presentation.screens.cart.CartScreen
import com.noghre.sod.presentation.screens.products.ProductsScreen
import com.noghre.sod.presentation.screens.products.ProductDetailsScreen

/**
 * Navigation Graph for NoghreSod App.
 * 
* Defines all navigation routes and flows:
 * - Auth (Login, Register)
 * - Main (Products, Cart)
 * - Product Details
 * - Checkout
 * - Profile
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */

// Route definitions
object Routes {
    // Auth routes
    const val AUTH_GRAPH = "auth_graph"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Main routes
    const val MAIN_GRAPH = "main_graph"
    const val PRODUCTS = "products"
    const val PRODUCT_DETAILS = "product_details"
    const val PRODUCT_ID_ARG = "product_id"
    const val CART = "cart"
    const val WISHLIST = "wishlist"

    // Checkout routes
    const val CHECKOUT_GRAPH = "checkout_graph"
    const val CHECKOUT = "checkout"
    const val ADDRESS = "address"
    const val PAYMENT = "payment"
    const val ORDER_CONFIRMATION = "order_confirmation"
    const val ORDER_ID_ARG = "order_id"

    // Profile routes
    const val PROFILE_GRAPH = "profile_graph"
    const val PROFILE = "profile"
    const val ORDERS = "orders"
    const val SETTINGS = "settings"

    // Composable routes
    fun productDetails(productId: String) = "$PRODUCT_DETAILS/$productId"
    fun orderConfirmation(orderId: String) = "$ORDER_CONFIRMATION/$orderId"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Graph
        if (!isLoggedIn) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.MAIN_GRAPH) {
                            popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Routes.REGISTER)
                    }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Routes.MAIN_GRAPH) {
                            popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.popBackStack()
                    }
                )
            }
        } else {
            // Main Graph (Products, Cart)
            composable(Routes.PRODUCTS) {
                ProductsScreen(
                    onProductClick = { productId ->
                        navController.navigate(
                            Routes.productDetails(productId)
                        )
                    }
                )
            }

            composable(
                route = "${Routes.PRODUCT_DETAILS}/{${Routes.PRODUCT_ID_ARG}}",
                arguments = listOf(
                    navArgument(Routes.PRODUCT_ID_ARG) {
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString(Routes.PRODUCT_ID_ARG)
                productId?.let {
                    ProductDetailsScreen(
                        productId = it,
                        onBackClick = { navController.popBackStack() },
                        onAddToCartClick = {
                            navController.navigate(Routes.CART)
                        },
                        onCheckoutClick = {
                            navController.navigate(Routes.CHECKOUT)
                        }
                    )
                }
            }

            composable(Routes.CART) {
                CartScreen(
                    onCheckoutClick = {
                        navController.navigate(Routes.CHECKOUT)
                    }
                )
            }

            // Checkout Graph
            composable(Routes.CHECKOUT) {
                // CheckoutScreen
                // TODO: Implement checkout screen
            }

            composable(Routes.ADDRESS) {
                // AddressScreen
                // TODO: Implement address screen
            }

            composable(Routes.PAYMENT) {
                // PaymentScreen
                // TODO: Implement payment screen
            }

            composable(
                route = "${Routes.ORDER_CONFIRMATION}/{${Routes.ORDER_ID_ARG}}",
                arguments = listOf(
                    navArgument(Routes.ORDER_ID_ARG) {
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString(Routes.ORDER_ID_ARG)
                // OrderConfirmationScreen
                // TODO: Implement order confirmation screen
            }

            // Wishlist
            composable(Routes.WISHLIST) {
                // WishlistScreen
                // TODO: Implement wishlist screen
            }

            // Profile Graph
            composable(Routes.PROFILE) {
                // ProfileScreen
                // TODO: Implement profile screen
            }

            composable(Routes.ORDERS) {
                // OrdersScreen
                // TODO: Implement orders screen
            }

            composable(Routes.SETTINGS) {
                // SettingsScreen
                // TODO: Implement settings screen
            }
        }
    }
}

/**
 * App-level navigation helper functions.
 */
fun NavHostController.navigateToProductDetails(productId: String) {
    this.navigate(Routes.productDetails(productId))
}

fun NavHostController.navigateToCart() {
    this.navigate(Routes.CART)
}

fun NavHostController.navigateToCheckout() {
    this.navigate(Routes.CHECKOUT)
}

fun NavHostController.navigateToProfile() {
    this.navigate(Routes.PROFILE)
}

fun NavHostController.navigateToLogin() {
    this.navigate(Routes.LOGIN) {
        popUpTo(0) { inclusive = true }
    }
}
