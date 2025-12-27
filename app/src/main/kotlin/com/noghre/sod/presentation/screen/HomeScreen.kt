package com.noghre.sod.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * HomeScreen - صفحه اصلی
 * Features:
 * - Search bar
 * - Featured products carousel
 * - Category grid
 * - Banner ads
 * - Recently viewed section
 * - Recommendations
 */
@Composable
fun HomeScreen(
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search bar
        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearchClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        // Featured products carousel
        item {
            FeaturedProductsCarousel(
                onProductClick = onProductClick
            )
        }

        // Category grid
        item {
            CategoryGrid(
                onCategoryClick = onCategoryClick
            )
        }

        // Banner ads
        item {
            BannerAdsSection()
        }

        // Recently viewed
        item {
            RecentlyViewedSection(
                onProductClick = onProductClick
            )
        }

        // Recommendations
        item {
            RecommendationsSection(
                onProductClick = onProductClick
            )
        }
    }
}

/**
 * Search bar component
 */
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search for jewelry...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

/**
 * Featured products carousel
 */
@Composable
private fun FeaturedProductsCarousel(
    onProductClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Featured Products",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(5) {
                FeaturedProductCard(
                    onProductClick = { onProductClick(it) }
                )
            }
        }
    }
}

@Composable
private fun FeaturedProductCard(
    onProductClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .aspectRatio(0.75f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    text = "Silver Bracelet",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
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

/**
 * Category grid
 */
@Composable
private fun CategoryGrid(
    onCategoryClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Shop by Category",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryRow(
                category1 = "Bracelets",
                category2 = "Rings",
                onCategoryClick = onCategoryClick
            )
            CategoryRow(
                category1 = "Necklaces",
                category2 = "Earrings",
                onCategoryClick = onCategoryClick
            )
        }
    }
}

@Composable
private fun CategoryRow(
    category1: String,
    category2: String,
    onCategoryClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CategoryCard(
            category = category1,
            modifier = Modifier.weight(1f),
            onCategoryClick = onCategoryClick
        )
        CategoryCard(
            category = category2,
            modifier = Modifier.weight(1f),
            onCategoryClick = onCategoryClick
        )
    }
}

@Composable
private fun CategoryCard(
    category: String,
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Banner ads section
 */
@Composable
private fun BannerAdsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(150.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.foundation.background(
                        MaterialTheme.colorScheme.secondary
                    ).brush ?: androidx.compose.foundation.background(
                        MaterialTheme.colorScheme.secondary
                    )
                )
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Special Offer",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = "Get 20% off on silver jewelry",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

/**
 * Recently viewed products
 */
@Composable
private fun RecentlyViewedSection(
    onProductClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently Viewed",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = { /* View all */ }) {
                Text("View All")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(5) {
                FeaturedProductCard(onProductClick = { onProductClick(it) })
            }
        }
    }
}

/**
 * Recommendations section
 */
@Composable
private fun RecommendationsSection(
    onProductClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Recommended For You",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(5) {
                FeaturedProductCard(onProductClick = { onProductClick(it) })
            }
        }
    }
}
