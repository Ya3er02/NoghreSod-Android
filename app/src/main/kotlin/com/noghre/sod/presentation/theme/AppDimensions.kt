package com.noghre.sod.presentation.theme

import androidx.compose.ui.unit.dp

/**
 * Centralized dimension constants for the application.
 * Eliminates magic numbers and provides consistent spacing/sizing.
 * 
 * Usage: Instead of Modifier.padding(16.dp), use Modifier.padding(AppDimensions.paddingMedium)
 * 
 * @since 1.0.0
 */
object AppDimensions {
    // Padding values
    val paddingExtraSmall = 2.dp
    val paddingSmall = 4.dp
    val paddingMediumSmall = 6.dp
    val paddingMedium = 8.dp
    val paddingMediumLarge = 12.dp
    val paddingLarge = 16.dp
    val paddingExtraLarge = 20.dp
    val paddingXXLarge = 24.dp
    val paddingHuge = 32.dp
    
    // Border/Corner radius values
    val cornerRadiusSmall = 4.dp
    val cornerRadiusMedium = 8.dp
    val cornerRadiusLarge = 12.dp
    val cornerRadiusXLarge = 16.dp
    val cornerRadiusFull = 24.dp
    
    // Size values
    val sizeExtraSmall = 16.dp
    val sizeSmall = 24.dp
    val sizeMedium = 32.dp
    val sizeLarge = 48.dp
    val sizeXLarge = 64.dp
    val sizeHuge = 128.dp
    
    // Image sizes
    val imageThumbnail = 80.dp
    val imageSmall = 100.dp
    val imageMedium = 150.dp
    val imageLarge = 200.dp
    
    // Card/elevation sizes
    val elevationSmall = 2.dp
    val elevationMedium = 4.dp
    val elevationLarge = 8.dp
    val elevationXLarge = 12.dp
    
    // Divider heights
    val dividerThin = 1.dp
    val dividerNormal = 2.dp
    val dividerThick = 4.dp
    
    // Touch target minimum (48dp per material design)
    val minimumTouchTarget = 48.dp
}
