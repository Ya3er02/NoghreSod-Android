package com.noghre.sod.presentation.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.noghre.sod.presentation.components.ErrorMessage
import com.noghre.sod.presentation.components.LoadingScreen
import com.noghre.sod.presentation.components.PrimaryButton
import com.noghre.sod.presentation.viewmodel.ProductDetailViewModel
import com.noghre.sod.utils.formatAsCurrency

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAddToCart: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            LoadingScreen(message = "در حال بارگذاری جزئیات محصول...")
        }
        uiState.error != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ErrorMessage(
                    error = uiState.error ?: "خطای نامشخص",
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = onBackClick, modifier = Modifier.padding(top = 16.dp)) {
                    Text("بازگشت")
                }
            }
        }
        uiState.product != null -> {
            val product = uiState.product!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Top App Bar
                TopAppBar(
                    title = { Text("جزئیات محصول") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (uiState.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Product Image
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        AsyncImage(
                            model = product.productSummary.thumbnailImage,
                            contentDescription = product.productSummary.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Product Info
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = product.productSummary.name,
                            style = MaterialTheme.typography.headlineMedium
                        )

                        // Price
                        Text(
                            text = product.productSummary.price.formatAsCurrency(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Weight & Purity
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "وزن",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = "${product.productSummary.weight}g",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "خلوص",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = product.productSummary.purity,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "امتیاز",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = "${product.productSummary.rating}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    // Description
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "توضیحات",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = product.description ?: "توضیحی موجود نیست",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Justify
                        )
                    }

                    // Quantity Selector
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "تعداد",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.updateQuantity(uiState.quantity - 1) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("-")
                            }
                            Text(
                                text = "${uiState.quantity}",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Button(
                                onClick = { viewModel.updateQuantity(uiState.quantity + 1) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+")
                            }
                        }
                    }

                    // Add to Cart Button
                    PrimaryButton(
                        text = "افزودن به سبد خرید",
                        onClick = onAddToCart
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
