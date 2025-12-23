package com.noghre.sod.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.noghre.sod.ui.theme.Gold
import com.noghre.sod.ui.theme.Spacing

/**
 * Display price with optional discount
 *
 * @param price Original price
 * @param discountPrice Discounted price (if any)
 * @param modifier Modifier for the composable
 * @param showCurrency Whether to show currency symbol
 */
@Composable
fun PriceDisplay(
    price: Double,
    discountPrice: Double? = null,
    modifier: Modifier = Modifier,
    showCurrency: Boolean = true
) {
    val currency = if (showCurrency) " تومان" else ""

    Row(
        modifier = modifier.padding(vertical = Spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (discountPrice != null) {
            // Show both prices
            Column {
                // Original price (strikethrough)
                Text(
                    text = "${price.formatPrice()}$currency",
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = Spacing.small)
                )
                // Discounted price
                Text(
                    text = "${discountPrice.formatPrice()}$currency",
                    style = MaterialTheme.typography.titleMedium,
                    color = Gold,
                    modifier = Modifier.padding(end = Spacing.small)
                )
            }
        } else {
            // Show only price
            Text(
                text = "${price.formatPrice()}$currency",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Format price with thousand separators
 */
fun Double.formatPrice(): String {
    return "%.0f".format(this).replace(",", ",")
}
