package com.noghre.sod.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noghre.sod.domain.model.Product
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.common.UiState
import com.noghre.sod.presentation.components.ErrorView
import com.noghre.sod.presentation.components.EmptyView
import com.noghre.sod.presentation.components.LoadingView
import com.noghre.sod.presentation.viewmodel.SearchViewModel
import timber.log.Timber

@Composable
fun SearchScreen(
    onNavigate: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Timber.d("Toast: ${event.message}")
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UiEvent.ShowError -> {
                    Timber.e("Error: ${event.error.message}")
                    Toast.makeText(context, event.error.toUserMessage(), Toast.LENGTH_LONG).show()
                }
                is UiEvent.Navigate -> {
                    Timber.d("Navigate: ${event.route}")
                    onNavigate(event.route)
                }
                else -> {}
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.onSearchQueryChanged(it) },
            onSearch = { viewModel.onSearchSubmit() },
            onClear = { viewModel.onSearchQueryChanged("") }
        )
        
        // Results or History
        if (searchQuery.isBlank()) {
            // Show search history
            SearchHistorySection(
                history = searchHistory,
                onHistoryItemClick = { viewModel.onHistoryItemClick(it) },
                onClearHistory = { viewModel.onClearHistory() }
            )
        } else {
            // Show search results
            when (searchResults) {
                UiState.Idle -> Unit
                UiState.Loading -> {
                    LoadingView(message = "جستجو...")
                }
                is UiState.Success -> {
                    SearchResultsContent(
                        products = searchResults.data,
                        onProductClick = { productId ->
                            Timber.d("Product clicked: $productId")
                            onNavigate("product_detail/$productId")
                        }
                    )
                }
                is UiState.Error -> {
                    ErrorView(
                        error = searchResults.error,
                        onRetry = { viewModel.onSearchSubmit() }
                    )
                }
                UiState.Empty -> {
                    EmptyView(
                        message = "هیچ محصولی یافت نشد"
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = { Text("جستجو...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "جستجو"
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "پاك کردن"
                    )
                }
            }
        },
        singleLine = true
    )
}

@Composable
private fun SearchHistorySection(
    history: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    if (history.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(شروع به جستجو...")
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تاریخچه جستجو",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onClearHistory) {
                    Text(پاک کردن")
                }
            }
            
            Divider()
            
            // History Items
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = history,
                    key = { it }
                ) { item ->
                    HistoryItem(
                        text = item,
                        onClick = { onHistoryItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight(Alignment.CenterVertically),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SearchResultsContent(
    products: List<Product>,
    onProductClick: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = products,
            key = { it.id }
        ) { product ->
            SearchResultItem(
                product = product,
                onClick = { onProductClick(product.id) }
            )
        }
    }
}

@Composable
private fun SearchResultItem(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image Placeholder
            Box(
                modifier = Modifier
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(تصویر")
            }
            
            // Product Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${product.price} تومان",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Action Button
            Button(
                onClick = onClick,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(→", fontSize = MaterialTheme.typography.labelSmall.fontSize)
            }
        }
    }
}
