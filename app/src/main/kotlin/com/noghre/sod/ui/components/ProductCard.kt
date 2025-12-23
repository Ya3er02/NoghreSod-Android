package com.noghre.sod.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.noghre.sod.domain.model.Product
import com.noghre.sod.ui.theme.Gold
import com.noghre.sod.ui.theme.Spacing

/**
 * Product card for displaying products in grid or list
 *
 * @param product Product to display
 * @param onProductClick Callback when product is clicked
 * @param onFavoriteClick Callback when favorite button is clicked
 * @param modifier Modifier for the card
 */
@Composable
fun ProductCard(
    product: Product,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick(product.id) },
        shape = RoundedCornerShape(Spacing.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clip(RoundedCornerShape(Spacing.medium))
            ) {
                AsyncImage(
                    model = product.images.firstOrNull(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )

                // Discount badge
                if (product.discountPrice != null) {
                    val discountPercent =
                        ((product.price - product.discountPrice!!) / product.price * 100).toInt()
                    DiscountBadge(discountPercent)
                }

                // Favorite button
                IconButton(
                    onClick = { onFavoriteClick(product.id) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Spacing.small)
                ) {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Content section
            Column(
                modifier = Modifier.padding(Spacing.medium)
            ) {
                // Product name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                // Rating
                Row(
                    modifier = Modifier
                        .padding(vertical = Spacing.small)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingBar(
                        rating = product.rating,
                        reviewCount = product.reviewCount
                    )
                }

                // Price
                PriceDisplay(
                    price = product.price,
                    discountPrice = product.discountPrice,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun DiscountBadge(
    discountPercent: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(Spacing.small)
            .background(
                color = Color.Red,
                shape = RoundedCornerShape(Spacing.small)
            )
            .padding(Spacing.extraSmall),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "-$discountPercent%",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}
