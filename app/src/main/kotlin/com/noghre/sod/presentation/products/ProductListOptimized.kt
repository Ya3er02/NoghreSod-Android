package com.noghre.sod.presentation.products

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noghre.sod.domain.model.Product
import com.noghre.sod.presentation.common.UiState
import com.noghre.sod.presentation.components.EmptyView
import com.noghre.sod.presentation.components.ErrorView
import com.noghre.sod.presentation.components.LoadingView
import timber.log.Timber

/**
 * 🚀 Optimized Product List Screen
 * 
 * Demonstrates Compose performance optimization patterns:
 * - remember for expensive computations
 * - derivedStateOf for derived state
 * - LazyColumn with proper keys
 * - Minimal recompositions
 */
@Composable
fun ProductListScreenOptimized(
    viewModel: ProductListViewModel,
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit = {},
) {
    Timber.d("[ProductListOptimized] Composing ProductListScreen")
    
    // Collect UI state
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    
    // ============================================
    // 1. EXPENSIVE COMPUTATION - Use remember
    // ============================================
    
    /**
     * Expensive calculation - only recompute when inputs change
     * Without remember: recalculated on every recomposition
     * With remember: calculated only when products change
     */
    val statistics = remember(uiState) {
        when (uiState) {
            is UiState.Success -> {
                val products = uiState.data
                val totalProducts = products.size
                val averagePrice = if (products.isNotEmpty()) {
                    products.map { it.price }.average()
                } else 0.0
                val highRatedProducts = products.count { it.rating >= 4.0 }
                
                Timber.d("[ProductListOptimized] Statistics recalculated: $totalProducts products")
                
                ProductStatistics(
                    totalCount = totalProducts,
                    averagePrice = averagePrice,
                    highRatedCount = highRatedProducts,
                )
            }
            else -> ProductStatistics(0, 0.0, 0)
        }
    }
    
    // ============================================
    // 2. DERIVED STATE - Use derivedStateOf
    // ============================================
    
    /**
     * Derived state that depends on other state
     * Without derivedStateOf: child recomposes when parent recomposes
     * With derivedStateOf: only recomposes when derived value changes
     */
    val isEmptyOrLoading = remember {
        derivedStateOf {
            when (uiState) {
                UiState.Empty -> true
                UiState.Loading -> true
                is UiState.Success -> uiState.data.isEmpty()
                else -> false
            }
        }
    }
    
    /**
     * Filtered and sorted products
     * Expensive operation: only recalculate when needed
     */
    val filteredAndSortedProducts = remember(uiState, searchQuery, selectedCategory) {
        when (uiState) {
            is UiState.Success -> {
                var filtered = uiState.data
                
                // Apply category filter
                if (selectedCategory != null) {
                    filtered = filtered.filter { it.categoryId == selectedCategory }
                }
                
                // Apply search filter
                if (searchQuery.isNotEmpty()) {
                    filtered = filtered.filter { product ->
                        product.name.contains(searchQuery, ignoreCase = true) ||
                        product.description.contains(searchQuery, ignoreCase = true)
                    }
                }
                
                // Sort by rating (descending)
                filtered.sortedByDescending { it.rating }
            }
            else -> emptyList()
        }
    }
    
    /**
     * Has any filters applied
     * Derived state for UI indication
     */
    val hasFiltersApplied = remember {
        derivedStateOf {
            searchQuery.isNotEmpty() || selectedCategory != null
        }
    }
    
    // ============================================
    // 3. RENDER UI
    // ============================================
    
    when (uiState) {
        UiState.Idle -> {} // Do nothing
        
        UiState.Loading -> LoadingView(modifier = modifier)
        
        is UiState.Success -> {
            if (filteredAndSortedProducts.isEmpty()) {
                EmptyView(
                    message = if (hasFiltersApplied.value) 
                        "نتیجه‌ای یافت نشد" 
                    else 
                        "محصولی موجود نیست",
                    modifier = modifier
                )
            } else {
                // Optimized LazyColumn with keys
                LazyColumn(modifier = modifier) {
                    // Header with statistics
                    item {
                        ProductListHeader(
                            statistics = statistics,
                            hasFilters = hasFiltersApplied.value,
                            filterCount = {
                                var count = 0
                                if (searchQuery.isNotEmpty()) count++
                                if (selectedCategory != null) count++
                                count
                            }()
                        )
                    }
                    
                    // Product list with proper keys
                    items(
                        items = filteredAndSortedProducts,
                        key = { product -> product.id }  // ✅ IMPORTANT: Proper key for reordering
                    ) { product ->
                        ProductListItem(
                            product = product,
                            onProductClick = { onProductClick(product.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(product) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
        
        is UiState.Error -> {
            ErrorView(
                error = uiState.error,
                onRetry = { viewModel.retry() },
                modifier = modifier
            )
        }
        
        UiState.Empty -> {
            EmptyView(modifier = modifier)
        }
    }
}

// ============================================
// Supporting Models & Components
// ============================================

/**
 * Statistics data class
 */
data class ProductStatistics(
    val totalCount: Int,
    val averagePrice: Double,
    val highRatedCount: Int,
)

/**
 * 💳 Product list header showing statistics
 */
@Composable
private fun ProductListHeader(
    statistics: ProductStatistics,
    hasFilters: Boolean,
    filterCount: Int,
    modifier: Modifier = Modifier,
) {
    // Header composition with statistics
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "${statistics.totalCount} محصول",
            style = MaterialTheme.typography.headlineSmall
        )
        
        if (hasFilters) {
            Text(
                text = "$filterCount فیلتر فعال است",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        if (statistics.highRatedCount > 0) {
            Text(
                text = "${statistics.highRatedCount} محصول با رتبه ⭐ 4.0 و بالاتر",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * 📕 Product list item
 */
@Composable
private fun ProductListItem(
    product: Product,
    onProductClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Item composition
    androidx.compose.foundation.layout.Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = "${product.price} تومان",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/**
 * Preview for the optimized screen
 */
@Composable
fun ProductListScreenOptimizedPreview() {
    // Preview implementation
}
