package com.noghre.sod.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noghre.sod.presentation.theme.PersianFormatter
import com.noghre.sod.presentation.theme.PersianFontFamily

/**
 * Professional jewelry e-commerce UI components.
 * 
 * Includes:
 * - Jewelry product cards with ratings
 * - Price display with discounts
 * - Wishlist toggles
 * - Quality badges (purity, weight)
 * - Cart add buttons
 * - Promotional banners
 * 
 * @author Yaser
 * @version 1.0.0
 */

// ==================== JEWELRY PRODUCT CARD ====================

/**
 * Premium jewelry product card with all details.
 */
@Composable
fun JewelryProductCard(
    name: String,
    price: Long,
    originalPrice: Long? = null,
    imageUrl: String?,
    rating: Float = 0f,
    reviewCount: Int = 0,
    purity: String = "925",
    weight: String = "",
    inStock: Boolean = true,
    isNew: Boolean = false,
    isFavorite: Boolean = false,
    onProductClick: () -> Unit = {},
    onAddToCart: () -> Unit = {},
    onToggleFavorite: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true, onClick = onProductClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Image with badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Product image
                AsyncImageWithCache(
                    imageUrl = imageUrl,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Badges (top-left)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isNew) {
                        Badge(
                            modifier = Modifier.background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            ),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = "جدید",
                                modifier = Modifier.padding(4.dp, 2.dp),
                                fontSize = 10.sp,
                                fontFamily = PersianFontFamily
                            )
                        }
                    }
                    
                    if (!inStock) {
                        Badge(
                            modifier = Modifier.background(
                                color = MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(4.dp)
                            ),
                            contentColor = MaterialTheme.colorScheme.onError
                        ) {
                            Text(
                                text = "تمام",
                                modifier = Modifier.padding(4.dp, 2.dp),
                                fontSize = 10.sp,
                                fontFamily = PersianFontFamily
                            )
                        }
                    }
                }
                
                // Discount badge (top-right)
                val discountPercent = if (originalPrice != null && originalPrice > price) {
                    ((originalPrice - price) * 100 / originalPrice).toInt()
                } else {
                    0
                }
                
                if (discountPercent > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "-$discountPercent%",
                            modifier = Modifier.padding(6.dp, 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = PersianFontFamily
                        )
                    }
                }
                
                // Favorite button (bottom-right)
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "علاقه مند",
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Product name
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = PersianFontFamily
                )
                
                // Quality info (purity & weight)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (purity.isNotBlank()) {
                        Surface(
                            modifier = Modifier.wrapContentSize(),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "فذ۵ $purity",
                                modifier = Modifier.padding(6.dp, 4.dp),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontFamily = PersianFontFamily
                            )
                        }
                    }
                    
                    if (weight.isNotBlank()) {
                        Surface(
                            modifier = Modifier.wrapContentSize(),
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "$weight گ",
                                modifier = Modifier.padding(6.dp, 4.dp),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontFamily = PersianFontFamily
                            )
                        }
                    }
                }
                
                // Rating
                if (rating > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RatingDisplay(rating = rating, maxStars = 5)
                        Text(
                            text = "($reviewCount)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = PersianFontFamily
                        )
                    }
                }
                
                // Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = PersianFormatter.formatCurrency(price),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = PersianFontFamily
                        )
                        if (originalPrice != null && originalPrice > price) {
                            Text(
                                text = PersianFormatter.formatCurrency(originalPrice),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    textDecoration = androidx.compose.ui.text.decoration.TextDecoration.LineThrough
                                ),
                                fontFamily = PersianFontFamily
                            )
                        }
                    }
                    
                    // Add to cart button
                    Button(
                        onClick = onAddToCart,
                        enabled = inStock,
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "افزودن به سبد",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==================== PROMOTIONAL BANNER ====================

/**
 * Full-width promotional banner for jewelry sales.
 */
@Composable
fun PromotionalBanner(
    title: String,
    subtitle: String? = null,
    discount: Int? = null,
    backgroundImageUrl: String? = null,
    onBannerClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(enabled = true, onClick = onBannerClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            if (backgroundImageUrl != null) {
                AsyncImageWithCache(
                    imageUrl = backgroundImageUrl,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
            )
            
            // Content
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = PersianFontFamily
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            fontFamily = PersianFontFamily
                        )
                    }
                }
                
                if (discount != null) {
                    Surface(
                        modifier = Modifier
                            .wrapContentSize(),
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "-$discount%",
                            modifier = Modifier.padding(12.dp, 8.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = PersianFontFamily
                        )
                    }
                }
            }
        }
    }
}
