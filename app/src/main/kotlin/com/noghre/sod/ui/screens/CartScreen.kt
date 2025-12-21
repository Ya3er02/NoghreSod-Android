package com.noghre.sod.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noghre.sod.viewmodel.CartViewModel

@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val state = uiState.value

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Your Cart",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (state.items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Cart is empty",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.items) { item ->
                    Text("Product ID: ${item.productId} - Qty: ${item.quantity}")
                }
            }

            Button(
                onClick = { /* Checkout */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Proceed to Checkout")
            }
        }
    }
}
