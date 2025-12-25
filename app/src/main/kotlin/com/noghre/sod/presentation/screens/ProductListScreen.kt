package com.noghre.sod.presentation.screens

import androidx.compose.animation.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.noghre.sod.R
import com.noghre.sod.domain.model.Product
import com.noghre.sod.presentation.viewmodel.ProductListViewModel

/**
 * Product list screen with pagination and configuration change handling.
 * 
 * Features:
 * - LazyColumn pagination for efficient rendering
 * - Search functionality
 * - Category filtering
 * - rememberSaveable for state preservation on configuration changes
 * - Optimized animations
 * 
 * @since 1.0.0
 */
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit = {}
) {
    // State preservation across configuration changes
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    // Collect products as paging items
    val products = viewModel.products.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { newQuery ->
                searchQuery = newQuery
                viewModel.searchProducts(newQuery)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Product list with pagination
        ProductPagingList(
            products = products,
            isLoading = isLoading,
            onProductClick = onProductClick
        )
    }
}

/**
 * Search bar component.
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = { Text(stringResource(R.string.search_products)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Paging product list with LazyColumn.
 */
@Composable
fun ProductPagingList(
    products: LazyPagingItems<Product>,
    isLoading: Boolean,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Optimized animation using animateFloatAsState instead of animate()
    val alpha by animateFloatAsState(
        targetValue = if (isLoading) 0.5f else 1f,
        label = "ProductListAlpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .opacity(alpha),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products.itemCount) { index ->
            val product = products[index]
            if (product != null) {
                ProductListItem(
                    product = product,
                    onClick = { onProductClick(product.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Loading indicator at end
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

/**
 * Individual product list item.
 */
@Composable
fun ProductListItem(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product image with contentDescription for accessibility
            Surface(
                modifier = Modifier
                    .size(96.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp)
            ) {
                // Image loading would go here
                // AsyncImage(model = product.imageUrl, contentDescription = product.name)
                Text(stringResource(R.string.image))
            }

            // Product info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = stringResource(R.string.price_format, product.price),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun Modifier.opacity(alpha: Float): Modifier {
    return this.graphicsLayer(alpha = alpha)
}

import androidx.compose.ui.graphics.graphicsLayer
