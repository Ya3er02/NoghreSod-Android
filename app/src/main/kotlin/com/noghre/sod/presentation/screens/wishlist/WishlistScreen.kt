package com.noghre.sod.presentation.screens.wishlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noghre.sod.domain.model.Product
import com.noghre.sod.presentation.components.PersianButton

/**
 * Wishlist Screen - User's favorite products.
 * 
* Shows wishlist items with price tracking.
 * Add to cart functionality.
 * Price drop notifications.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Composable
fun WishlistScreen(
    viewModel: WishlistViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit = {},
    onAddToCartClick: () -> Unit = {}
) {
    val uiState by viewModel.wishlistUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        WishlistHeader()

        // Wishlist content
        if (isLoading && uiState.items.isEmpty()) {
            WishlistLoadingState()
        } else if (uiState.items.isEmpty()) {
            WishlistEmptyState()
        } else {
            WishlistContent(
                items = uiState.items,
                onProductClick = onProductClick,
                onAddToCart = { product ->
                    viewModel.addToCart(product)
                    onAddToCartClick()
                },
                onRemoveFromWishlist = { product ->
                    viewModel.removeFromWishlist(product.id)
                },
                onShare = { viewModel.shareWishlist() }
            )
        }
    }
}

@Composable
fun WishlistHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "لیست علاقه مندی",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "در لیست علاقه خود محفوظ کنید",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun WishlistContent(
    items: List<Product>,
    onProductClick: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    onRemoveFromWishlist: (Product) -> Unit,
    onShare: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 8.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = items,
            key = { it.id }
        ) { product ->
            WishlistItemCard(
                product = product,
                onProductClick = { onProductClick(product.id) },
                onAddToCart = { onAddToCart(product) },
                onRemove = { onRemoveFromWishlist(product) }
            )
        }

        item {
            PersianButton(
                text = "اشتراک لیست",
                onClick = onShare,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun WishlistItemCard(
    product: Product,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProductClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Product image
                Card(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = product.name.first().toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                // Product info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )

                    if (!product.description.isNullOrEmpty()) {
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Price info
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "${product.price} ریال",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        // Price drop indicator
                        if (product.originalPrice != null && product.originalPrice > product.price) {
                            Text(
                                text = "تخفیف شد!",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Remove button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Add to cart button
            PersianButton(
                text = "افزودن به سبد",
                onClick = onAddToCart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun WishlistLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun WishlistEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "هیچ محصولی وجود ندارد",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "محصولات رو به لیست علاقه اضافه کنید",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
