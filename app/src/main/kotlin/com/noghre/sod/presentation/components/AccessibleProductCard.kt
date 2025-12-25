package com.noghre.sod.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.noghre.sod.R
import com.noghre.sod.domain.model.Product

/**
 * Accessible product card with RTL support and proper content descriptions.
 * 
 * Accessibility features:
 * - Content descriptions for all images
 * - Semantic properties for screen readers
 * - Color contrast compliance (WCAG AA)
 * - Touch target size >= 48dp
 * 
 * RTL Support:
 * - Proper row/column arrangement
 * - Directionality handled by Compose
 * 
 * @since 1.0.0
 */
@Composable
fun AccessibleProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .semantics {
                contentDescription = "Product: ${product.name}, Price: ${product.price}"
            },
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image - RTL aware with proper content description
            ProductImagePlaceholder(
                productName = product.name,
                modifier = Modifier.size(100.dp)
            )

            // Product Details - RTL aware
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                // Product Name with proper semantics
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    modifier = Modifier.semantics {
                        contentDescription = "Product name: ${product.name}"
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price with currency (accessible format)
                Text(
                    text = stringResource(
                        R.string.price_format,
                        product.price.toPlainString()
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics {
                        contentDescription = "Price: ${product.price}"
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category/Stock info with accessibility
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stock status
                    if (product.inStock) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .padding(4.dp)
                                .semantics {
                                    contentDescription = "In stock"
                                }
                        ) {
                            Text(
                                text = stringResource(R.string.in_stock),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .padding(4.dp)
                                .semantics {
                                    contentDescription = "Out of stock"
                                }
                        ) {
                            Text(
                                text = stringResource(R.string.out_of_stock),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }

                    // Discount badge if applicable
                    if (product.discountPercentage.compareTo(java.math.BigDecimal.ZERO) > 0) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .padding(4.dp)
                                .semantics {
                                    contentDescription = "Discount: ${product.discountPercentage}% off"
                                }
                        ) {
                            Text(
                                text = "-${product.discountPercentage}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }

            // Rating (if available) - RTL aware
            if (product.rating > 0f) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .height(100.dp)
                        .semantics {
                            contentDescription = "Rating: ${product.rating} out of 5 stars"
                        }
                ) {
                    Text(
                        text = "★",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = String.format("%.1f", product.rating),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun ProductImagePlaceholder(
    productName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .semantics {
                contentDescription = "Product image for $productName"
            },
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Image",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
