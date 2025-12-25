package com.noghre.sod.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.noghre.sod.domain.entities.User
import com.noghre.sod.ui.components.ConfirmDialog
import com.noghre.sod.ui.components.ErrorState
import com.noghre.sod.ui.components.LoadingState

/**
 * 👤 User Profile Screen
 *
 * Features:
 * - User profile information
 * - Edit profile
 * - Order history
 * - Wishlist
 * - Settings
 * - Logout
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutConfirm by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("👤 پروفايل") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "برگشت")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState) {
            is ProfileUiState.Loading ->
                LoadingState(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding))
            is ProfileUiState.Success ->
                ProfileContent(
                    user = state.user,
                    onNavigateToOrders = onNavigateToOrders,
                    onNavigateToSettings = {
                        // TODO: Navigate to settings
                    },
                    onEditProfile = {
                        // TODO: Navigate to edit profile
                    },
                    onLogout = { showLogoutConfirm = true },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            is ProfileUiState.Error ->
                ErrorState(
                    error = state.error,
                    onRetry = viewModel::loadProfile,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
        }
    }
    
    // Logout confirmation dialog
    ConfirmDialog(
        title = "خروج",
        message = "اآيا مي خواهيد از حسابخابي خارج شويد؟",
        onConfirm = {
            viewModel.logout()
            onLogout()
        },
        onDismiss = { showLogoutConfirm = false },
        isShown = showLogoutConfirm
    )
}

/**
 * Profile content
 */
@Composable
private fun ProfileContent(
    user: User,
    onNavigateToOrders: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with avatar
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar
                if (user.avatarUrl?.isNotEmpty() == true) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = user.name,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user.name.firstOrNull()?.toString() ?: "U",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // User name
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                // Email
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        
        // Edit Profile Button
        item {
            Button(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("ويرايش پروفايل")
            }
        }
        
        // Menu Items
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Orders
                    MenuItem(
                        icon = Icons.Filled.ShoppingCart,
                        title = "سفارشات من",
                        onClick = onNavigateToOrders
                    )
                    Divider()
                    
                    // Wishlist
                    MenuItem(
                        icon = Icons.Filled.Favorite,
                        title = "فاوریته‌ها",
                        onClick = { /* TODO */ }
                    )
                    Divider()
                    
                    // Addresses
                    MenuItem(
                        icon = Icons.Filled.LocationOn,
                        title = "آدرس‌هاي من",
                        onClick = { /* TODO */ }
                    )
                    Divider()
                    
                    // Payment Methods
                    MenuItem(
                        icon = Icons.Filled.CreditCard,
                        title = "روش‌هاي پرداخت",
                        onClick = { /* TODO */ }
                    )
                    Divider()
                    
                    // Settings
                    MenuItem(
                        icon = Icons.Filled.Settings,
                        title = "تنظیمات",
                        onClick = onNavigateToSettings
                    )
                }
            }
        }
        
        // Account Info
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "اطلاعات حساب",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("شمار کاربری")
                            Text(user.id, color = MaterialTheme.colorScheme.outline)
                        }
                        Divider()
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("تاريخ عضويت")
                            Text(user.createdAt, color = MaterialTheme.colorScheme.outline)
                        }
                        Divider()
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("شمار سفارشات")
                            Text(user.totalOrders.toString(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        // Logout Button
        item {
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("خروج")
            }
        }
    }
}

/**
 * Menu item component
 */
@Composable
private fun MenuItem(
    icon: androidx.compose.material.icons.Icons,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val error: Exception) : ProfileUiState()
}
