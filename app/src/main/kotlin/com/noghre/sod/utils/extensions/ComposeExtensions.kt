package com.noghre.sod.utils.extensions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.widget.Toast

/**
 * Clickable modifier without ripple effect.
 * Useful for custom interaction feedback.
 */
fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { onClick() }
}

/**
 * Show toast message from composable.
 * Note: Consider using SnackBar for Material Design compliance.
 */
@Composable
fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    val context = LocalContext.current
    Toast.makeText(context, message, duration).show()
}

/**
 * Get current context in composable.
 */
@Composable
fun rememberContext(): Context = LocalContext.current
