package com.noghre.sod.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.noghre.sod.presentation.viewmodel.FavoritesViewModel
import timber.log.Timber

@Composable
fun FavoritesScreen(
    onNavigate: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
    
    // Render based on state
    when (uiState) {
        UiState.Idle, UiState.Loading -> {
            LoadingView(message = "درحال بارگذاری علاقه‌مندی‌ها...")
        }
        is UiState.Success -> {
            FavoritesContent(
                products = uiState.data,
                onProductClick = { productId ->
                    Timber.d("Product clicked: $productId")
                    onNavigate("product_detail/$productId")
                },
                onRemoveFavorite = { productId ->
                    Timber.d("Remove from favorites: $productId")
                    viewModel.onRemoveFromFavorites(productId)
                },
                onAddToCart = { productId ->
                    Timber.d("Add to cart: $productId")
                    viewModel.onAddToCart(productId)
                }
            )
        }
        is UiState.Error -> {
            ErrorView(
                error = uiState.error,
                onRetry = { viewModel.loadFavorites() }
            )
        }
        UiState.Empty -> {
            EmptyView(
                message = "هیچ محصولی در علاقه‌مندی‌ها نیست",
                actionText = "بروی خرید",
                onAction = { onNavigate("products") }
            )
        }
    }
}

@Composable
private fun FavoritesContent(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    onRemoveFavorite: (String) -> Unit,
    onAddToCart: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = products,
            key = { it.id }
        ) { product ->
            FavoriteProductCard(
                product = product,
                onProductClick = { onProductClick(product.id) },
                onRemoveFavorite = { onRemoveFavorite(product.id) },
                onAddToCart = { onAddToCart(product.id) }
            )
        }
    }
}

@Composable
private fun FavoriteProductCard(
    product: Product,
    onProductClick: () -> Unit,
    onRemoveFavorite: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Product Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "تصویر",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Product Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Product Name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2
                )
                
                // Product Price
                Text(
                    text = "${product.price} تومان",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // View Details Button
                Button(
                    onClick = onProductClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(ما را ببین")
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = onRemoveFavorite,
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(حذف", fontSize = MaterialTheme.typography.labelSmall.fontSize)
                    }
                    
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(سبد", fontSize = MaterialTheme.typography.labelSmall.fontSize)
                    }
                }
            }
        }
    }
}
