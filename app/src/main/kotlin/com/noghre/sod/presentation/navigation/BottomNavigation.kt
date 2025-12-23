package com.noghre.sod.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.material.icons.vector.ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Routes.HOME,
        label = "اصلی",
        icon = Icons.Filled.Home
    ),
    BottomNavItem(
        route = Routes.PRODUCT_LIST,
        label = "محصولات",
        icon = Icons.Filled.ShoppingBag
    ),
    BottomNavItem(
        route = Routes.CART,
        label = "سبد",
        icon = Icons.Filled.ShoppingCart
    ),
    BottomNavItem(
        route = Routes.ORDERS,
        label = "سفارشات",
        icon = Icons.Filled.LocalShipping
    ),
    BottomNavItem(
        route = Routes.PROFILE,
        label = "پروفایل",
        icon = Icons.Filled.Person
    )
)

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry.value?.destination

    NavigationBar {
        bottomNavItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        lazyRestoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}
