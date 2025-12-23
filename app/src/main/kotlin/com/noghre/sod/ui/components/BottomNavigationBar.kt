package com.noghre.sod.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home)
    object Search : BottomNavItem("search", "Search", Icons.Filled.Search)
    object Cart : BottomNavItem("cart", "Cart", Icons.Filled.ShoppingCart)
    object Favorites : BottomNavItem("favorites", "Favorites", Icons.Filled.Favorite)
    object Profile : BottomNavItem("profile", "Profile", Icons.Filled.Person)
}

/**
 * Bottom navigation bar with route selection
 *
 * @param selectedRoute Currently selected route
 * @param onNavigate Callback when item is selected
 * @param cartCount Optional cart item count
 * @param modifier Modifier for the bar
 */
@Composable
fun BottomNavigationBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    cartCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Cart,
        BottomNavItem.Favorites,
        BottomNavItem.Profile
    )

    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = if (item == BottomNavItem.Cart && cartCount > 0) {
                            { Badge { Text(cartCount.toString()) } }
                        } else null
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    }
                },
                label = { Text(item.label) },
                selected = selectedRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
