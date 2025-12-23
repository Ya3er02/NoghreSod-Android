package com.noghre.sod.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noghre.sod.presentation.ui.auth.LoginScreen
import com.noghre.sod.presentation.ui.auth.RegisterScreen
import com.noghre.sod.presentation.ui.cart.CartScreen
import com.noghre.sod.presentation.ui.checkout.CheckoutScreen
import com.noghre.sod.presentation.ui.home.HomeScreen
import com.noghre.sod.presentation.ui.orders.OrdersScreen
import com.noghre.sod.presentation.ui.product.ProductDetailScreen
import com.noghre.sod.presentation.ui.product.ProductListScreen
import com.noghre.sod.presentation.ui.profile.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.HOME,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                },
                onSearchClick = {
                    navController.navigate(Routes.PRODUCT_LIST)
                }
            )
        }

        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = Routes.productDetailArgs
        ) {
            ProductDetailScreen(
                onBackClick = { navController.popBackStack() },
                onAddToCart = {
                    navController.navigate(Routes.CART) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.PRODUCT_LIST) {
            ProductListScreen(
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                }
            )
        }

        composable(Routes.CART) {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = {
                    navController.navigate(Routes.CHECKOUT) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.CHECKOUT) {
            CheckoutScreen(
                onBackClick = { navController.popBackStack() },
                onPaymentSuccess = {
                    navController.navigate(Routes.ORDERS) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.ORDERS) {
            OrdersScreen(
                onBackClick = { navController.popBackStack() },
                onOrderClick = { orderId ->
                    // Navigate to order detail if needed
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onEditClick = {
                    navController.navigate(Routes.EDIT_PROFILE)
                },
                onLogoutClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER) {
                        popUpTo(Routes.LOGIN)
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER)
                    }
                }
            )
        }

        composable(Routes.EDIT_PROFILE) {
            // Edit profile screen would go here
        }
    }
}
