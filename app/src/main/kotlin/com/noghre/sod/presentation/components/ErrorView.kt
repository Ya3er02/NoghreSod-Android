package com.noghre.sod.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.noghre.sod.core.error.AppError
import timber.log.Timber

/**
 * Reusable error view component for displaying errors with retry option.
 * Full-screen error display.
 *
 * @param error The AppError to display
 * @param onRetry Optional retry callback
 * @param modifier Modifier for customization
 */
@Composable
fun ErrorView(
    error: AppError,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Timber.d("Showing error: ${error.message}")
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "خطا",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "خطا",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error.toUserMessage(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    Timber.d("Retry button clicked")
                    onRetry()
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("تلاش مجدد")
            }
        }
    }
}

/**
 * Compact error view for inline errors (e.g., in cards, dialogs)
 * Displays error in a card format.
 *
 * @param error The AppError to display
 * @param onRetry Optional retry callback
 * @param modifier Modifier for customization
 */
@Composable
fun CompactErrorView(
    error: AppError,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Timber.d("Showing compact error: ${error.message}")
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = error.toUserMessage(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            
            if (onRetry != null) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        Timber.d("Retry button clicked in compact view")
                        onRetry()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "تلاش مجدد",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
