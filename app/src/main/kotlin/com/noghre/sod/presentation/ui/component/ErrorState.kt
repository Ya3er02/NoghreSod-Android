package com.noghre.sod.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NetworkOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.noghre.sod.ui.theme.PersianFontFamily

/**
 * Error and empty state components for NoghreSod.
 * 
* Provides user-friendly error messages and recovery options.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */

/**
 * Full-screen error state display.
 * 
* Shows error icon, message, and optional retry button.
 * 
 * @param message Error message to display
 * @param onRetry Callback for retry button
 * @param modifier Modifier for styling
 */
@Composable
fun ErrorScreen(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Error
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "خطای پیدا شد",
            fontFamily = PersianFontFamily,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            fontFamily = PersianFontFamily,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "تلاش دوباره",
                    fontFamily = PersianFontFamily
                )
            }
        }
    }
}

/**
 * Empty state screen (no data found).
 * 
* Shows when list is empty or no results match search.
 * 
 * @param title Title message
 * @param description Detailed description
 * @param onRetry Optional retry callback
 * @param modifier Modifier for styling
 */
@Composable
fun EmptyStateScreen(
    title: String = "بی نتیجه",
    description: String = "هیچ عنوانی یافت نشد",
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            fontFamily = PersianFontFamily,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            fontFamily = PersianFontFamily,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text(
                    text = "بازبی ری تلاش",
                    fontFamily = PersianFontFamily
                )
            }
        }
    }
}

/**
 * Network error dialog.
 * 
* Shows when network connection is unavailable.
 * 
 * @param onRetry Retry callback
 * @param onDismiss Dismiss callback
 */
@Composable
fun NetworkErrorDialog(
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.NetworkOff,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "مشکل اتصال",
                fontFamily = PersianFontFamily,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "اتصال انترنت خود را بررسی کرده و دوباره سعی کنید.",
                fontFamily = PersianFontFamily,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onRetry) {
                Text(
                    text = "تلاش دوباره",
                    fontFamily = PersianFontFamily
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "بستن",
                    fontFamily = PersianFontFamily
                )
            }
        }
    )
}

/**
 * Error snackbar message.
 * 
* Shows error message as toast notification.
 * 
 * @param message Error message
 * @param action Optional action button text and callback
 * @param modifier Modifier for styling
 */
@Composable
fun ErrorSnackbar(
    message: String,
    action: Pair<String, () -> Unit>? = null,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier.padding(8.dp),
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        action = {
            if (action != null) {
                TextButton(onClick = action.second) {
                    Text(
                        text = action.first,
                        fontFamily = PersianFontFamily,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    ) {
        Text(
            text = message,
            fontFamily = PersianFontFamily,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
