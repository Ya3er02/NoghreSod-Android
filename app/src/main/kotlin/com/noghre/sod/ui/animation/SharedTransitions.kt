package com.noghre.sod.ui.animation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape

@Composable
fun SharedTransitionScope.SharedProductImage(
    imageUri: String,
    productId: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .sharedElement(
                rememberSharedContentState(key = "image/$productId"),
                animatedVisibilityScope = this
            )
            .animateContentSize()
    )
}
