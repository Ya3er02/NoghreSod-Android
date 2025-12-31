package com.noghre.sod.presentation.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noghre.sod.domain.model.Product
import com.noghre.sod.presentation.components.PersianButton
import com.noghre.sod.presentation.components.StarRating

/**
 * Product Details Screen - Product detail page.
 * 
* Shows complete product information with images.
 * Add to cart, wishlist functionality.
 * Related products and reviews.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Composable
fun ProductDetailsScreen(
    productId: String,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onAddToCartClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    val uiState by viewModel.productDetailsUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProductDetails(productId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top app bar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = { viewModel.toggleFavorite(productId) }
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = null,
                        tint = if (isFavorite) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.product != null) {
            ProductDetailsContent(
                product = uiState.product!!,
                onAddToCartClick = {
                    viewModel.addToCart(productId)
                    onAddToCartClick()
                },
                onBuyNowClick = {
                    viewModel.addToCart(productId)
                    onCheckoutClick()
                }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("محصول یافت نشد")
            }
        }
    }
}

@Composable
fun ProductDetailsContent(
    product: Product,
    onAddToCartClick: () -> Unit,
    onBuyNowClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            // Product image with zoom
            ProductImageViewer(
                imageUrl = product.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Product name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Rating and reviews
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StarRating(
                        rating = product.rating ?: 0.0,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${product.rating ?: 0.0} (${product.reviewCount ?: 0} نظر)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Divider()

                // Price section
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (product.originalPrice != null && product.originalPrice > product.price) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${product.originalPrice} ریال",
                                style = MaterialTheme.typography.bodySmall,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "تخفیف ${((product.originalPrice!! - product.price) / product.originalPrice!! * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Text(
                        text = "${product.price} ریال",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Divider()

                // Availability
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "موجودی:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (product.inStock) "موجود" else "ناموجود",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (product.inStock) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "توضیحات محصول",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = product.description ?: "توضیحی موجود نیست",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Related products
        if (product.relatedProducts?.isNotEmpty() == true) {
            item {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "محصولات مرتبط",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = product.relatedProducts,
                            key = { it.id }
                        ) { relatedProduct ->
                            RelatedProductCard(product = relatedProduct)
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PersianButton(
                    text = "افزودن به سبد",
                    onClick = onAddToCartClick,
                    modifier = Modifier.fillMaxWidth()
                )

                PersianButton(
                    text = "خرید فوری",
                    onClick = onBuyNowClick,
                    modifier = Modifier.fillMaxWidth(),
                    buttonType = "primary"
                )
            }
        }
    }
}

@Composable
fun ProductImageViewer(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Load image with Coil and implement zoom functionality
            Text("تصویر محصول")
        }
    }
}

@Composable
fun RelatedProductCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(150.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("تصویر")
            }

            Text(
                text = product.name,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${product.price} ریال",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
