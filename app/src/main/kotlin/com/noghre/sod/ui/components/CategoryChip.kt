package com.noghre.sod.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.noghre.sod.domain.model.Category
import com.noghre.sod.ui.theme.Spacing

/**
 * Category filter chip for product filtering
 *
 * @param category Category to display
 * @param isSelected Whether chip is selected
 * @param onClick Callback when chip is clicked
 * @param icon Optional icon for category
 * @param modifier Modifier for the chip
 */
@Composable
fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(category.name) },
        leadingIcon = if (isSelected && icon != null) {
            { Icon(imageVector = icon, contentDescription = category.name) }
        } else null,
        modifier = modifier.padding(horizontal = 4.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

/**
 * Horizontal scrollable row of category chips
 *
 * @param categories List of categories
 * @param selectedCategoryId Selected category ID
 * @param onCategorySelected Callback when category is selected
 * @param modifier Modifier for the row
 */
@Composable
fun CategoryChipsRow(
    categories: List<Category>,
    selectedCategoryId: String? = null,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Spacing.medium),
    ) {
        categories.forEach { category ->
            CategoryChip(
                category = category,
                isSelected = category.id == selectedCategoryId,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}
