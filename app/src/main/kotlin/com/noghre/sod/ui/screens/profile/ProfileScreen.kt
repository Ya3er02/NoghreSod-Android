package com.noghre.sod.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noghre.sod.presentation.viewmodel.ProfileViewModel
import com.noghre.sod.ui.theme.Spacing

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    val fullName = remember { mutableStateOf(uiState.user?.fullName ?: "") }
    val email = remember { mutableStateOf(uiState.user?.email ?: "") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.medium)
            ) {
                OutlinedTextField(
                    value = fullName.value,
                    onValueChange = { fullName.value = it },
                    label = { Text("نام كامل") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.medium))

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("ایميل") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.medium))

                Button(
                    onClick = {
                        viewModel.updateProfile(fullName.value, email.value, null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("به روز رسانی")
                }

                if (uiState.successMessage != null) {
                    Spacer(modifier = Modifier.height(Spacing.medium))
                    Text(uiState.successMessage)
                }
            }
        }

        if (uiState.error != null) {
            Text(
                uiState.error,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(Spacing.medium)
            )
        }
    }
}
