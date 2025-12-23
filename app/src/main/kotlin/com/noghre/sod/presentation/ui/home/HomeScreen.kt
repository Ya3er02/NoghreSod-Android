package com.noghre.sod.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noghre.sod.presentation.components.ErrorMessage
import com.noghre.sod.presentation.components.LoadingScreen
import com.noghre.sod.presentation.components.ProductCard
import com.noghre.sod.presentation.viewmodel.HomeViewModel
import com.noghre.sod.utils.clickableWithRipple

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit,
    onSearchClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar with Search
        TopAppBar(
            title = {
                Text(
                    text = "Noghresod",
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        when {
            uiState.isLoading && uiState.featuredProducts.isEmpty() -> {
                LoadingScreen(message = "در حال بارگذاری محصولات...")
            }
            uiState.error != null && uiState.featuredProducts.isEmpty() -> {
                ErrorMessage(
                    error = uiState.error ?: "خطای نامشخص",
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // Featured Section
                    if (uiState.featuredProducts.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Column {
                                Text(
                                    text = "محصولات ویژه",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                        items(
                            items = uiState.featuredProducts.take(4),
                            key = { it.id }
                        ) { product ->
                            ProductCard(
                                product = product,
                                onProductClick = { onProductClick(product.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // All Products Section
                    if (uiState.allProducts.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Column {
                                Text(
                                    text = "تمام محصولات",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                        items(
                            items = uiState.allProducts,
                            key = { it.id }
                        ) { product ->
                            ProductCard(
                                product = product,
                                onProductClick = { onProductClick(product.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Load More Button
                    if (uiState.allProducts.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Button(
                                onClick = { viewModel.loadMore() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text(text = "بیشتر بارگذاری کن")
                            }
                        }
                    }
                }
            }
        }
    }
}
