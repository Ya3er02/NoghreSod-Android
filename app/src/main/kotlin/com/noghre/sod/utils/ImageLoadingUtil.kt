package com.noghre.sod.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 * Image loading optimization utilities.
 * Note: This requires Coil or Glide dependency
 */
object ImageLoadingUtil {

    /**
     * Get optimized request options for Glide.
     */
    fun getOptimizedGlideOptions(): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .timeout(10000) // 10 seconds timeout
            .centerCrop()
    }

    /**
     * Get optimized request options for thumbnails.
     */
    fun getThumbnailGlideOptions(): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(300, 300) // Resize to thumbnail size
            .centerCrop()
    }
}

/**
 * Loading placeholder composable.
 */
@Composable
fun ImageLoadingPlaceholder(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 200.dp
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(8.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

/**
 * Error placeholder composable.
 */
@Composable
fun ImageErrorPlaceholder(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 200.dp
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(8.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Text(
                "Failed to load image",
                color = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}
