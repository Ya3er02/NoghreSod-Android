package com.noghre.sod.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Empty state view for lists and searches.
 */
@Composable
fun EmptyStateView(
    title: String,
    message: String,
    icon: ImageVector = Icons.Default.Search,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(actionText)
            }
        }
    }
}

/**
 * No products found empty state.
 */
@Composable
fun NoProductsEmptyState(
    onBrowseAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateView(
        title = "No Products Found",
        message = "Try adjusting your filters or search terms to find more products.",
        icon = Icons.Default.Search,
        actionText = "Browse All Products",
        onAction = onBrowseAll,
        modifier = modifier
    )
}

/**
 * Empty cart state.
 */
@Composable
fun EmptyCartState(
    onShopNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateView(
        title = "Your Cart is Empty",
        message = "Start shopping and add items to your cart to see them here.",
        icon = Icons.Default.ShoppingCart,
        actionText = "Shop Now",
        onAction = onShopNow,
        modifier = modifier
    )
}

/**
 * Empty wishlist state.
 */
@Composable
fun EmptyWishlistState(
    onExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateView(
        title = "Wishlist is Empty",
        message = "Add items to your wishlist to save them for later.",
        icon = Icons.Default.Favorite,
        actionText = "Explore Products",
        onAction = onExplore,
        modifier = modifier
    )
}

/**
 * Empty search history state.
 */
@Composable
fun EmptySearchHistoryState(
    modifier: Modifier = Modifier
) {
    EmptyStateView(
        title = "No Search History",
        message = "Your search history will appear here.",
        icon = Icons.Default.History,
        modifier = modifier
    )
}

/**
 * Empty orders state.
 */
@Composable
fun EmptyOrdersState(
    onShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateView(
        title = "No Orders Yet",
        message = "Start shopping to place your first order.",
        icon = Icons.Default.ShoppingCart,
        actionText = "Start Shopping",
        onAction = onShop,
        modifier = modifier
    )
}