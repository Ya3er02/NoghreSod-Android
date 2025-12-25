package com.noghre.sod.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.noghre.sod.core.ui.UiError

/**
 * Full-screen error view.
 */
@Composable
fun FullScreenErrorView(
    error: UiError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error icon
        Icon(
            imageVector = getErrorIcon(error),
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = getErrorColor(error)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Error title
        Text(
            text = getErrorTitle(error),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Error message
        Text(
            text = error.message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Action buttons
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Try Again")
        }
        
        if (onDismiss != null) {
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Dismiss")
            }
        }
    }
}

/**
 * Inline error view for embedded display.
 */
@Composable
fun InlineErrorView(
    error: UiError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = getErrorIcon(error),
            contentDescription = "Error",
            modifier = Modifier.size(48.dp),
            tint = getErrorColor(error)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = error.message,
            style = MaterialTheme.typography.bodySmall,
            color = getErrorColor(error),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedButton(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(fraction = 0.6f)
        ) {
            Text("Retry")
        }
    }
}

/**
 * Error snackbar view for bottom display.
 */
@Composable
fun ErrorSnackbar(
    error: UiError,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = getErrorTitle(error),
            style = MaterialTheme.typography.labelMedium,
            color = getErrorColor(error)
        )
        
        Text(
            text = error.message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Get appropriate icon for error type.
 */
private fun getErrorIcon(error: UiError): ImageVector = when (error) {
    is UiError.NetworkError -> Icons.Default.CloudOff
    is UiError.TimeoutError -> Icons.Default.WarningAmber
    is UiError.ServerError -> Icons.Default.Error
    is UiError.HttpError -> Icons.Default.ErrorOutline
    is UiError.ValidationError -> Icons.Default.WarningAmber
    is UiError.UnknownError -> Icons.Default.Error
}

/**
 * Get color for error type.
 */
private fun getErrorColor(error: UiError): Color = when (error) {
    is UiError.NetworkError -> Color(0xFFFF9800)
    is UiError.TimeoutError -> Color(0xFFFF9800)
    is UiError.ServerError -> Color(0xFFF44336)
    is UiError.HttpError -> Color(0xFFF44336)
    is UiError.ValidationError -> Color(0xFFFF9800)
    is UiError.UnknownError -> Color(0xFFF44336)
}

/**
 * Get title for error type.
 */
private fun getErrorTitle(error: UiError): String = when (error) {
    is UiError.NetworkError -> "Connection Error"
    is UiError.TimeoutError -> "Request Timeout"
    is UiError.ServerError -> "Server Error"
    is UiError.HttpError -> "HTTP Error ${error.code}"
    is UiError.ValidationError -> "Validation Error"
    is UiError.UnknownError -> "Something went wrong"
}