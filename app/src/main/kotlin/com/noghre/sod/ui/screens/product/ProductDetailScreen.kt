package com.noghre.sod.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.noghre.sod.domain.entities.Product
import com.noghre.sod.ui.components.ErrorState
import com.noghre.sod.ui.components.LoadingState

/**
 * 📐 Product Detail Screen
 *
 * Features:
 * - Product image gallery
 * - Full product information
 * - Specifications
 * - Customer reviews
 * - Add to cart/wishlist
 * - Quantity selector
 *
 * @param productId Product identifier
 * @param viewModel Injected ProductDetailViewModel
 * @param onNavigateBack Callback for back navigation
 * @param onNavigateToCart Callback for cart navigation
 */
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
    }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val quantity by viewModel.quantity.collectAsStateWithLifecycle()
    val isInWishlist by viewModel.isInWishlist.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تفاصیل محصول") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "برگشت")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleWishlist(productId) }) {
                        Icon(
                            imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "فاوریت",
                            tint = if (isInWishlist) MaterialTheme.colorScheme.error else LocalContentColor.current
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState) {
            is ProductDetailUiState.Loading -> {
                LoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is ProductDetailUiState.Success -> {
                ProductDetailContent(
                    product = state.product,
                    quantity = quantity,
                    onQuantityChange = viewModel::setQuantity,
                    onAddToCart = {
                        viewModel.addToCart(productId, quantity)
                        onNavigateToCart()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is ProductDetailUiState.Error -> {
                ErrorState(
                    error = state.error,
                    onRetry = { viewModel.loadProductDetail(productId) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

/**
 * Product detail content
 */
@Composable
private fun ProductDetailContent(
    product: Product,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentImageIndex by remember { mutableStateOf(0) }
    
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Image Gallery
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Main Image
                if (product.images.isNotEmpty()) {
                    AsyncImage(
                        model = product.images[currentImageIndex].url,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Image Thumbnails
                    if (product.images.size > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            product.images.forEachIndexed { index, image ->
                                AsyncImage(
                                    model = image.thumbnail,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (index == currentImageIndex) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surface
                                        )
                                        .clickable { currentImageIndex = index },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Product Name & Rating
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (index < product.rating.average.toInt()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${product.rating.average} (${product.rating.count} رای)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        // Price Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${product.price.amount} تومان",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (product.discount.percentage > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text("-${product.discount.percentage}%")
                            }
                        }
                    }
                    if (product.discount.percentage > 0) {
                        Text(
                            text = "قيمت بعد از تخفيف: ${product.discount.final_price} تومان",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        // Description
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "مورد توضيح:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = product.long_description ?: product.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Specifications
        if (product.specifications.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "مشخصات:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    product.specifications.forEach { (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = key, style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        
        // Stock Status
        item {
            when (product.stock.status) {
                "in_stock" -> {
                    Text(
                        text = "✅ موجود در انبار",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                "low_stock" -> {
                    Text(
                        text = "⚠️ تعداد محدود",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    Text(
                        text = "❌ در انبار نموجود",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        
        // Action Buttons
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quantity Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("تعداد:")
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = null)
                    }
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    IconButton(
                        onClick = { onQuantityChange(quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                    }
                }
                
                // Add to Cart Button
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = product.stock.available > 0
                ) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("اضافه به سبد")
                }
            }
        }
    }
}

sealed class ProductDetailUiState {
    object Loading : ProductDetailUiState()
    data class Success(val product: Product) : ProductDetailUiState()
    data class Error(val error: Exception) : ProductDetailUiState()
}

@Composable
private fun rememberScrollState() = remember { androidx.compose.foundation.ScrollState(0) }

@Composable
private fun clickable(onClick: () -> Unit) = Modifier.clickable(onClick = onClick)
