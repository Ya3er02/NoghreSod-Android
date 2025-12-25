package com.noghre.sod.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noghre.sod.domain.model.Product
import com.noghre.sod.presentation.component.ProductCardOptimized
import com.noghre.sod.presentation.viewmodel.ProductsViewModelImproved
import timber.log.Timber

/**
 * Optimized Products Screen with proper Compose best practices:
 * - Lazy loading with pagination
 * - Memory-efficient list with keys
 * - Proper state management
 * - Image caching via Coil
 */
@Composable
fun ProductsScreenOptimized(
    viewModel: ProductsViewModelImproved = hiltViewModel(),
    onProductClick: (Product) -> Unit = {}
) {
    // Collect state with lifecycle awareness
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()
    val errorState by viewModel.errorState.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    // Initialize products loading on first composition
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is ProductsViewModelImproved.UiState.Loading -> {
                    LoadingState()
                }
                is ProductsViewModelImproved.UiState.Success -> {
                    val products = (uiState as ProductsViewModelImproved.UiState.Success).data
                    ProductsList(
                        products = products,
                        isLoadingMore = isLoadingMore,
                        onProductClick = { product ->
                            viewModel.selectProduct(product)
                            onProductClick(product)
                        },
                        onLoadMore = { viewModel.loadMoreProducts() }
                    )
                }
                is ProductsViewModelImproved.UiState.Error -> {
                    val errorMessage = (uiState as ProductsViewModelImproved.UiState.Error).message
                    ErrorState(
                        message = errorMessage,
                        onRetry = { viewModel.retry() }
                    )
                }
                is ProductsViewModelImproved.UiState.Empty -> {
                    EmptyState()
                }
                is ProductsViewModelImproved.UiState.Idle -> {
                    // Initial state, do nothing
                }
            }
        }
    }
}

/**
 * Products list with optimized pagination and memory usage.
 */
@Composable
fun ProductsList(
    products: List<Product>,
    isLoadingMore: Boolean,
    onProductClick: (Product) -> Unit,
    onLoadMore: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    
    // Detect when user scrolls near bottom
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = products.size
            val lastVisibleIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= (totalItems - 5) && !isLoadingMore && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            Timber.d("Loading more products...")
            onLoadMore()
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
    ) {
        items(
            items = products,
            key = { product -> product.id }, // Critical for optimization!
            contentType = { "product_card" }
        ) { product ->
            ProductCardOptimized(
                product = product,
                onClick = onProductClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }

        // Loading more indicator
        if (isLoadingMore) {
            item {
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
 * Loading state UI.
 */
@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "درحال بارگذاری محصولات...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Error state UI.
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = onRetry
            ) {
                Text("تلاش مجدد")
            }
        }
    }
}

/**
 * Empty state UI.
 */
@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "محصولی یافت نشد",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
