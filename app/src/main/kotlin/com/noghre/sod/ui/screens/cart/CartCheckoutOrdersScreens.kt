package com.noghre.sod.ui.screens.cart

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.noghre.sod.domain.entities.CartItem
import com.noghre.sod.domain.entities.Order
import com.noghre.sod.ui.components.EmptyState
import com.noghre.sod.ui.components.ErrorState
import com.noghre.sod.ui.components.LoadingState

// ==================== CART SCREEN ====================

/**
 * 🛒 Shopping Cart Screen
 */
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onNavigateToCheckout: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛒 سبد خرید") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "برگشت")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState) {
            is CartUiState.Empty -> {
                EmptyState(
                    message = "🛒 سبد خرید خالی است",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is CartUiState.Loading -> {
                LoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is CartUiState.Success -> {
                CartContent(
                    cartData = state.cartData,
                    onRemoveItem = viewModel::removeFromCart,
                    onUpdateQuantity = viewModel::updateQuantity,
                    onNavigateToProduct = onNavigateToProduct,
                    onCheckout = onNavigateToCheckout,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is CartUiState.Error -> {
                ErrorState(
                    error = state.error,
                    onRetry = viewModel::loadCart,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun CartContent(
    cartData: CartData,
    onRemoveItem: (String) -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartData.items) { item ->
                CartItemCard(
                    item = item,
                    onRemove = { onRemoveItem(item.productId) },
                    onQuantityChange = { newQuantity ->
                        onUpdateQuantity(item.productId, newQuantity)
                    },
                    onProductClick = { onNavigateToProduct(item.productId) }
                )
            }
        }
        
        // Summary
        CartSummary(
            subtotal = cartData.subtotal,
            discount = cartData.discount,
            tax = cartData.tax,
            total = cartData.total,
            onCheckout = onCheckout,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.productName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onProductClick() },
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.price} تومان",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (item.quantity > 1) onQuantityChange(item.quantity - 1) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = null, modifier = Modifier.size(12.dp))
                    }
                    Text(
                        text = item.quantity.toString(),
                        modifier = Modifier.width(20.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    IconButton(
                        onClick = { onQuantityChange(item.quantity + 1) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                    }
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

@Composable
private fun CartSummary(
    subtotal: Long,
    discount: Long,
    tax: Long,
    total: Long,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("مجموع")
                Text("$subtotal تومان", fontWeight = FontWeight.Bold)
            }
            if (discount > 0) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("تخفيف", color = MaterialTheme.colorScheme.primary)
                    Text("-$discount تومان", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("ماليات")
                Text("$tax تومان", fontWeight = FontWeight.Bold)
            }
            Divider()
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("جمع كل: ", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("$total تومان", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) {
                Text("رفتن به سفارش")
            }
        }
    }
}

data class CartData(
    val items: List<CartItem>,
    val subtotal: Long,
    val discount: Long,
    val tax: Long,
    val total: Long
)

sealed class CartUiState {
    object Empty : CartUiState()
    object Loading : CartUiState()
    data class Success(val cartData: CartData) : CartUiState()
    data class Error(val error: Exception) : CartUiState()
}

// ==================== CHECKOUT SCREEN ====================

/**
 * 🛍️ Checkout Screen
 */
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onOrderSuccess: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedPaymentMethod by remember { mutableStateOf("card") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("رفت رسانی") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "برگشت")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState) {
            is CheckoutUiState.Loading ->
                LoadingState(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding))
            is CheckoutUiState.Success -> {
                CheckoutContent(
                    checkoutData = state.checkoutData,
                    selectedPaymentMethod = selectedPaymentMethod,
                    onPaymentMethodChange = { selectedPaymentMethod = it },
                    onPlaceOrder = { viewModel.placeOrder(selectedPaymentMethod) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is CheckoutUiState.Error ->
                ErrorState(
                    error = state.error,
                    onRetry = viewModel::loadCheckoutData,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            is CheckoutUiState.OrderPlaced ->
                onOrderSuccess(state.orderId)
        }
    }
}

@Composable
private fun CheckoutContent(
    checkoutData: CheckoutData,
    selectedPaymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    onPlaceOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
        item { Text("آدرس تحویل", style = MaterialTheme.typography.titleMedium) }
        item {
            AddressCard(checkoutData.shippingAddress)
        }
        item { Text("روش بردارفت", style = MaterialTheme.typography.titleMedium) }
        item {
            PaymentMethodCard(
                selectedMethod = selectedPaymentMethod,
                onMethodChange = onPaymentMethodChange
            )
        }
        item { Text("خلاصه سفارش", style = MaterialTheme.typography.titleMedium) }
        item { OrderSummaryCard(checkoutData) }
        item {
            Button(onClick = onPlaceOrder, modifier = Modifier.fillMaxWidth()) {
                Text("ثبت سفارش")
            }
        }
    }
}

@Composable
private fun AddressCard(address: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(address, modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun PaymentMethodCard(
    selectedMethod: String,
    onMethodChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            "card" to "كارت بانکی",
            "wallet" to "كیف دیجيتالی",
            "bank_transfer" to "انالتر بانکی"
        ).forEach { (method, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMethodChange(method) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selectedMethod == method, onClick = { onMethodChange(method) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(label)
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(checkoutData: CheckoutData) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("مجموع")
                Text("${checkoutData.subtotal} تومان")
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("ماليات")
                Text("${checkoutData.tax} تومان")
            }
            Divider()
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("جمع")
                Text("${checkoutData.total} تومان", fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class CheckoutData(
    val shippingAddress: String,
    val subtotal: Long,
    val tax: Long,
    val total: Long
)

sealed class CheckoutUiState {
    object Loading : CheckoutUiState()
    data class Success(val checkoutData: CheckoutData) : CheckoutUiState()
    data class Error(val error: Exception) : CheckoutUiState()
    data class OrderPlaced(val orderId: String) : CheckoutUiState()
}

// ==================== ORDERS SCREEN ====================

/**
 * 💫 Orders History Screen
 */
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = hiltViewModel(),
    onNavigateToOrderDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💫 سفارشات من") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "برگشت")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState) {
            is OrdersUiState.Empty ->
                EmptyState(message = "هیچ سفارشی ندارید", modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding))
            is OrdersUiState.Loading ->
                LoadingState(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding))
            is OrdersUiState.Success ->
                OrdersList(state.orders, onNavigateToOrderDetail, Modifier
                    .fillMaxSize()
                    .padding(innerPadding))
            is OrdersUiState.Error ->
                ErrorState(state.error, { viewModel.loadOrders() }, Modifier
                    .fillMaxSize()
                    .padding(innerPadding))
        }
    }
}

@Composable
private fun OrdersList(
    orders: List<Order>,
    onOrderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
        items(orders) { order ->
            OrderCard(order, onClick = { onOrderClick(order.id) })
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("#${order.id.take(8)}", fontWeight = FontWeight.Bold)
                Text(order.status, color = when (order.status) {
                    "completed" -> MaterialTheme.colorScheme.primary
                    "pending" -> MaterialTheme.colorScheme.tertiary
                    "cancelled" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outline
                })
            }
            Text(order.createdAt, style = MaterialTheme.typography.bodySmall)
            Text("${order.total} تومان", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

sealed class OrdersUiState {
    object Empty : OrdersUiState()
    object Loading : OrdersUiState()
    data class Success(val orders: List<Order>) : OrdersUiState()
    data class Error(val error: Exception) : OrdersUiState()
}

@Composable
private fun clickable(onClick: () -> Unit) = Modifier.clickable(onClick = onClick)
