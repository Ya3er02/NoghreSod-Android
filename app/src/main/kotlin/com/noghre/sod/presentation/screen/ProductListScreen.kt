package com.noghre.sod.presentation.screen

import androidx.compose.animation.animateItemPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullRefreshIndicator
import androidx.compose.material3.pulltorefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.noghre.sod.presentation.viewmodel.ProductsUiState
import com.noghre.sod.presentation.viewmodel.ProductsViewModel

/**
 * ProductListScreen - صفحه لیست محصولات
 * Features:
 * - Pagination with infinite scroll
 * - Shimmer loading effect
 * - Pull-to-refresh
 * - Empty state with CTA
 * - Error state with retry
 * - Smooth animations
 * - Accessibility labels
 */
@Composable
fun ProductListScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val paginationState by viewModel.paginationState.collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState is ProductsUiState.Loading,
        onRefresh = { viewModel.loadProducts() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = uiState) {
            is ProductsUiState.Loading -> {
                ProductListShimmer()
            }

            is ProductsUiState.Success -> {
                if (state.products.isEmpty()) {
                    EmptyProductsState(
                        onExploreClick = { viewModel.loadProducts() }
                    )
                } else {
                    ProductListContent(
                        products = state.products,
                        paginationState = paginationState,
                        onProductClick = onProductClick,
                        onLoadMore = { viewModel.loadNextPage() }
                    )
                }
            }

            is ProductsUiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = { viewModel.retry() }
                )
            }

            is ProductsUiState.Initial -> {
                // Initial loading
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Pull-to-refresh indicator
        if (uiState is ProductsUiState.Success) {
            PullRefreshIndicator(
                refreshing = uiState is ProductsUiState.Loading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Floating filter button
        FloatingActionButton(
            onClick = onFilterClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Default.FilterList,
                contentDescription = "Filter products"
            )
        }
    }
}

/**
 * Product list with infinite scroll and pagination
 */
@Composable
private fun ProductListContent(
    products: List<Product>,
    paginationState: PaginationState,
    onProductClick: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = remember {
        androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    }

    // Infinite scroll detection
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= products.size - 3 &&
                    paginationState.hasMorePages &&
                    !paginationState.isLoadingMore
                ) {
                    onLoadMore()
                }
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = products,
            key = { it.id }
        ) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) },
                modifier = Modifier.animateItemPlacement()
            )
        }

        // Loading indicator at bottom
        if (paginationState.isLoadingMore) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

/**
 * Individual product card with image, title, price, and rating
 */
@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // Product image with progressive loading
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.formattedPrice,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = product.rating.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

/**
 * Shimmer loading effect for product cards
 */
@Composable
private fun ProductListShimmer() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(6) {
            ShimmerProductCard()
        }
    }
}

/**
 * Shimmer placeholder for product card
 */
@Composable
private fun ShimmerProductCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shimmerEffect()
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(20.dp)
                        .shimmerEffect()
                )
            }
        }
    }
}

/**
 * Empty state with call-to-action
 */
@Composable
private fun EmptyProductsState(
    onExploreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Products Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Explore our categories to find amazing silver jewelry",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onExploreClick) {
            Text("Explore Categories")
        }
    }
}

/**
 * Error state with retry option
 */
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRetry) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}

// Placeholder data classes for compilation
data class Product(
    val id: String,
    val name: String,
    val imageUrl: String,
    val formattedPrice: String,
    val rating: Double
)

data class PaginationState(
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true,
    val isLoadingMore: Boolean = false
)
