package com.noghre.sod.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.noghre.sod.core.ext.shimmerLoading
import com.noghre.sod.ui.theme.PersianFontFamily
import com.valentinilk.shimmer.shimmer

/**
 * Loading indicators for various states in NoghreSod.
 * 
* Includes skeleton loaders, progress dialogs, and pagination indicators.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */

/**
 * Shimmer skeleton loader for product card.
 * 
* Animates as placeholder while loading product data.
 */
@Composable
fun ProductCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Image skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .shimmer()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Title skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surface)
                .shimmer()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Price skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surface)
                .shimmer()
        )
    }
}

/**
 * Shimmer skeleton loader for list item.
 */
@Composable
fun ListItemSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar skeleton
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surface)
                .shimmer()
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmer()
            )
            
            // Description skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmer()
            )
        }
    }
}

/**
 * Full-screen loading dialog.
 * 
* Shows circular progress indicator with optional message.
 * 
 * @param message Loading message (optional)
 * @param isDismissible Whether user can dismiss (default: false)
 * @param onDismiss Callback when dismissed
 */
@Composable
fun LoadingDialog(
    message: String? = null,
    isDismissible: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { if (isDismissible) onDismiss() },
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
                
                if (message != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = message,
                        fontFamily = PersianFontFamily,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {}
    )
}

/**
 * Loading indicator for pagination (bottom of list).
 * 
* Shows when loading more items.
 */
@Composable
fun PaginationLoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
    }
}

/**
 * Inline progress indicator.
* Small loading spinner with optional text.
 * 
 * @param text Optional text to show with spinner
 * @param modifier Modifier for styling
 */
@Composable
fun InlineProgressIndicator(
    text: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 2.dp
        )
        
        if (text != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontFamily = PersianFontFamily,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
