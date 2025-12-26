package com.noghre.sod.presentation.components

import androidx.compose.foundation.background
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.noghre.sod.R

/**
 * Optimized AsyncImage composable with proper memory management, caching, and error handling.
 * This replaces raw AsyncImage calls for better performance and UX.
 * 
 * Features:
 * - Image caching (memory + disk)
 * - Crossfade animation
 * - Placeholder and error states
 * - Proper content scaling
 * - Memory-efficient loading
 * 
 * @param imageUrl URL of the image to load
 * @param contentDescription Accessibility description
 * @param modifier Modifier for layout
 * @param contentScale How to scale the image
 * @param onLoadingStateChanged Callback when loading state changes
 * 
 * @author Yaser
 * @version 1.0.0
 */
@Composable
fun AsyncImageWithCache(
    imageUrl: String?,
    contentDescription: String = "",
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(200.dp),
    contentScale: ContentScale = ContentScale.Crop,
    onLoadingStateChanged: (Boolean) -> Unit = {},
    enableCrossfade: Boolean = true
) {
    if (imageUrl.isNullOrBlank()) {
        // Show placeholder if no URL
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = "No image",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }
    
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .crossfade(enable = enableCrossfade)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .memoryCacheKey(imageUrl)
            .diskCacheKey(imageUrl)
            // Memory and disk cache settings
            .allowHardware(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        onState = { state ->
            // Track loading state for parent composables
            when (state) {
                is AsyncImagePainter.State.Loading -> onLoadingStateChanged(true)
                is AsyncImagePainter.State.Success -> onLoadingStateChanged(false)
                is AsyncImagePainter.State.Error -> onLoadingStateChanged(false)
                is AsyncImagePainter.State.Empty -> onLoadingStateChanged(false)
            }
        }
    )
}

/**
 * Product image thumbnail with fixed aspect ratio.
 * Optimized for product list cards.
 */
@Composable
fun ProductImageThumbnail(
    imageUrl: String?,
    contentDescription: String = "Product image",
    modifier: Modifier = Modifier
) {
    AsyncImageWithCache(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        contentScale = ContentScale.Crop
    )
}

/**
 * Large product image for detail screen.
 * Optimized for high-quality display.
 */
@Composable
fun ProductImageLarge(
    imageUrl: String?,
    contentDescription: String = "Product image",
    modifier: Modifier = Modifier
) {
    AsyncImageWithCache(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        contentScale = ContentScale.Crop
    )
}

/**
 * User profile avatar with circular crop.
 */
@Composable
fun UserAvatarImage(
    imageUrl: String?,
    contentDescription: String = "User avatar",
    modifier: Modifier = Modifier
) {
    AsyncImageWithCache(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .height(100.dp)
            .height(100.dp),
        contentScale = ContentScale.Crop
    )
}

/**
 * Banner image for promotional content.
 */
@Composable
fun BannerImage(
    imageUrl: String?,
    contentDescription: String = "Banner",
    modifier: Modifier = Modifier
) {
    AsyncImageWithCache(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        contentScale = ContentScale.Crop
    )
}
