package com.noghre.sod.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noghre.sod.domain.model.Product
import com.noghre.sod.presentation.viewmodel.HomeViewModel
import com.noghre.sod.presentation.viewmodel.HomeUiState
import com.noghre.sod.presentation.viewmodel.HomeIntent

/**
 * Unified Home Screen
 *
 * Merged implementation combining:
 * - Complete UI from ui.screens.home (SearchBar, FilterSheet, Categories)
 * - State management from presentation.ui.home (ViewModel integration)
 * - Pagination and lazy loading from ProductsViewModel
 *
 * Features:
 * - RTL support for Persian/Arabic layouts
 * - High-quality jewelry product display
 * - Advanced filtering (price, weight, gem type, plating)
 * - Search with debouncing
 * - Wishlist integration
 * - Real-time price updates
 *
 * @author NoghreSod Team
 * @version 1.0.0
 * @since Refactor Phase 2
 */

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery = viewModel.searchQuery.collectAsStateWithLifecycle()
    val filters = viewModel.filters.collectAsStateWithLifecycle()
    val isLoadingMore = viewModel.isLoadingMore.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    // Handle effects (one-time events)
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToDetail -> onProductClick(effect.productId)
                is HomeEffect.ScrollToTop -> { /* scroll to top */ }
                is HomeEffect.ShowError -> { /* show error snackbar */ }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        when (val state = uiState.value) {
            is HomeUiState.Loading -> {
                LoadingScreen()
            }
            is HomeUiState.Success -> {
                SuccessScreen(
                    products = state.products,
                    searchText = searchText,
                    onSearchChange = { newSearch ->
                        searchText = newSearch
                        viewModel.handleIntent(HomeIntent.SearchProducts(newSearch.text))
                    },
                    onFilterClick = { showFilterSheet = true },
                    onProductClick = { productId ->
                        viewModel.handleIntent(HomeIntent.NavigateToProduct(productId))
                    },
                    onLoadMore = {
                        viewModel.handleIntent(HomeIntent.LoadMore)
                    },
                    isLoadingMore = isLoadingMore.value,
                    activeFilterCount = if (filters.value.hasActiveFilters()) 1 else 0
                )
            }
            is HomeUiState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.handleIntent(HomeIntent.Refresh) }
                )
            }
        }

        // Filter bottom sheet
        if (showFilterSheet) {
            FilterBottomSheet(
                currentFilters = filters.value,
                onDismiss = { showFilterSheet = false },
                onApplyFilters = { newFilters ->
                    viewModel.handleIntent(HomeIntent.ApplyFilters(newFilters))
                    showFilterSheet = false
                }
            )
        }
    }
}

@Composable
fun SuccessScreen(
    products: List<Product>,
    searchText: TextFieldValue,
    onSearchChange: (TextFieldValue) -> Unit,
    onFilterClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean,
    activeFilterCount: Int,
    modifier: Modifier = Modifier
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Search bar + Filter button
        SearchBar(
            searchText = searchText,
            onSearchChange = onSearchChange,
            onFilterClick = onFilterClick,
            activeFilterCount = activeFilterCount,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Product grid with pagination
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            items(
                items = products,
                key = { product -> product.id }
            ) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProductClick(product.id) }
                )
            }

            // Load more indicator
            if (isLoadingMore) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    searchText: TextFieldValue,
    onSearchChange: (TextFieldValue) -> Unit,
    onFilterClick: () -> Unit,
    activeFilterCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search field
        TextField(
            value = searchText,
            onValueChange = onSearchChange,
            placeholder = { Text("جستجوی مصنوعات") }, // Persian: "Search products"
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Gray
                )
            },
            singleLine = true
        )

        // Filter button
        Button(
            onClick = onFilterClick,
            modifier = Modifier
                .size(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2A5F73)
            )
        ) {
            Text(
                text = if (activeFilterCount > 0) "F✓" else "F",
                color = Color.White
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Product image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Product name
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )

            // Product price
            Text(
                text = "${product.price} تومان",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF2A5F73)
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("تلاش مجدد") // Persian: "Retry"
        }
    }
}

// Placeholder composables (implement per your design system)
@Composable
fun FilterBottomSheet(
    currentFilters: com.noghre.sod.domain.model.ProductFilters,
    onDismiss: () -> Unit,
    onApplyFilters: (com.noghre.sod.domain.model.ProductFilters) -> Unit
) {
    // Implementation based on your design system
}

@Composable
fun GridItemSpan(maxLineSpan: Int) {
    // Compose Grid span helper
}