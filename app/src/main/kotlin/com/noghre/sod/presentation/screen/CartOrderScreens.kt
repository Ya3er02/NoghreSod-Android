package com.noghre.sod.presentation.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.noghre.sod.domain.model.Cart
import com.noghre.sod.domain.model.CartItem
import com.noghre.sod.domain.model.Order
import com.noghre.sod.presentation.viewmodel.CartViewModel
import com.noghre.sod.presentation.viewmodel.OrderViewModel
import com.noghre.sod.presentation.viewmodel.UiState

/**
 * Shopping cart screen.
 */
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onCheckout: () -> Unit
) {
    val cart by viewModel.cartState.collectAsState()
    val userId = "current-user-id" // Should come from auth

    LaunchedEffect(Unit) {
        viewModel.getCart(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Cart") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (cart) {
            is UiState.Success -> {
                val cartData = (cart as UiState.Success<Cart>).data
                if (cartData.items.isEmpty()) {
                    EmptyCartMessage(modifier = Modifier.padding(paddingValues))
                } else {
                    CartContent(
                        cart = cartData,
                        viewModel = viewModel,
                        onCheckout = onCheckout,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text((cart as UiState.Error).message)
                }
            }
            else -> {}
        }
    }
}

/**
 * Order history screen.
 */
@Composable
fun OrderListScreen(
    viewModel: OrderViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onOrderClick: (String) -> Unit
) {
    val orders by viewModel.ordersState.collectAsState()
    val userId = "current-user-id" // Should come from auth

    LaunchedEffect(Unit) {
        viewModel.getOrders(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (orders) {
            is UiState.Success -> {
                val orderList = (orders as UiState.Success<List<Order>>).data
                if (orderList.isEmpty()) {
                    EmptyOrdersMessage(modifier = Modifier.padding(paddingValues))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        items(orderList) { order ->
                            OrderListItem(
                                order = order,
                                onClick = { onOrderClick(order.id) }
                            )
                        }
                    }
                }
            }
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text((orders as UiState.Error).message)
                }
            }
            else -> {}
        }
    }
}

/**
 * Order detail and tracking screen.
 */
@Composable
fun OrderDetailScreen(
    orderId: String,
    viewModel: OrderViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val order by viewModel.orderDetailState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getOrderDetail(orderId)
        viewModel.getOrderTracking(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (order) {
            is UiState.Success -> {
                val orderData = (order as UiState.Success<Order>).data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Order Header
                    item {
                        OrderHeader(orderData)
                    }

                    // Order Items
                    item {
                        OrderItemsSection(orderData)
                    }

                    // Tracking
                    item {
                        if (orderData.tracking != null) {
                            TrackingSection(orderData.tracking!!)
                        }
                    }

                    // Order Summary
                    item {
                        OrderSummary(orderData)
                    }

                    // Delivery Address
                    item {
                        DeliveryAddressSection(orderData)
                    }
                }
            }
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text((order as UiState.Error).message)
                }
            }
            else -> {}
        }
    }
}

// ============== HELPER COMPOSABLES ==============

@Composable
private fun CartContent(
    cart: Cart,
    viewModel: CartViewModel,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(cart.items) { item ->
                CartItemCard(item) { quantity ->
                    viewModel.updateCartItem(item.id, quantity)
                }
            }
        }

        Divider()

        // Cart Summary
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "\$${String.format("%.2f", cart.totalPrice)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Proceed to Checkout")
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Image
            AsyncImage(
                model = item.product.imageUrl,
                contentDescription = item.product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Product Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                Text(
                    text = "\$${item.product.price}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (item.quantity > 1) onQuantityChange(item.quantity - 1) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(item.quantity.toString())
                    IconButton(
                        onClick = { onQuantityChange(item.quantity + 1) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderListItem(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.orderNumber}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = order.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = getStatusColor(order.status.name)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\$${String.format("%.2f", order.totalAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun OrderHeader(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Order Number", style = MaterialTheme.typography.labelSmall)
                    Text(
                        order.orderNumber,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Status", style = MaterialTheme.typography.labelSmall)
                    Text(
                        order.status.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = getStatusColor(order.status.name)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderItemsSection(order: Order) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Items", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        order.items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${item.product.name} x${item.quantity}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "\$${String.format("%.2f", item.unitPrice * item.quantity)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun TrackingSection(tracking: com.noghre.sod.domain.model.OrderTracking) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Tracking", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Tracking #: ${tracking.trackingNumber}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            "Carrier: ${tracking.carrier}",
            style = MaterialTheme.typography.bodySmall
        )
        if (tracking.currentLocation != null) {
            Text(
                "Location: ${tracking.currentLocation}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun OrderSummary(order: Order) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Subtotal:", style = MaterialTheme.typography.bodySmall)
            Text("\$${String.format("%.2f", order.subtotal)}")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Shipping:", style = MaterialTheme.typography.bodySmall)
            Text("\$${String.format("%.2f", order.shippingCost)}")
        }
        if (order.discountAmount > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Discount:", style = MaterialTheme.typography.bodySmall)
                Text("-\$${String.format("%.2f", order.discountAmount)}")
            }
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Total:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                "\$${String.format("%.2f", order.totalAmount)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DeliveryAddressSection(order: Order) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Delivery Address", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            order.shippingAddress.fullAddress,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            "${order.shippingAddress.city}, ${order.shippingAddress.province}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun EmptyCartMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = "Empty Cart",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Your cart is empty",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun EmptyOrdersMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.LocalShipping,
                contentDescription = "No Orders",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No orders yet",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun getStatusColor(status: String): androidx.compose.ui.graphics.Color {
    return when (status) {
        "PENDING" -> MaterialTheme.colorScheme.error
        "CONFIRMED" -> MaterialTheme.colorScheme.secondary
        "PROCESSING" -> MaterialTheme.colorScheme.secondary
        "SHIPPED" -> MaterialTheme.colorScheme.primary
        "DELIVERED" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}
