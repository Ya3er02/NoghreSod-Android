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
import com.noghre.sod.domain.model.Order
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.common.UiState
import com.noghre.sod.presentation.components.ErrorView
import com.noghre.sod.presentation.components.EmptyView
import com.noghre.sod.presentation.components.LoadingView
import com.noghre.sod.presentation.viewmodel.OrderViewModel
import timber.log.Timber

@Composable
fun OrderListScreen(
    onNavigate: (String) -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
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
            LoadingView(message = "درحال بارگذاری سفارشها...")
        }
        is UiState.Success -> {
            OrderListContent(
                orders = uiState.data,
                currentPage = currentPage,
                onOrderClick = { orderId ->
                    Timber.d("Order clicked: $orderId")
                    onNavigate("order_detail/$orderId")
                },
                onNextPage = { viewModel.onNextPage() },
                onPreviousPage = { viewModel.onPreviousPage() }
            )
        }
        is UiState.Error -> {
            ErrorView(
                error = uiState.error,
                onRetry = { viewModel.loadOrders() }
            )
        }
        UiState.Empty -> {
            EmptyView(
                message = "هیچ سفارشی برای شما یافت نشد"
            )
        }
    }
}

@Composable
private fun OrderListContent(
    orders: List<Order>,
    currentPage: Int,
    onOrderClick: (String) -> Unit,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Orders List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = orders,
                key = { it.id }
            ) { order ->
                OrderCard(
                    order = order,
                    onClick = { onOrderClick(order.id) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Pagination
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onPreviousPage,
                enabled = currentPage > 1
            ) {
                Text("قبلی")
            }
            
            Text(
                text = "صفحه $currentPage",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Button(onClick = onNextPage) {
                Text("بعدی")
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Order ID and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "سفارش #${order.id.take(8)}",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = order.date,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status and Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "وضعیت:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    StatusChip(order.status)
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "مبلغ:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${order.totalPrice} تومان",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // View Details Button
            Button(
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .height(32.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                Text(بررسی جزئیات")
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (color, label) = when (status.lowercase()) {
        "pending" -> Pair(MaterialTheme.colorScheme.error, "درانتظار")
        "processing" -> Pair(MaterialTheme.colorScheme.tertiary, "درحال انجام")
        "shipped" -> Pair(MaterialTheme.colorScheme.primary, "ارسال شد")
        "delivered" -> Pair(MaterialTheme.colorScheme.secondary, "تحويل شد")
        "cancelled" -> Pair(MaterialTheme.colorScheme.error, "لغو شد")
        else -> Pair(MaterialTheme.colorScheme.onSurfaceVariant, status)
    }
    
    AssistChip(
        onClick = {},
        label = { Text(label) },
        modifier = Modifier.padding(top = 4.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.2f),
            labelColor = color
        )
    )
}
