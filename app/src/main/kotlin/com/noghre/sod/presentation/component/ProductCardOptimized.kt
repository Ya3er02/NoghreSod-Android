package com.noghre.sod.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.noghre.sod.domain.model.Product
import com.noghre.sod.presentation.theme.NoghreSodTheme
import timber.log.Timber

/**
 * Optimized ProductCard component with Compose best practices.
 * Uses remember, derivedStateOf, and CompositionLocal for performance.
 */
@Composable
fun ProductCardOptimized(
    product: Product,
    onClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    // Use remember to avoid unnecessary recompositions
    val isProductFavorite by remember(product.id) {
        mutableStateOf(false)
    }

    // Use derivedStateOf for calculated values
    val discountedPrice by remember(product.price, product.discountPercentage) {
        derivedStateOf {
            product.price * (1 - product.discountPercentage / 100)
        }
    }

    // Format price once
    val formattedPrice = remember(product.price) {
        "\$${String.format("%.2f", product.price)}"
    }

    val formattedDiscountedPrice = remember(discountedPrice) {
        "\$${String.format("%.2f", discountedPrice)}"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = !isLoading,
                onClick = {
                    Timber.d("Product clicked: ${product.id}")
                    onClick(product)
                }
            ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            // Product Image
            AsyncImage(
                model = product.image,
                contentDescription = "Product: ${product.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            // Product Name
            Text(
                text = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Category
            Text(
                text = product.category,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Price Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (product.discountPercentage > 0) {
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = formattedDiscountedPrice,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Discount Badge
                if (product.discountPercentage > 0) {
                    Text(
                        text = "-${product.discountPercentage.toInt()}%",
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Rating
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    modifier = Modifier.padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${String.format("%.1f", product.rating)}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // Stock Status
            if (!product.inStock) {
                Text(
                    text = "Out of Stock",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Preview for ProductCard component.
 */
@Preview(showBackground = true, widthDp = 200)
@Composable
private fun ProductCardPreview() {
    NoghreSodTheme {
        ProductCardOptimized(
            product = Product(
                id = "1",
                name = "Silver Necklace Premium",
                price = 150.0,
                discountPercentage = 20.0,
                description = "Beautiful silver necklace",
                image = "https://via.placeholder.com/200",
                category = "Jewelry",
                rating = 4.5,
                inStock = true,
                createdAt = "2025-01-01"
            ),
            onClick = {}
        )
    }
}

/**
 * Preview for ProductCard component - Out of Stock.
 */
@Preview(showBackground = true, widthDp = 200)
@Composable
private fun ProductCardOutOfStockPreview() {
    NoghreSodTheme {
        ProductCardOptimized(
            product = Product(
                id = "2",
                name = "Silver Ring",
                price = 100.0,
                discountPercentage = 0.0,
                description = "Classic silver ring",
                image = "https://via.placeholder.com/200",
                category = "Jewelry",
                rating = 4.0,
                inStock = false,
                createdAt = "2025-01-01"
            ),
            onClick = {}
        )
    }
}
