package com.noghre.sod.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/**
 * Main navigation graph for the app
 *
 * @param navController Navigation controller
 * @param startDestination Starting route
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            // LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            // RegisterScreen(navController)
        }
        composable(Screen.OtpVerification.route) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            // OtpVerificationScreen(phone, navController)
        }

        // Main screens
        composable(Screen.Home.route) {
            // HomeScreen(navController)
        }
        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            // ProductDetailScreen(productId, navController)
        }
        composable(Screen.Cart.route) {
            // CartScreen(navController)
        }
        composable(Screen.Checkout.route) {
            // CheckoutScreen(navController)
        }
        composable(Screen.OrderConfirmation.route) {
            // OrderConfirmationScreen(navController)
        }

        // User screens
        composable(Screen.Profile.route) {
            // ProfileScreen(navController)
        }
        composable(Screen.Orders.route) {
            // OrdersScreen(navController)
        }
        composable(Screen.OrderDetail.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            // OrderDetailScreen(orderId, navController)
        }
        composable(Screen.Addresses.route) {
            // AddressesScreen(navController)
        }
        composable(Screen.AddEditAddress.route) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId")
            // AddEditAddressScreen(addressId, navController)
        }

        // Other screens
        composable(Screen.Favorites.route) {
            // FavoritesScreen(navController)
        }
        composable(Screen.Search.route) {
            // SearchScreen(navController)
        }
        composable(Screen.Categories.route) {
            // CategoriesScreen(navController)
        }
    }
}
