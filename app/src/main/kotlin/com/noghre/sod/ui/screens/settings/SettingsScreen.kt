package com.noghre.sod.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.noghre.sod.ui.theme.Spacing

@Composable
fun SettingsScreen(navController: NavController) {
    val darkModeEnabled = remember { mutableStateOf(false) }
    val biometricEnabled = remember { mutableStateOf(false) }
    val notificationsEnabled = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.medium)
    ) {
        Text("تمبل تاریك")

        SettingsItem(
            title = "حالت تاریک",
            enabled = darkModeEnabled.value,
            onToggle = { darkModeEnabled.value = it },
            modifier = Modifier.fillMaxWidth()
        )

        SettingsItem(
            title = "روز بیومتریک",
            enabled = biometricEnabled.value,
            onToggle = { biometricEnabled.value = it },
            modifier = Modifier.fillMaxWidth()
        )

        SettingsItem(
            title = "اطلاعات رسانی",
            enabled = notificationsEnabled.value,
            onToggle = { notificationsEnabled.value = it },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier.padding(Spacing.small),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = enabled, onCheckedChange = onToggle)
    }
}
