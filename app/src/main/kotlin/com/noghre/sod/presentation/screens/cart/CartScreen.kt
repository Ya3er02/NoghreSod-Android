package com.noghre.sod.presentation.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noghre.sod.R
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.presentation.components.PersianButton

/**
 * Cart Screen - Shopping cart display and management.
 * 
* Shows cart items with quantity control.
 * Displays total and checkout button.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onCheckoutClick: () -> Unit = {}
) {
    val uiState by viewModel.cartUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        CartHeader()

        // Cart content
        if (isLoading && uiState.items.isEmpty()) {
            CartLoadingState()
        } else if (uiState.items.isEmpty()) {
            CartEmptyState()
        } else {
            CartContent(
                items = uiState.items,
                total = uiState.total,
                onQuantityChange = { item, quantity ->
                    viewModel.updateQuantity(item.productId, quantity)
                },
                onRemoveItem = { item ->
                    viewModel.removeItem(item.productId)
                },
                onCheckoutClick = onCheckoutClick
            )
        }
    }
}

@Composable
fun CartHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.cart),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun CartContent(
    items: List<CartItem>,
    total: Double,
    onQuantityChange: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onCheckoutClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = items,
                key = { it.productId }
            ) { item ->
                CartItemCard(
                    item = item,
                    onQuantityChange = { quantity ->
                        onQuantityChange(item, quantity)
                    },
                    onRemove = {
                        onRemoveItem(item)
                    }
                )
            }
        }

        // Summary and checkout
        CartSummary(
            total = total,
            itemCount = items.size,
            onCheckoutClick = onCheckoutClick
        )
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image placeholder
            Card(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(6.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.productName.first().toString(),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            // Product info and controls
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "${item.price} ریال",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // Quantity control
                Row(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (item.quantity > 1) onQuantityChange(item.quantity - 1) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("-")
                    }

                    Text(
                        text = item.quantity.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(20.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    IconButton(
                        onClick = { onQuantityChange(item.quantity + 1) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("+")
                    }
                }
            }

            // Delete button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun CartSummary(
    total: Double,
    itemCount: Int,
    onCheckoutClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "مجموع (بخش: $itemCount)",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$total ریال",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Divider()

            // Checkout button
            PersianButton(
                text = "اقدام به پرداخت",
                onClick = onCheckoutClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CartLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CartEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "سبد خريد خالی است",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
