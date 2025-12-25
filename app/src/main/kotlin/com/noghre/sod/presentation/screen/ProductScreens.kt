package com.noghre.sod.presentation.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.noghre.sod.domain.model.Product
import com.noghre.sod.presentation.viewmodel.ProductViewModel
import com.noghre.sod.presentation.viewmodel.UiState

/**
 * Home screen showing featured products and categories.
 */
@Composable
fun HomeScreen(
    viewModel: ProductViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit
) {
    val featuredProducts by viewModel.productsState.collectAsState()
    val categories by viewModel.categoriesState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getFeaturedProducts()
        viewModel.getCategories()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        // Header
        HomeHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Categories
        when (categories) {
            is UiState.Success -> {
                CategoriesSection(
                    categories = (categories as UiState.Success<List<String>>).data,
                    onCategoryClick = onCategoryClick
                )
            }
            is UiState.Loading -> {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Featured Products
        Text(
            text = "Featured Products",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        when (featuredProducts) {
            is UiState.Success -> {
                ProductGrid(
                    products = (featuredProducts as UiState.Success<List<Product>>).data,
                    onProductClick = onProductClick
                )
            }
            is UiState.Loading -> {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                ErrorMessage((featuredProducts as UiState.Error).message)
            }
            else -> {}
        }
    }
}

/**
 * Product list screen.
 */
@Composable
fun ProductListScreen(
    viewModel: ProductViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val products by viewModel.productsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (products) {
            is UiState.Success -> {
                ProductGrid(
                    products = (products as UiState.Success<List<Product>>).data,
                    onProductClick = onProductClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                ErrorMessage((products as UiState.Error).message)
            }
            else -> {}
        }
    }
}

/**
 * Product detail screen.
 */
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ProductViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAddToCart: (Product) -> Unit
) {
    val product by viewModel.productDetailState.collectAsState()
    var quantity by remember { mutableStateOf(1) }

    LaunchedEffect(Unit) {
        viewModel.getProductDetail(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (product) {
            is UiState.Success -> {
                val prod = (product as UiState.Success<Product>).data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    // Product Image
                    AsyncImage(
                        model = prod.imageUrl,
                        contentDescription = prod.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Product Name
                    Text(
                        text = prod.name,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) {
                            Icon(
                                imageVector = if (it < prod.rating.toInt()) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "(${prod.reviewCount} reviews)")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "\$${prod.price}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (prod.originalPrice != null && prod.originalPrice > prod.price) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "\$${prod.originalPrice}",
                                style = MaterialTheme.typography.bodySmall,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = prod.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Material and Weight
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Material", style = MaterialTheme.typography.labelSmall)
                            Text(prod.material, style = MaterialTheme.typography.bodyMedium)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Weight", style = MaterialTheme.typography.labelSmall)
                            Text("${prod.weight}g", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quantity Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Quantity: ", style = MaterialTheme.typography.bodyMedium)
                        IconButton(onClick = { if (quantity > 1) quantity-- }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(quantity.toString())
                        IconButton(onClick = { quantity++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Add to Cart Button
                    Button(
                        onClick = { onAddToCart(prod.copy()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Add to Cart")
                    }
                }
            }
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                ErrorMessage((product as UiState.Error).message)
            }
            else -> {}
        }
    }
}

// ============== HELPER COMPOSABLES ==============

@Composable
private fun HomeHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Welcome to Noghresod",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Discover beautiful silver jewelry",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CategoriesSection(
    categories: List<String>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryCard(category) { onCategoryClick(category) }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(category, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ProductGrid(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(products) { product ->
            ProductCard(product) { onProductClick(product.id) }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Product Image
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            // Product Info
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\$${product.price}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
