package com.noghre.sod.presentation.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.noghre.sod.R
import com.noghre.sod.domain.model.Product
import com.noghre.sod.presentation.components.FilterBottomSheet
import com.noghre.sod.presentation.components.PersianButton
import com.noghre.sod.presentation.components.PersianTextField
import com.noghre.sod.ui.components.shimmer.shimmer
import kotlinx.coroutines.delay

/**
 * Products Screen - Main product listing screen.
 * 
 * Displays products with search, filter, and pagination.
 * Shows loading states and error handling.
 * Features:
 * - Search with real-time filtering
 * - Advanced filter with price range
 * - Image loading with Coil
 * - Shimmer loading skeleton
 * - Pagination support
 * 
 * @author NoghreSod Team
 * @version 1.1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit = {}
) {
    val uiState by viewModel.productsUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var minPriceFilter by remember { mutableStateOf(0f) }
    var maxPriceFilter by remember { mutableStateOf(100000f) }

    LaunchedEffect(Unit) {
        delay(500) // Small delay for UI initialization
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with search and filter
        ProductsHeader(
            searchQuery = searchQuery,
            onSearchChange = { query ->
                searchQuery = query
                viewModel.searchProducts(query)
            },
            onFilterClick = { showFilterDialog = true }
        )

        // Products list or empty state
        if (isLoading && uiState.products.isEmpty()) {
            ProductsLoadingState()
        } else if (error != null && uiState.products.isEmpty()) {
            ProductsErrorState(
                error = error,
                onRetry = { viewModel.loadProducts() }
            )
        } else if (uiState.products.isEmpty()) {
            ProductsEmptyState()
        } else {
            ProductsList(
                products = uiState.products,
                isLoading = isLoading,
                hasMore = uiState.hasMorePages,
                onProductClick = { product ->
                    viewModel.selectProduct(product)
                    onProductClick(product.id)
                },
                onLoadMore = { viewModel.loadMore() }
            )
        }
    }

    // Filter Bottom Sheet Dialog
    if (showFilterDialog) {
        FilterBottomSheet(
            onDismiss = { showFilterDialog = false },
            onApplyFilter = { priceRange, category ->
                minPriceFilter = priceRange.start
                maxPriceFilter = priceRange.endInclusive
                // Apply filters to viewModel
                viewModel.applyFilters(
                    minPrice = minPriceFilter,
                    maxPrice = maxPriceFilter,
                    category = category
                )
                showFilterDialog = false
            }
        )
    }
}

@Composable
fun ProductsHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.products),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        PersianTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = "جستجو کنید...",
            leadingIcon = Icons.Default.Search,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ProductsList(
    products: List<Product>,
    isLoading: Boolean,
    hasMore: Boolean,
    onProductClick: (Product) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 8.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = products,
            key = { it.id }
        ) { product ->
            ProductCardItem(
                product = product,
                onClick = { onProductClick(product) }
            )
        }

        // Load more indicator
        if (isLoading && hasMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        // Load more button
        if (!isLoading && hasMore && products.isNotEmpty()) {
            item {
                PersianButton(
                    text = "بیشتر",
                    onClick = onLoadMore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }

    // Trigger load more when scrolled to end
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && 
                    lastVisibleIndex >= products.size - 3 && 
                    hasMore && !isLoading
                ) {
                    onLoadMore()
                }
            }
    }
}

@Composable
fun ProductCardItem(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product image with Coil
            ProductImagePlaceholder(
                imageUrl = product.imageUrl,
                productName = product.name,
                modifier = Modifier.size(80.dp)
            )

            // Product details
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
                    overflow = TextOverflow.Ellipsis
                )

                if (!product.description.isNullOrEmpty()) {
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${product.price} ریال",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ProductImagePlaceholder(
    imageUrl: String?,
    productName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp)
    ) {
        if (imageUrl.isNullOrEmpty()) {
            // Fallback placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = productName.first().toString(),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        } else {
            // Load image with Coil - AsyncImage
            AsyncImage(
                model = imageUrl,
                contentDescription = productName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = { placeholder ->
                    // Show shimmer while loading
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shimmer()
                    )
                },
                error = { error ->
                    // Show fallback on error
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.errorContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "خطا",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ProductsLoadingState() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(5) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
                    .shimmer(),
                shape = RoundedCornerShape(8.dp)
            ) {
                // Shimmer skeleton effect applied via modifier
            }
        }
    }
}

@Composable
fun ProductsErrorState(
    error: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "خطا در بارگیری محصولات",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error ?: "خطای نامشخصی رخ داد",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        PersianButton(
            text = "تلاش مجدد",
            onClick = onRetry
        )
    }
}

@Composable
fun ProductsEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "محصولی یافت نشد",
            style = MaterialTheme.typography.titleMedium
        )
    }
}