package com.noghre.sod.ui.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection

/**
 * RTL Configuration for the entire app
 * Provides proper RTL/Farsi support
 */

@Composable
fun RTLTheme(
    isDarkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection  // System will handle RTL
    ) {
        MaterialTheme(
            colorScheme = if (isDarkMode) {
                DarkColorScheme
            } else {
                LightColorScheme
            },
            content = content
        )
    }
}

/**
 * RTL-aware text style
 * Automatically sets text alignment for RTL
 */
@Composable
fun rtlTextStyle(
    baseStyle: TextStyle = LocalTextStyle.current,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current
): TextStyle {
    return baseStyle.copy(
        textAlign = when (layoutDirection) {
            LayoutDirection.Ltr -> TextAlign.Start
            LayoutDirection.Rtl -> TextAlign.Start  // Compose handles RTL auto
        }
    )
}

/**
 * Check if current layout is RTL
 */
@Composable
fun isRTLLayout(): Boolean {
    return LocalLayoutDirection.current == LayoutDirection.Rtl
}
