package com.noghre.sod.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LanguageOutlined
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Settings Screen - App settings and preferences.
 * 
* Language and theme selection.
 * Notification preferences.
 * Privacy and security settings.
 * About app information.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.settingsUiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top app bar
        TopAppBar(
            title = { Text("تنظیمات") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        )

        // Settings content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Language settings
            item {
                SettingsSection(
                    title = "زبان",
                    icon = Icons.Default.LanguageOutlined
                )
            }

            item {
                LanguageOption(
                    language = "فارسی",
                    isSelected = uiState.language == "fa",
                    onSelect = { viewModel.setLanguage("fa") }
                )
            }

            item {
                LanguageOption(
                    language = "English",
                    isSelected = uiState.language == "en",
                    onSelect = { viewModel.setLanguage("en") }
                )
            }

            item {
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }

            // Theme settings
            item {
                SettingsSection(
                    title = "پس زمینه",
                    icon = Icons.Default.LanguageOutlined
                )
            }

            item {
                ThemeOption(
                    theme = "روشن",
                    isSelected = uiState.theme == "light",
                    onSelect = { viewModel.setTheme("light") }
                )
            }

            item {
                ThemeOption(
                    theme = "تاریک",
                    isSelected = uiState.theme == "dark",
                    onSelect = { viewModel.setTheme("dark") }
                )
            }

            item {
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }

            // Notifications
            item {
                SettingsSection(
                    title = "اطلاع رسانی",
                    icon = Icons.Default.Notifications
                )
            }

            item {
                NotificationToggle(
                    title = "اطلاعات مجموعه",
                    enabled = uiState.notificationsEnabled,
                    onToggle = { viewModel.setNotificationsEnabled(it) }
                )
            }

            item {
                NotificationToggle(
                    title = "اطلاعات سبد خريد",
                    enabled = uiState.cartNotificationsEnabled,
                    onToggle = { viewModel.setCartNotificationsEnabled(it) }
                )
            }

            item {
                NotificationToggle(
                    title = "اطلاعات کاهش قيمت",
                    enabled = uiState.priceDropNotificationsEnabled,
                    onToggle = { viewModel.setPriceDropNotificationsEnabled(it) }
                )
            }

            item {
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }

            // Privacy and security
            item {
                SettingsSection(
                    title = "امنيت",
                    icon = Icons.Default.Security
                )
            }

            item {
                SettingsMenuItem(
                    title = "تغيير رمز عبور",
                    subtitle = "رمز خود را به روز رسانی نگه دارید",
                    onClick = { /* TODO: Navigate to password change */ }
                )
            }

            item {
                SettingsMenuItem(
                    title = "دولت اعتماد شده وسيله",
                    subtitle = "وسايلي را مديريت کنید",
                    onClick = { /* TODO: Navigate to trusted devices */ }
                )
            }

            item {
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }

            // About
            item {
                SettingsSection(title = "درباره")
            }

            item {
                SettingsMenuItem(
                    title = "نسخه برنامه",
                    subtitle = "1.0.0",
                    onClick = { }
                )
            }

            item {
                SettingsMenuItem(
                    title = "سياست حريم عمومی",
                    subtitle = "برنامه و سياستها",
                    onClick = { /* TODO: Navigate to privacy policy */ }
                )
            }

            item {
                SettingsMenuItem(
                    title = "شرايط استفاده",
                    subtitle = "شرايط استفاده کنید",
                    onClick = { /* TODO: Navigate to terms */ }
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: androidx.compose.material.icons.Icons = Icons.Default.LanguageOutlined
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun LanguageOption(
    language: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = language,
            style = MaterialTheme.typography.bodyMedium
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ThemeOption(
    theme: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = theme,
            style = MaterialTheme.typography.bodyMedium
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun NotificationToggle(
    title: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = enabled,
            onCheckedChange = onToggle
        )
    }
}

@Composable
fun SettingsMenuItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class SettingsUiState(
    val language: String = "fa",
    val theme: String = "light",
    val notificationsEnabled: Boolean = true,
    val cartNotificationsEnabled: Boolean = true,
    val priceDropNotificationsEnabled: Boolean = true
)
