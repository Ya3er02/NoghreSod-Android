package com.noghre.sod.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noghre.sod.ui.screens.HomeScreen
import com.noghre.sod.ui.screens.CartScreen
import com.noghre.sod.ui.screens.ProfileScreen
import com.noghre.sod.ui.screens.OrderScreen

sealed class Route(val route: String) {
    object Home : Route("home")
    object Products : Route("products")
    object ProductDetail : Route("product/{productId}")
    object Cart : Route("cart")
    object Orders : Route("orders")
    object OrderDetail : Route("order/{orderId}")
    object Profile : Route("profile")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Route.Home.route) {
            HomeScreen(navController)
        }
        composable(Route.Cart.route) {
            CartScreen(navController)
        }
        composable(Route.Orders.route) {
            OrderScreen(navController)
        }
        composable(Route.Profile.route) {
            ProfileScreen(navController)
        }
    }
}
