package com.noghre.sod.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * RTL-aware Composable components
 * Provides proper Farsi/RTL support for the app
 */

/**
 * RTL-aware padding modifier
 * Automatically reverses start/end based on layout direction
 */
fun Modifier.rtlPadding(
    horizontal: Dp = Dp.Unspecified,
    vertical: Dp = Dp.Unspecified,
    start: Dp = Dp.Unspecified,
    end: Dp = Dp.Unspecified,
    top: Dp = Dp.Unspecified,
    bottom: Dp = Dp.Unspecified
): Modifier {
    return if (horizontal != Dp.Unspecified || start != Dp.Unspecified || end != Dp.Unspecified) {
        this.padding(
            start = if (start != Dp.Unspecified) start else horizontal,
            end = if (end != Dp.Unspecified) end else horizontal,
            top = top,
            bottom = bottom,
            verticalPadding = vertical
        )
    } else {
        this.padding(horizontal, vertical, top, bottom)
    }
}

/**
 * RTL-aware text alignment
 * Returns TextAlign.Start for LTR, TextAlign.Start for RTL (auto-handled)
 */
fun getRTLTextAlign(layoutDirection: LayoutDirection): TextAlign {
    return when (layoutDirection) {
        LayoutDirection.Ltr -> TextAlign.Start
        LayoutDirection.Rtl -> TextAlign.Start  // Compose handles RTL automatically
    }
}

/**
 * RTL-aware navigation icon
 * Shows back arrow that mirrors for RTL
 */
@Composable
fun RTLNavigationIcon(
    modifier: Modifier = Modifier,
    onNavigate: () -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current
    val icon = when (layoutDirection) {
        LayoutDirection.Ltr -> Icons.Filled.ArrowBack
        LayoutDirection.Rtl -> Icons.Filled.ArrowForward
    }

    IconButton(
        onClick = onNavigate,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Navigate back"
        )
    }
}

/**
 * RTL-aware horizontal arrangement
 * Automatically reverses direction for RTL
 */
fun getRTLArrangement(layoutDirection: LayoutDirection): Arrangement.Horizontal {
    return when (layoutDirection) {
        LayoutDirection.Ltr -> Arrangement.Start
        LayoutDirection.Rtl -> Arrangement.End  // Will be reversed by Compose
    }
}

/**
 * RTL-aware content alignment
 * Aligns content based on layout direction
 */
fun getRTLAlignment(layoutDirection: LayoutDirection): Alignment {
    return when (layoutDirection) {
        LayoutDirection.Ltr -> Alignment.CenterStart
        LayoutDirection.Rtl -> Alignment.CenterEnd
    }
}

/**
 * RTL box with proper alignment
 */
@Composable
fun RTLBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current
    val alignment = when (layoutDirection) {
        LayoutDirection.Ltr -> contentAlignment
        LayoutDirection.Rtl -> when (contentAlignment) {
            Alignment.TopStart -> Alignment.TopEnd
            Alignment.CenterStart -> Alignment.CenterEnd
            Alignment.BottomStart -> Alignment.BottomEnd
            Alignment.TopEnd -> Alignment.TopStart
            Alignment.CenterEnd -> Alignment.CenterStart
            Alignment.BottomEnd -> Alignment.BottomStart
            else -> contentAlignment
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = alignment,
        content = content
    )
}

/**
 * Convert Farsi/Persian numbers to English for display
 */
fun String.convertPersianToEnglish(): String {
    var result = this
    result = result.replace("۰", "0")
    result = result.replace("۱", "1")
    result = result.replace("۲", "2")
    result = result.replace("۳", "3")
    result = result.replace("۴", "4")
    result = result.replace("۵", "5")
    result = result.replace("۶", "6")
    result = result.replace("۷", "7")
    result = result.replace("۸", "8")
    result = result.replace("۹", "9")
    return result
}

/**
 * Convert English numbers to Farsi/Persian for display
 */
fun String.convertEnglishToPersian(): String {
    var result = this
    result = result.replace("0", "۰")
    result = result.replace("1", "۱")
    result = result.replace("2", "۲")
    result = result.replace("3", "۳")
    result = result.replace("4", "۴")
    result = result.replace("5", "۵")
    result = result.replace("6", "۶")
    result = result.replace("7", "۷")
    result = result.replace("8", "۸")
    result = result.replace("9", "۹")
    return result
}
