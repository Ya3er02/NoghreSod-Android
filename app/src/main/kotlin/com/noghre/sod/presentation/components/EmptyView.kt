package com.noghre.sod.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import timber.log.Timber

/**
 * Reusable empty state component for when no data is available.
 *
 * @param message Empty state message to display
 * @param actionText Optional action button text
 * @param onActionClick Optional action button callback
 * @param modifier Modifier for customization
 */
@Composable
fun EmptyView(
    message: String = "موردی یافت نشد",
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Timber.d("Showing empty state with message: $message")
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = "خالی",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    Timber.d("Empty state action button clicked: $actionText")
                    onActionClick()
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(actionText)
            }
        }
    }
}

/**
 * Compact empty state for use in smaller containers (lists, cards).
 *
 * @param message Empty state message
 * @param actionText Optional action button text
 * @param onActionClick Optional action callback
 * @param modifier Modifier
 */
@Composable
fun CompactEmptyView(
    message: String = "موردی یافت نشد",
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = {
                    Timber.d("Compact empty state action clicked: $actionText")
                    onActionClick()
                }
            ) {
                Text(actionText)
            }
        }
    }
}
