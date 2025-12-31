package com.noghre.sod.presentation.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noghre.sod.presentation.components.PersianButton

/**
 * Orders Screen - User's order history.
 * 
* Shows all orders with status tracking.
 * Order details and reorder option.
 * Filter by status.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onOrderDetailsClick: (String) -> Unit = {}
) {
    val uiState by viewModel.ordersUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedStatus by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top app bar
        TopAppBar(
            title = { Text("سفارش های من") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        )

        // Status filter
        StatusFilterChips(
            statuses = uiState.statuses,
            selectedStatus = selectedStatus,
            onStatusSelect = { status ->
                selectedStatus = if (selectedStatus == status) null else status
                viewModel.filterByStatus(status)
            }
        )

        // Orders list
        if (isLoading && uiState.orders.isEmpty()) {
            OrdersLoadingState()
        } else if (uiState.orders.isEmpty()) {
            OrdersEmptyState()
        } else {
            OrdersList(
                orders = uiState.orders,
                onOrderClick = onOrderDetailsClick
            )
        }
    }
}

@Composable
fun StatusFilterChips(
    statuses: List<String>,
    selectedStatus: String?,
    onStatusSelect: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(statuses) { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = { onStatusSelect(status) },
                label = { Text(translateStatus(status)) }
            )
        }
    }
}

@Composable
fun OrdersList(
    orders: List<Order>,
    onOrderClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
}

@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Order header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "#${order.orderNumber}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order.createdDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = order.total,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Chip(
                        label = { Text(translateStatus(order.status)) },
                        colors = when (order.status) {
                            "delivered" -> ChipDefaults.chipColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                            "cancelled" -> ChipDefaults.chipColors(
                                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            )
                            else -> ChipDefaults.chipColors()
                        }
                    )
                }
            }

            Divider()

            // Items summary
            Text(
                text = "${order.itemCount} محصول",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Delivery info
            if (order.estimatedDelivery != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "تحویل در: ${order.estimatedDelivery}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun OrdersLoadingState() {
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
fun OrdersEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocalShipping,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "هیچ سفارشی وجود ندارد",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "از حالا برای خريد محصولات اشتروع کنید",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun translateStatus(status: String): String {
    return when (status) {
        "pending" -> "در انتظار"
        "confirmed" -> "تأئيد شده"
        "shipped" -> "ارسال شده"
        "delivered" -> "تحويل بابه رسيد"
        "cancelled" -> "لغو شده"
        else -> status
    }
}

// Data classes
data class Order(
    val id: String,
    val orderNumber: String,
    val createdDate: String,
    val status: String,
    val total: String,
    val itemCount: Int,
    val estimatedDelivery: String? = null
)

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val statuses: List<String> = listOf(
        "pending",
        "confirmed",
        "shipped",
        "delivered",
        "cancelled"
    )
)
