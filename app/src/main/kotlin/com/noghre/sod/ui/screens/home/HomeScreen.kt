package com.noghre.sod.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.noghre.sod.R
import com.noghre.sod.domain.entities.Category
import com.noghre.sod.domain.entities.Product
import com.noghre.sod.ui.components.*
import com.noghre.sod.ui.theme.NoghreSodTheme

/**
 * 🏠 Home Screen - Main shopping interface
 *
 * Features:
 * - Featured products carousel
 * - Product categories grid
 * - Search functionality
 * - Filter options
 * - Load more pagination
 *
 * @param viewModel Injected HomeViewModel
 * @param onNavigateToProduct Callback for product navigation
 * @param onNavigateToCart Callback for cart navigation
 * @param modifier Compose modifier
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var showFilterBottomSheet by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            HomeTopBar(
                onCartClick = onNavigateToCart,
                onFilterClick = { showFilterBottomSheet = true }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                LoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is HomeUiState.Success -> {
                HomeContent(
                    data = state.data,
                    searchQuery = searchQuery,
                    onSearchQuery = viewModel::setSearchQuery,
                    onProductClick = onNavigateToProduct,
                    onCategoryClick = viewModel::filterByCategory,
                    onLoadMore = viewModel::loadMoreProducts,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is HomeUiState.Error -> {
                ErrorState(
                    error = state.error,
                    onRetry = viewModel::loadHomeData,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
    
    // Filter Bottom Sheet
    if (showFilterBottomSheet) {
        FilterBottomSheet(
            onDismiss = { showFilterBottomSheet = false },
            onApplyFilter = { priceRange, category ->
                viewModel.applyFilters(priceRange, category)
                showFilterBottomSheet = false
            }
        )
    }
}

/**
 * Top app bar for home screen
 */
@Composable
private fun HomeTopBar(
    onCartClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = "🌟 نوقره‌سُد",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = "فیلتر"
                )
            }
            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "سبد خرید"
                )
            }
        },
        modifier = modifier
    )
}

/**
 * Main content of home screen
 */
@Composable
private fun HomeContent(
    data: HomeData,
    searchQuery: String,
    onSearchQuery: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // Search Bar
        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
        
        // Featured Products
        if (data.featuredProducts.isNotEmpty()) {
            item {
                Text(
                    text = "محصولات برتر",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    items(data.featuredProducts) { product ->
                        ProductCard(
                            product = product,
                            onProductClick = onProductClick,
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            }
        }
        
        // Categories
        if (data.categories.isNotEmpty()) {
            item {
                Text(
                    text = "دسته‌بندی‌ها",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                CategoriesGrid(
                    categories = data.categories,
                    onCategoryClick = onCategoryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
        }
        
        // All Products
        if (data.allProducts.isNotEmpty()) {
            item {
                Text(
                    text = "تمام محصولات",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(data.allProducts) { product ->
                ProductListItem(
                    product = product,
                    onProductClick = onProductClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            item {
                EmptyState(
                    message = "محصولی یافت نشد",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                )
            }
        }
        
        // Load More Button
        item {
            Button(
                onClick = onLoadMore,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("بیشتر بارگذاری کنید")
            }
        }
    }
}

/**
 * Categories grid layout
 */
@Composable
private fun CategoriesGrid(
    categories: List<Category>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        categories.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Spacer for odd items
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Category card component
 */
@Composable
private fun CategoryCard(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = category.icon ?: "📦",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

/**
 * Product list item for vertical scrolling
 */
@Composable
private fun ProductListItem(
    product: Product,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(vertical = 8.dp)
            .clickable(onClick = { onProductClick(product.id) }),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Image
            AsyncImage(
                model = product.images.firstOrNull()?.url,
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Product Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${product.price.amount} تومان",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "⭐ ${product.rating.average}",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "(${product.rating.count})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

data class HomeData(
    val featuredProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val allProducts: List<Product> = emptyList()
)

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val data: HomeData) : HomeUiState()
    data class Error(val error: Exception) : HomeUiState()
}
