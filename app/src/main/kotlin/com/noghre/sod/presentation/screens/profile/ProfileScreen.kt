package com.noghre.sod.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noghre.sod.presentation.components.PersianButton

/**
 * Profile Screen - User profile management.
 * 
* Shows user information, orders, addresses.
 * Edit profile and settings options.
 * Logout functionality.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onEditClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val uiState by viewModel.profileUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Profile header
        item {
            ProfileHeader(
                user = uiState.user,
                onEditClick = onEditClick
            )
        }

        // Profile stats
        item {
            ProfileStats(
                totalOrders = uiState.totalOrders,
                totalSpent = uiState.totalSpent,
                wishlistCount = uiState.wishlistCount
            )
        }

        // Menu items
        item {
            ProfileMenuItems(
                onOrdersClick = onOrdersClick,
                onWishlistClick = onWishlistClick,
                onAddressClick = onAddressClick,
                onSettingsClick = onSettingsClick
            )
        }

        // Logout button
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                PersianButton(
                    text = "خروج",
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ProfileHeader(
    user: User?,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.name?.firstOrNull() ?: "U").toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // User info
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = user?.name ?: "کاربر",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user?.email ?: "عضویت را تکميل کنید",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = user?.mobile ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Edit button
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileStats(
    totalOrders: Int,
    totalSpent: Double,
    wishlistCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            icon = Icons.Default.ShoppingCart,
            label = "سفارشها",
            value = totalOrders.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.AttachMoney,
            label = "کل خريد",
            value = "$totalSpent ر",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.Favorite,
            label = "علاقه مندی",
            value = wishlistCount.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.material.icons.Icons,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ProfileMenuItems(
    onOrdersClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onAddressClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfileMenuItem(
                icon = Icons.Default.ShoppingCart,
                title = "سفارشهای من",
                subtitle = "مراطعه و نظارت بر سفارشها",
                onClick = onOrdersClick
            )
            Divider()
            ProfileMenuItem(
                icon = Icons.Default.Favorite,
                title = "لیست علاقه",
                subtitle = "محصولات مورد علاقه",
                onClick = onWishlistClick
            )
            Divider()
            ProfileMenuItem(
                icon = Icons.Default.LocationOn,
                title = "آدرسها",
                subtitle = "مديريت آدرس تحويل",
                onClick = onAddressClick
            )
            Divider()
            ProfileMenuItem(
                icon = Icons.Default.Settings,
                title = "تنظیمات",
                subtitle = "تنظیمات اکاونت و أمنیت",
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.material.icons.Icons,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val mobile: String
)

data class ProfileUiState(
    val user: User? = null,
    val totalOrders: Int = 0,
    val totalSpent: Double = 0.0,
    val wishlistCount: Int = 0
)
