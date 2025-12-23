package com.noghre.sod.ui.animation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.addToCartAnimation(isAdded: Boolean): Modifier {
    val scale by animateFloatAsState(if (isAdded) 1.1f else 1f)
    val offsetX by animateDpAsState(if (isAdded) 20.dp else 0.dp)

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            translationX = offsetX.toPx()
        }
        .background(
            color = if (isAdded)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
}
