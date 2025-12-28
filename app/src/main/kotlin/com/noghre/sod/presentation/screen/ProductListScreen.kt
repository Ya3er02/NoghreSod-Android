package com.noghre.sod.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
import com.noghre.sod.presentation.components.LoadingListItem
import com.noghre.sod.presentation.viewmodel.ProductListViewModel
import timber.log.Timber

@Composable
fun ProductListScreen(
    onNavigate: (String) -> Unit,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Timber.d("Toast event: ${event.message}")
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UiEvent.ShowError -> {
                    Timber.e("Error event: ${event.error.message}")
                    Toast.makeText(context, event.error.toUserMessage(), Toast.LENGTH_LONG).show()
                }
                is UiEvent.Navigate -> {
                    Timber.d("Navigate event: ${event.route}")
                    onNavigate(event.route)
                }
                else -> {
                    Timber.d("Other event: $event")
                }
            }
        }
    }
    
    // Render based on state
    when (uiState) {
        UiState.Idle -> {
            LaunchedEffect(Unit) {
                viewModel.loadProducts()
            }
        }
        UiState.Loading -> {
            LoadingView(message = "درحال بارگذاری...")
        }
        is UiState.Success -> {
            ProductListContent(
                products = uiState.data,
                onProductClick = { productId ->
                    Timber.d("Product clicked: $productId")
                    onNavigate("product_detail/$productId")
                },
                onRefresh = { viewModel.loadProducts() }
            )
        }
        is UiState.Error -> {
            ErrorView(
                error = uiState.error,
                onRetry = { viewModel.loadProducts() }
            )
        }
        UiState.Empty -> {
            EmptyView(
                message = "محصولی یافت نشد",
                actionText = "تلاش مجدد",
                onAction = { viewModel.loadProducts() }
            )
        }
    }
}

@Composable
private fun ProductListContent(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = products,
            key = { it.id }
        ) { product ->
            ProductListItemCard(
                product = product,
                onClick = { onProductClick(product.id) }
            )
        }
    }
}

@Composable
private fun ProductListItemCard(
    product: Product,
    onClick: () -> Unit
) {
    // Implement product card UI
    Timber.d("Rendering product: ${product.name}")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
