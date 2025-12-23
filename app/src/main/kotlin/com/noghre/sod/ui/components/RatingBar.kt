package com.noghre.sod.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.noghre.sod.ui.theme.Gold
import com.noghre.sod.ui.theme.Spacing

/**
 * Display star rating with optional review count
 *
 * @param rating Rating value (0-5)
 * @param reviewCount Optional number of reviews
 * @param modifier Modifier for the composable
 */
@Composable
fun RatingBar(
    rating: Float,
    reviewCount: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = Spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Star",
                modifier = Modifier
                    .size(16.dp)
                    .padding(horizontal = 2.dp),
                tint = Gold
            )
        }

        if (reviewCount != null) {
            Text(
                text = "($reviewCount)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = Spacing.small)
            )
        }
    }
}
