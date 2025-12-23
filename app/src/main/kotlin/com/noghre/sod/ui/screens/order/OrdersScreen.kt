package com.noghre.sod.ui.screens.order

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noghre.sod.presentation.viewmodel.OrderViewModel
import com.noghre.sod.ui.theme.Spacing

@Composable
fun OrdersScreen(
    navController: NavController,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.orders.isEmpty() -> {
                Text("هیچ سفارشی نداريد", modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.medium)
                ) {
                    items(uiState.orders.size) { index ->
                        OrderCard(
                            order = uiState.orders[index],
                            onClick = { /* Navigate to detail */ }
                        )
                    }
                }
            }
        }

        if (uiState.error != null) {
            Text(
                uiState.error,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(Spacing.medium)
            )
        }
    }
}

@Composable
fun OrderCard(
    order: com.noghre.sod.domain.model.Order,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.medium)
    ) {
        Text("سفارش #${order.id}")
        Text("وضعيت: ${order.status}")
        Text("مبلغ: ریال ${order.finalPrice}")
    }
}
