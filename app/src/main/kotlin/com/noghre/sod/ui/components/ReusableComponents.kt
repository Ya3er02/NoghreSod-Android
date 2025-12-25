package com.noghre.sod.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.shimmer.Shimmer

// ==================== LOADING STATE ====================

/**
 * ⚡ Shimmer loading state with animated placeholders
 */
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(6) {
            ShimmerCard(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun ShimmerCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Shimmer.shimmerGradient(
                    targetValue = 1300f,
                    showShimmer = true
                )
            )
    )
}

// ==================== ERROR STATE ====================

/**
 * ❌ Error display with retry option
 */
@Composable
fun ErrorState(
    error: Exception,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "خطای بهره عنگی",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error.message ?: "Unknown error occurred",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("تلاش دوباره")
        }
    }
}

// ==================== EMPTY STATE ====================

/**
 * 🗑️ Empty state display
 */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    icon: String = "🗑️"
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, style = MaterialTheme.typography.titleMedium)
    }
}

// ==================== SEARCH BAR ====================

/**
 * 🔍 Search bar with filtering
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "جستجو محصولات"
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = null)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        )
    )
}

// ==================== FILTER BOTTOM SHEET ====================

/**
 * 🔎 Filter bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    onApplyFilter: (ClosedFloatingPointRange<Float>, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var minPrice by remember { mutableStateOf(0f) }
    var maxPrice by remember { mutableStateOf(10000f) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("فیلتر محصولات", style = MaterialTheme.typography.titleLarge)
            
            // Price Range
            Text("رنج قيمت", style = MaterialTheme.typography.titleSmall)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RangeSlider(
                    value = minPrice..maxPrice,
                    onValueChange = { range ->
                        minPrice = range.start
                        maxPrice = range.endInclusive
                    },
                    valueRange = 0f..100000f
                )
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("$minPrice تومان")
                    Text("$maxPrice تومان")
                }
            }
            
            // Apply Button
            Button(
                onClick = {
                    onApplyFilter(minPrice..maxPrice, selectedCategory)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("اعمال فیلتر")
            }
        }
    }
}

// ==================== PRODUCT CARD ====================

/**
 * 💳 Product card for grid/horizontal display
 */
@Composable
fun ProductCard(
    product: com.noghre.sod.domain.entities.Product,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onProductClick(product.id) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                coil.compose.AsyncImage(
                    model = product.images.firstOrNull()?.url,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                // Discount Badge
                if (product.discount.percentage > 0) {
                    Badge(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text("-${product.discount.percentage}%")
                    }
                }
            }
            
            // Content
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                
                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${product.rating.average}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                
                // Price
                Text(
                    text = "${product.price.amount} تومان",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ==================== DIALOGS ====================

/**
 * Confirmation dialog
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isShown: Boolean
) {
    if (isShown) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("تایيد")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("لغو")
                }
            }
        )
    }
}

// ==================== INPUT FIELDS ====================

/**
 * Phone number input field with validation
 */
@Composable
fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { if (it.length <= 11) onValueChange(it) },
            label = { Text("شماره موبايل") },
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
        }
    }
}

/**
 * Email input field with validation
 */
@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("ايميل") },
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = modifier.fillMaxWidth()
    )
}

// ==================== BADGES & CHIPS ====================

/**
 * Custom badge component
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        "completed" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        "pending" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.tertiary
        "cancelled" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Badge(
        modifier = modifier,
        containerColor = backgroundColor
    ) {
        Text(status, color = textColor, style = MaterialTheme.typography.labelSmall)
    }
}

const val SHIMMER_INTENSITY = 0.6f

@Composable
private fun clickable(onClick: () -> Unit) = Modifier.clickable(onClick = onClick)
