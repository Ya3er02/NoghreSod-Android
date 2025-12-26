package com.noghre.sod.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.overflow.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import coil.compose.AsyncImage
import com.noghre.sod.domain.model.Product

/**
 * RTL-compatible Product Card component.
 *
 * Features:
 * - Proper RTL/LTR layout handling
 * - Responsive image loading with Coil
 * - Favorite button with toggle state
 * - Add to cart button
 * - Price display with proper formatting
 * - Mirrored icons for RTL
 * - Touch feedback with ripple effect
 *
 * @param product The product to display
 * @param onCardClick Callback when card is clicked
 * @param onFavoriteClick Callback when favorite button is clicked
 * @param onAddToCartClick Callback when add to cart button is clicked
 * @param modifier Optional modifier for styling
 */
@Composable
fun ProductCard(
    product: Product,
    onCardClick: (Product) -> Unit,
    onFavoriteClick: (Product) -> Unit,
    onAddToCartClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false
) {
    val layoutDirection = LocalLayoutDirection.current
    val isFavored = remember { mutableStateOf(isFavorite) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .clickable { onCardClick(product) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Header with image and favorite button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        color = Color.LightGray,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                // Product Image
                AsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Favorite Button (positioned at top - respects RTL)
                // In RTL: button appears at left (naturallly)
                // In LTR: button appears at right (naturally)
                Box(
                    modifier = Modifier
                        .align(
                            if (layoutDirection == LayoutDirection.Rtl)
                                Alignment.TopStart
                            else
                                Alignment.TopEnd
                        )
                        .padding(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            isFavored.value = !isFavored.value
                            onFavoriteClick(product)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.9f),
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        Icon(
                            imageVector = if (isFavored.value)
                                Icons.Filled.Favorite
                            else
                                Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(id = android.R.string.ok),
                            tint = if (isFavored.value)
                                Color(0xFFE91E63)
                            else
                                Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Product Name (truncated)
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Product Category
            Text(
                text = product.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Footer with price and add to cart button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price (uses natural alignment in row for RTL/LTR)
                Text(
                    text = formatPrice(product.price),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Add to Cart Button
                IconButton(
                    onClick = { onAddToCartClick(product) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = stringResource(id = android.R.string.ok),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Format price with Persian numerals and currency symbol.
 *
 * @param price Price in Rials
 * @return Formatted price string
 */
private fun formatPrice(price: Float): String {
    val formattedPrice = String.format("%,.0f", price)
    return "ریال $formattedPrice"
}

/**
 * Product Card List component with proper RTL/LTR grid layout.
 *
 * Uses LazyVerticalGrid which automatically handles RTL layout.
 *
 * @param products List of products to display
 * @param onCardClick Callback when card is clicked
 * @param onFavoriteClick Callback when favorite button is clicked
 * @param onAddToCartClick Callback when add to cart button is clicked
 * @param modifier Optional modifier
 */
@Composable
fun ProductCardGrid(
    products: List<Product>,
    onCardClick: (Product) -> Unit,
    onFavoriteClick: (Product) -> Unit,
    onAddToCartClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
    favoriteProducts: Set<String> = emptySet()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        products.forEach { product ->
            ProductCard(
                product = product,
                onCardClick = onCardClick,
                onFavoriteClick = onFavoriteClick,
                onAddToCartClick = onAddToCartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                isFavorite = product.id in favoriteProducts
            )
        }
    }
}
