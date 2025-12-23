package com.noghre.sod.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noghre.sod.ui.theme.Spacing

/**
 * Quantity selector with +/- buttons
 *
 * @param quantity Current quantity
 * @param onQuantityChange Callback when quantity changes
 * @param maxQuantity Maximum allowed quantity
 * @param minQuantity Minimum allowed quantity
 * @param modifier Modifier for the composable
 */
@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    maxQuantity: Int = 99,
    minQuantity: Int = 1,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(Spacing.small)
            ),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (quantity > minQuantity) onQuantityChange(quantity - 1) },
            modifier = Modifier.size(36.dp),
            enabled = quantity > minQuantity
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Decrease",
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        IconButton(
            onClick = { if (quantity < maxQuantity) onQuantityChange(quantity + 1) },
            modifier = Modifier.size(36.dp),
            enabled = quantity < maxQuantity
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Increase",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
