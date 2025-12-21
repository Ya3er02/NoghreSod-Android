package com.noghre.sod.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noghre.sod.viewmodel.OrderViewModel

@Composable
fun OrderScreen(
    navController: NavController,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val state = uiState.value

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "My Orders",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (state.orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "No orders yet",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.orders) { order ->
                    Text("Order ${order.id} - ${order.status}")
                }
            }
        }
    }
}
