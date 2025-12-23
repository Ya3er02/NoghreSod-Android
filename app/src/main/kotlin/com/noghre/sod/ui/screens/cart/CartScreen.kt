package com.noghre.sod.ui.screens.cart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noghre.sod.presentation.viewmodel.CartViewModel
import com.noghre.sod.ui.theme.Spacing

@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.items.isEmpty() -> {
                EmptyCartView(modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.medium)
                ) {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(uiState.items.size) { index ->
                            CartItemCard(
                                item = uiState.items[index],
                                onQuantityChange = { /* TODO */ },
                                onRemove = { /* TODO */ }
                            )
                            Spacer(modifier = Modifier.height(Spacing.small))
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.medium))

                    CartSummary(
                        subtotal = uiState.subtotal,
                        discountAmount = uiState.discountAmount,
                        shippingCost = uiState.shippingCost,
                        total = uiState.total,
                        appliedCoupon = uiState.appliedCoupon,
                        onApplyCoupon = { viewModel.applyCoupon(it) },
                        onRemoveCoupon = { viewModel.removeCoupon() }
                    )

                    Spacer(modifier = Modifier.height(Spacing.medium))

                    Button(
                        onClick = { /* Navigate to checkout */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("پيام خريد")
                    }
                }
            }
        }

        if (uiState.error != null) {
            Text(uiState.error, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
fun EmptyCartView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("سبد خريد خالی است")
    }
}

@Composable
fun CartItemCard(
    item: com.noghre.sod.domain.model.CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Text(item.product.name)
    }
}

@Composable
fun CartSummary(
    subtotal: Double,
    discountAmount: Double,
    shippingCost: Double,
    total: Double,
    appliedCoupon: String?,
    onApplyCoupon: (String) -> Unit,
    onRemoveCoupon: () -> Unit
) {
    Column {
        Text("یاخته: ریال $subtotal")
        if (discountAmount > 0) {
            Text("تخفیف: -ریال $discountAmount")
        }
        Text("۶رمالگان: ریال $shippingCost")
        Text("کل رقم: ریال $total")
    }
}
