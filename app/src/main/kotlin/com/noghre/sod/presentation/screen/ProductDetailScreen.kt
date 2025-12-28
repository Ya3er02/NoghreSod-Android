package com.noghre.sod.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import com.noghre.sod.presentation.components.LoadingView
import com.noghre.sod.presentation.viewmodel.ProductDetailViewModel
import timber.log.Timber

@Composable
fun ProductDetailScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val quantity by viewModel.quantity.collectAsStateWithLifecycle()
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
            LoadingView(message = "جزئیات بارگذاری می‌شود...")
        }
        is UiState.Success -> {
            ProductDetailContent(
                product = uiState.data,
                quantity = quantity,
                onQuantityIncrease = { viewModel.onQuantityIncrease() },
                onQuantityDecrease = { viewModel.onQuantityDecrease() },
                onAddToCart = { viewModel.onAddToCartClick() },
                onFavoriteClick = { viewModel.onFavoriteClick() },
                onBackClick = onBackClick
            )
        }
        is UiState.Error -> {
            ErrorView(
                error = uiState.error,
                onRetry = { viewModel.loadProductDetail() }
            )
        }
        UiState.Empty -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("محصول یافت نشد")
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: Product,
    quantity: Int,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    onAddToCart: () -> Unit,
    onFavoriteClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBackClick) {
                Text("بازگشت")
            }
            Button(onClick = onFavoriteClick) {
                Text("❤️ علاقه")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Product Info
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text(
            text = "${product.price} تومان",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quantity Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onQuantityDecrease) {
                Text("-")
            }
            
            Text(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            
            Button(onClick = onQuantityIncrease) {
                Text("+")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Add to Cart Button
        Button(
            onClick = onAddToCart,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("افزودن به سبد خرید")
        }
    }
}
