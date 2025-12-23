package com.noghre.sod.presentation.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noghre.sod.presentation.components.ErrorMessage
import com.noghre.sod.presentation.components.LoadingBar
import com.noghre.sod.presentation.components.PrimaryButton
import com.noghre.sod.presentation.components.SecondaryButton
import com.noghre.sod.presentation.viewmodel.AuthViewModel
import com.noghre.sod.utils.InputValidators

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Title
        Text(
            text = "Noghresod",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "ورود به حساب",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Error Message
        if (uiState.error != null) {
            ErrorMessage(
                error = uiState.error ?: "خطای نامشخص",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("ایمیل") },
            placeholder = { Text("name@example.com") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("رمز عبور") },
            placeholder = { Text("•••••••••") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Button(
                    onClick = { showPassword = !showPassword },
                    modifier = Modifier.size(24.dp)
                ) {
                    Text(if (showPassword) "🙈" else "👁️")
                }
            },
            singleLine = true,
            enabled = !uiState.isLoading
        )

        // Login Button
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = email.isNotEmpty() && password.isNotEmpty() && !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                LoadingBar()
            } else {
                Text("ورود")
            }
        }

        // Register Link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("حسابی ندارید؟ ")
            TextButton(onClick = onRegisterClick) {
                Text("ثبت نام کنید")
            }
        }
    }
}

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo/Title
        Text(
            text = "Noghresod",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "ثبت نام",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Error Message
        if (uiState.error != null) {
            ErrorMessage(
                error = uiState.error ?: "خطای نامشخص",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // First Name
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("نام") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        // Last Name
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("نام خانوادگی") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("ایمیل") },
            placeholder = { Text("name@example.com") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        // Phone
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("شماره تلفن") },
            placeholder = { Text("09xxxxxxxxx") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("رمز عبور") },
            placeholder = { Text("•••••••••") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        // Register Button
        Button(
            onClick = {
                viewModel.register(email, phone, password, firstName, lastName)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty() &&
                    firstName.isNotEmpty() && lastName.isNotEmpty() && !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                LoadingBar()
            } else {
                Text("ثبت نام")
            }
        }

        // Login Link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("حسابی دارید؟ ")
            TextButton(onClick = onLoginClick) {
                Text("وارد شوید")
            }
        }
    }
}
