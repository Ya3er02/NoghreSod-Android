package com.noghre.sod.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.common.UiState
import com.noghre.sod.presentation.components.ErrorView
import com.noghre.sod.presentation.components.EmptyView
import com.noghre.sod.presentation.components.LoadingView
import com.noghre.sod.presentation.components.LoadingListItem
import com.noghre.sod.presentation.viewmodel.CartViewModel
import timber.log.Timber

@Composable
fun CartScreen(
    onNavigate: (String) -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val totalPrice by viewModel.totalPrice.collectAsStateWithLifecycle()
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
            LoadingView(message = "سبد خرید بارگذاری می‌شود...")
        }
        is UiState.Success -> {
            CartContent(
                items = uiState.data,
                totalPrice = totalPrice,
                onRemoveItem = { itemId ->
                    Timber.d("Remove item: $itemId")
                    viewModel.onRemoveFromCart(itemId)
                },
                onCheckout = {
                    Timber.d("Checkout")
                    onNavigate("checkout")
                },
                onContinueShopping = {
                    Timber.d("Continue shopping")
                    onNavigate("products")
                }
            )
        }
        is UiState.Error -> {
            ErrorView(
                error = uiState.error,
                onRetry = { viewModel.loadCartItems() }
            )
        }
        UiState.Empty -> {
            EmptyView(
                message = "سبد خرید خالی است",
                actionText = "بروی خرید",
                onAction = { onNavigate("products") }
            )
        }
    }
}

@Composable
private fun CartContent(
    items: List<CartItem>,
    totalPrice: Double,
    onRemoveItem: (String) -> Unit,
    onCheckout: () -> Unit,
    onContinueShopping: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Cart Items
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = items,
                key = { it.id }
            ) { item ->
                CartItemRow(
                    item = item,
                    onRemove = { onRemoveItem(item.id) }
                )
            }
        }
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        // Total Price
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "مجموع:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$totalPrice تومان",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        // Buttons
        Button(
            onClick = onCheckout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(رفتن به پگیرابی")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedButton(
            onClick = onContinueShopping,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("ادامه خرید")
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(item.product.name)
                Text(
                    text = "${item.product.price} تومان x ${item.quantity}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Button(
                onClick = onRemove,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("حذف")
            }
        }
    }
}
