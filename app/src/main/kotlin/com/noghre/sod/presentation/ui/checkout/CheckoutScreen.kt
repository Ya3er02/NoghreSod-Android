package com.noghre.sod.presentation.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noghre.sod.presentation.components.ErrorMessage
import com.noghre.sod.presentation.components.LoadingBar
import com.noghre.sod.presentation.components.PrimaryButton
import com.noghre.sod.presentation.viewmodel.CartViewModel
import com.noghre.sod.utils.formatAsCurrency

@Composable
fun CheckoutScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedAddressIndex by remember { mutableStateOf(0) }
    var paymentMethod by remember { mutableStateOf("card") }
    var useNewAddress by remember { mutableStateOf(false) }
    var newAddressText by remember { mutableStateOf("") }
    var newPhoneText by remember { mutableStateOf("") }
    var newCityText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isCheckoutSuccess) {
        if (uiState.isCheckoutSuccess) {
            onPaymentSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("ترتیب پرداخت") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        if (uiState.error != null) {
            ErrorMessage(
                error = uiState.error ?: "خطای نامشخص",
                modifier = Modifier.padding(16.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Step 1: Review Items
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "مرور سرانجام",
                        style = MaterialTheme.typography.titleMedium
                    )
                    uiState.cart?.items?.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "محصول x${item.quantity}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = item.totalPrice.formatAsCurrency(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Step 2: Delivery Address
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "آدرس تحویل",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (!useNewAddress && uiState.userAddresses.isNotEmpty()) {
                        uiState.userAddresses.forEachIndexed { index, address ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedAddressIndex == index,
                                    onClick = { selectedAddressIndex = index }
                                )
                                Text(
                                    text = "${address.city}, ${address.address}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        TextButton(
                            onClick = { useNewAddress = true },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("آدرس جدید")
                        }
                    } else if (useNewAddress) {
                        OutlinedTextField(
                            value = newCityText,
                            onValueChange = { newCityText = it },
                            label = { Text("شهر") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newAddressText,
                            onValueChange = { newAddressText = it },
                            label = { Text("آدرس") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = newPhoneText,
                            onValueChange = { newPhoneText = it },
                            label = { Text("شماره تلفن") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                        TextButton(
                            onClick = { useNewAddress = false },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("لغو")
                        }
                    }
                }
            }

            // Step 3: Payment Method
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "روش پرداخت",
                        style = MaterialTheme.typography.titleMedium
                    )
                    listOf(
                        "card" to "کارت اعتباری",
                        "transfer" to "انتقال بانکی",
                        "wallet" to "کیف دیجیتال"
                    ).forEach { (value, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = paymentMethod == value,
                                onClick = { paymentMethod = value }
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            // Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("مبلغ عبارتمند:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = (uiState.cart?.totalPrice ?: 0.0).formatAsCurrency(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("هزینه ارسال:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "50000 ریال",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("جمع کل", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = ((uiState.cart?.totalPrice ?: 0.0) + 50000).formatAsCurrency(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Proceed to Payment Button
        PrimaryButton(
            text = "مراجعه برای پرداخت",
            onClick = { viewModel.checkout() },
            modifier = Modifier.padding(16.dp),
            enabled = !uiState.isLoading
        )
    }
}
