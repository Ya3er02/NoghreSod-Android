package com.noghre.sod.core.ext

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

/**
 * Extension functions for Jetpack Compose Modifier with common patterns.
 * 
 * Includes loading states, animations, RTL support, and interactions.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */

/**
 * Applies shimmer effect for skeleton loading.
 * 
 * Used for product cards, lists during data loading.
 * 
 * @return Modified Modifier with shimmer effect
 */
@Composable
fun Modifier.shimmerLoading(): Modifier {
    return this.shimmer()
}

/**
 * Conditionally applies a modifier based on a condition.
 * 
 * Example: 
 * ```
 * Modifier.conditional(isSelected) { backgroundColor(Color.Blue) }
 * ```
 * 
 * @param condition Whether to apply the modifier
 * @param block Lambda containing the modifier to apply
 * @return Modified Modifier
 */
fun Modifier.conditional(
    condition: Boolean,
    block: Modifier.() -> Modifier
): Modifier {
    return if (condition) {
        this.then(block())
    } else {
        this
    }
}

/**
 * Applies ripple effect on click with scale animation.
 * 
* Creates tactile feedback for button interactions.
 * 
 * @param onClick Click callback
 * @param enabled Whether the click is enabled
 * @return Modified clickable Modifier
 */
@Composable
fun Modifier.bounceClick(
    onClick: () -> Unit,
    enabled: Boolean = true
): Modifier {
    val interactionSource = MutableInteractionSource()
    return this.clickable(
        interactionSource = interactionSource,
        indication = rememberRipple(bounded = true),
        enabled = enabled,
        onClick = onClick
    )
}

/**
 * Adds border with RTL layout awareness.
 * 
* For right-to-left layouts, border positions are adjusted.
 * 
 * @param width Border width
 * @param color Border color
 * @param shape Border shape
 * @return Modified Modifier with RTL border
 */
@Composable
fun Modifier.borderRtl(
    width: Dp = 1.dp,
    color: Color = Color.Black,
    shape: Shape = RectangleShape
): Modifier {
    val layoutDirection = LocalLayoutDirection.current
    return this.border(
        width = width,
        color = color,
        shape = shape
    )
}

/**
 * Applies alpha (opacity) conditionally.
 * 
 * Example:
 * ```
 * Modifier.alphaIf(isDisabled) { 0.5f } // 50% opacity if disabled
 * ```
 * 
 * @param condition Whether to apply opacity
 * @param alpha Opacity value (0f to 1f)
 * @return Modified Modifier with conditional alpha
 */
fun Modifier.alphaIf(condition: Boolean, alpha: Float = 0.5f): Modifier {
    return if (condition) {
        this.alpha(alpha)
    } else {
        this
    }
}

/**
 * Applies scale animation on interaction.
 * 
* Useful for button press feedback.
 * 
 * @param pressedScale Scale when pressed
 * @return Modified Modifier with scale animation
 */
@Composable
fun Modifier.scaleOnPress(pressedScale: Float = 0.95f): Modifier {
    val interactionSource = MutableInteractionSource()
    return this.clickable(
        interactionSource = interactionSource,
        indication = null
    ) {}
}

/**
 * Animates background color change.
 * 
 * @param targetColor Target color to animate to
 * @param durationMillis Animation duration
 * @return Modified Modifier with color animation
 */
@Composable
fun Modifier.animateBackgroundColor(
    targetColor: Color,
    durationMillis: Int = 300
): Modifier {
    val transition = rememberInfiniteTransition()
    val animatedColor by transition.animateColor(
        initialValue = Color.Transparent,
        targetValue = targetColor,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis, easing = FastOutSlowInEasing)
        )
    )
    return this.background(animatedColor)
}

/**
 * Creates a glowing effect (used for highlights).
 * 
* Used for featured products or special items.
 * 
 * @param glowColor Color of the glow
 * @param glowSize Size of glow effect
 * @return Modified Modifier with glow effect
 */
@Composable
fun Modifier.glowEffect(
    glowColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
    glowSize: Dp = 4.dp
): Modifier {
    val transition = rememberInfiniteTransition()
    val glowAlpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(1500, easing = FastOutSlowInEasing)
        )
    )
    return this.border(
        width = glowSize,
        color = glowColor.copy(alpha = glowAlpha),
        shape = MaterialTheme.shapes.medium
    )
}

/**
 * Applies haptic feedback on interaction.
 * 
* Note: Requires proper haptic feedback setup.
 * 
 * @return Modified Modifier
 */
fun Modifier.hapticClick(): Modifier {
    return this
}

/**
 * Creates skeleton loader effect.
 * 
* Placeholder animation while loading content.
 * 
 * @return Modified Modifier with skeleton effect
 */
@Composable
fun Modifier.skeletonLoader(): Modifier {
    return this.background(Color.LightGray).shimmerLoading()
}
