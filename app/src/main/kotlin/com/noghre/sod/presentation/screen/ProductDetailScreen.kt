package com.noghre.sod.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.noghre.sod.presentation.viewmodel.ProductsViewModel

/**
 * ProductDetailScreen - صفحه جزئیات محصول
 * Features:
 * - Image gallery with pager
 * - Product specifications
 * - Reviews section
 * - Related products
 * - Add to cart functionality
 * - Favorite button
 * - Share functionality
 * - Stock status
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ProductsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { /* Favorite */ }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Price",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                        )
                        Text(
                            text = "$99.99",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Cart")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Image Gallery
            item {
                ProductImageGallery(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }

            // Product Info
            item {
                ProductInfoSection()
            }

            // Specifications
            item {
                SpecificationsSection()
            }

            // Reviews
            item {
                ReviewsSection()
            }

            // Related Products
            item {
                RelatedProductsSection()
            }
        }
    }
}

/**
 * Product image gallery with horizontal pager
 */
@Composable
private fun ProductImageGallery(
    modifier: Modifier = Modifier
) {
    val images = listOf(
        "https://example.com/product-1.jpg",
        "https://example.com/product-2.jpg",
        "https://example.com/product-3.jpg"
    )

    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = images[page],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Page indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(images.size) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (pagerState.currentPage == index)
                                MaterialTheme.colorScheme.primary
                            else
                                Color.White.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

/**
 * Product information section
 */
@Composable
private fun ProductInfoSection() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Premium Silver Bracelet",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rating
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("⭐ 4.8", style = MaterialTheme.typography.bodyMedium)
            Text(
                "(127 reviews)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stock status
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "In Stock",
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        var expanded by remember { mutableStateOf(false) }
        val description = "This beautiful silver bracelet is crafted with precision and care. Made from high-quality sterling silver, it features an elegant design that complements any outfit. Perfect for both casual and formal occasions."

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis
        )

        if (description.length > 150) {
            Text(
                text = if (expanded) "Show Less" else "Show More",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}

/**
 * Product specifications section
 */
@Composable
private fun SpecificationsSection() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Specifications",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        SpecificationRow("Material", "Sterling Silver (925)")
        SpecificationRow("Weight", "12.5g")
        SpecificationRow("Size", "Adjustable (6-8 inches)")
        SpecificationRow("Color", "Silver")
        SpecificationRow("Warranty", "2 Years")
    }
}

@Composable
private fun SpecificationRow(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = key,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Reviews section
 */
@Composable
private fun ReviewsSection() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Reviews",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        ReviewItem(
            author = "Sarah M.",
            rating = 5,
            comment = "Excellent quality! Exactly as described. Very happy with my purchase."
        )
        ReviewItem(
            author = "John D.",
            rating = 4,
            comment = "Great bracelet, arrived quickly."
        )

        Button(
            onClick = { /* View all reviews */ },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("View All Reviews")
        }
    }
}

@Composable
private fun ReviewItem(
    author: String,
    rating: Int,
    comment: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = author,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "⭐ $rating.0",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = comment,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Related products section
 */
@Composable
private fun RelatedProductsSection() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Related Products",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Related products grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(2) {
                RelatedProductCard()
            }
        }
    }
}

@Composable
private fun RelatedProductCard() {
    Card(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(0.8f)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Silver Ring",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$49.99",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
