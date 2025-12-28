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
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.common.UiState
import com.noghre.sod.presentation.components.ErrorView
import com.noghre.sod.presentation.components.EmptyView
import com.noghre.sod.presentation.components.LoadingView
import com.noghre.sod.presentation.viewmodel.CheckoutViewModel
import timber.log.Timber

@Composable
fun CheckoutScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val cartState by viewModel.cartState.collectAsStateWithLifecycle()
    val checkoutState by viewModel.checkoutState.collectAsStateWithLifecycle()
    val totalPrice by viewModel.totalPrice.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var shippingAddress by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
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
                    isLoading = false
                }
                is UiEvent.Navigate -> {
                    Timber.d("Navigate: ${event.route}")
                    onNavigate(event.route)
                }
                else -> {}
            }
        }
    }
    
    // Track checkout state
    LaunchedEffect(checkoutState) {
        when (checkoutState) {
            UiState.Loading -> isLoading = true
            is UiState.Success -> {
                isLoading = false
                onNavigate("order_confirmation/${checkoutState.data}")
            }
            is UiState.Error -> isLoading = false
            else -> {}
        }
    }
    
    // Render based on cart state
    when (cartState) {
        UiState.Idle, UiState.Loading -> {
            LoadingView(message = "درحال تهیه چکاوت...")
        }
        is UiState.Success -> {
            CheckoutContent(
                totalPrice = totalPrice,
                shippingAddress = shippingAddress,
                onShippingAddressChange = { shippingAddress = it },
                paymentMethod = paymentMethod,
                onPaymentMethodChange = { paymentMethod = it },
                isLoading = isLoading,
                onPlaceOrder = {
                    Timber.d("Place order: $shippingAddress, $paymentMethod")
                    viewModel.onPlaceOrder(shippingAddress, paymentMethod)
                },
                onBackClick = onBackClick
            )
        }
        is UiState.Error -> {
            ErrorView(
                error = cartState.error,
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
private fun CheckoutContent(
    totalPrice: Double,
    shippingAddress: String,
    onShippingAddressChange: (String) -> Unit,
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    isLoading: Boolean,
    onPlaceOrder: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBackClick) {
                Text("بازگشت")
            }
            Text(
                text = "پگیرابی",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Shipping Address
        Text(
            text = "آدرس السگر",
            style = MaterialTheme.typography.titleMedium
        )
        
        OutlinedTextField(
            value = shippingAddress,
            onValueChange = onShippingAddressChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            label = { Text(آدرس را وارد کنید") },
            maxLines = 3
        )
        
        // Payment Method
        Text(
            text = "روش پرداخت",
            style = MaterialTheme.typography.titleMedium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = paymentMethod == "card",
                onClick = { onPaymentMethodChange("card") }
            )
            Text("کارت بانکی")
            
            RadioButton(
                selected = paymentMethod == "wallet",
                onClick = { onPaymentMethodChange("wallet") }
            )
            Text("کیف دیجیتال")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Total Price
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Place Order Button
        Button(
            onClick = onPlaceOrder,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading && shippingAddress.isNotBlank() && paymentMethod.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(اتمام خرید")
            }
        }
    }
}
