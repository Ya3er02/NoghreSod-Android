package com.noghre.sod.presentation.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ============================================
// Loading States
// ============================================

/**
 * Reusable loading indicator
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = Color.Blue
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            color = color
        )
    }
}

/**
 * Loading skeleton for content
 */
@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier,
    width: Dp = 200.dp,
    height: Dp = 16.dp,
    cornerRadius: Dp = 4.dp
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.LightGray.copy(alpha = 0.3f))
    )
}

// ============================================
// Error States
// ============================================

/**
 * Reusable error display
 */
@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.Error,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFEBEE))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFC62828),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = message,
            color = Color(0xFFC62828),
            style = TextStyle(fontSize = 14.sp),
            modifier = Modifier.semantics { contentDescription = message }
        )
        if (onRetry != null) {
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Retry")
            }
        }
    }
}

/**
 * Empty state display
 */
@Composable
fun EmptyState(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = title,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Gray
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        if (action != null) {
            Spacer(modifier = Modifier.height(16.dp))
            action()
        }
    }
}

// ============================================
// Image Loading
// ============================================

/**
 * Network image with loading and error states
 */
@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    onLoadingStart: (() -> Unit)? = null,
    onLoadingSuccess: (() -> Unit)? = null,
    onLoadingError: (() -> Unit)? = null
) {
    val context = LocalContext.current
    
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        onLoading = { onLoadingStart?.invoke() },
        onSuccess = { onLoadingSuccess?.invoke() },
        onError = { onLoadingError?.invoke() }
    )
}

// ============================================
// Animations
// ============================================

/**
 * Fade in/out animation wrapper
 */
@Composable
fun FadeInOutAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        content()
    }
}

/**
 * Slide up animation wrapper
 */
@Composable
fun SlideUpAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        content()
    }
}

// ============================================
// Delayed Actions
// ============================================

/**
 * Execute action after delay
 */
@Composable
fun DelayedAction(
    delayMillis: Long = 1000,
    action: suspend () -> Unit
) {
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            delay(delayMillis)
            action()
        }
    }
}

/**
 * Auto-dismiss action after delay
 */
@Composable
fun AutoDismissable(
    visible: Boolean,
    delayMillis: Long = 3000,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    if (visible) {
        LaunchedEffect(visible) {
            delay(delayMillis)
            onDismiss()
        }
        content()
    }
}

// ============================================
// Layout Helpers
// ============================================

/**
 * Safe padding wrapper
 */
@Composable
fun SafeAreaPadding(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        content = content
    )
}

/**
 * Center aligned column
 */
@Composable
fun CenterColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

/**
 * Responsive grid
 */
@Composable
fun ResponsiveGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    
    Column(modifier = modifier) {
        content()
    }
}

// ============================================
// Click Handlers
// ============================================

/**
 * Clickable modifier with ripple effect
 */
fun Modifier.clickableWithRipple(
    enabled: Boolean = true,
    onClick: () -> Unit
) = clickable(
    enabled = enabled,
    onClickLabel = "Button",
    onClick = onClick
)

/**
 * Debounced click handler
 */
@Composable
fun debouncedClick(
    delayMillis: Long = 300,
    onClick: suspend () -> Unit
): () -> Unit {
    val scope = rememberCoroutineScope()
    var lastClickTime = 0L
    
    return {
        val now = System.currentTimeMillis()
        if (now - lastClickTime > delayMillis) {
            lastClickTime = now
            scope.launch {
                onClick()
            }
        }
    }
}
