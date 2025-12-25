package com.noghre.sod.presentation.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.noghre.sod.R

// ============================================
// 💫 UI State Sealed Class
// ============================================

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val exception: Exception? = null) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}

// ============================================
// 📎 Error View Component
// ============================================

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.ErrorOutline,
    title: String = stringResource(R.string.error_title),
    actionLabel: String = stringResource(R.string.retry)
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
            contentDescription = stringResource(R.string.error_icon_description),
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )
        
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .padding(end = 8.dp)
            )
            Text(actionLabel)
        }
    }
}

// ============================================
// 🔗 Network Error View
// ============================================

@Composable
fun NetworkErrorView(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorView(
        message = stringResource(R.string.network_error_message),
        onRetry = onRetry,
        modifier = modifier,
        icon = Icons.Default.WifiOff,
        title = stringResource(R.string.no_internet_title)
    )
}

// ============================================
// 💾 Server Error View
// ============================================

@Composable
fun ServerErrorView(
    statusCode: Int? = null,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val message = if (statusCode != null) {
        "Server Error: $statusCode"
    } else {
        stringResource(R.string.server_error_message)
    }
    
    ErrorView(
        message = message,
        onRetry = onRetry,
        modifier = modifier,
        icon = Icons.Default.Error,
        title = stringResource(R.string.server_error_title)
    )
}

// ============================================
// 💫 Empty State View
// ============================================

@Composable
fun EmptyView(
    message: String = stringResource(R.string.no_items_found),
    icon: ImageVector = Icons.Default.Inbox,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
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
            contentDescription = stringResource(R.string.empty_state_icon),
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (action != null) {
            Spacer(modifier = Modifier.height(24.dp))
            action()
        }
    }
}

// ============================================
// ⏳ Loading Indicator
// ============================================

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    contentDescription: String = stringResource(R.string.loading)
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp
        )
    }
}

// ============================================
// 💁 Loading Skeleton
// ============================================

@Composable
fun LoadingSkeletonItem(
    modifier: Modifier = Modifier,
    height: Int = 150
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        // Placeholder
    }
}

// ============================================
// 🙋 Content with State Handling
// ============================================

@Composable
fun <T> StateContent(
    state: UiState<T>,
    loading: @Composable () -> Unit = { LoadingIndicator() },
    error: @Composable (String) -> Unit = { message -> ErrorView(message = message, onRetry = {}) },
    empty: @Composable () -> Unit = { EmptyView() },
    success: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> loading()
        is UiState.Error -> error(state.message)
        is UiState.Empty -> empty()
        is UiState.Success -> success(state.data)
    }
}

// ============================================
// 🔍 String Resources for Accessibility
// ============================================

/*
Add these to res/values/strings.xml:

<string name="error_title">خطا در بارگیری</string>
<string name="error_icon_description">نماد خطا</string>
<string name="retry">تالش مجدد</string>
<string name="no_internet_title">بدون اتصال اینترنت</string>
<string name="network_error_message">لطفا اتصال اینترنت خود را بررسی کنید</string>
<string name="server_error_title">خطای سرور</string>
<string name="server_error_message">مشکلی در سمت سرور پیش آمد</string>
<string name="no_items_found">هیچ آیتمی یافت نشد</string>
<string name="empty_state_icon">نماد خالی</string>
<string name="loading">در حال بارگیری...</string>
*/
