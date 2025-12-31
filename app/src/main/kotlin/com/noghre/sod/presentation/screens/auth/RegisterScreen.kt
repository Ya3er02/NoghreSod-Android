package com.noghre.sod.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noghre.sod.R
import com.noghre.sod.presentation.components.PersianButton
import com.noghre.sod.presentation.components.PersianTextField

/**
 * Register Screen - User registration.
 * 
* Form for new user registration.
 * Shows loading states and error messages.
 * Link to login screen.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val uiState by viewModel.authUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var nameInput by remember { mutableStateOf("") }
    var mobileInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "گفته تاب جديد",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "به جامعه NoghreSod خوش امديد",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error message
        if (error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = error ?: "خطای نامشخصی",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // Name input
        PersianTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            placeholder = "نام عممی",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        // Mobile input
        PersianTextField(
            value = mobileInput,
            onValueChange = { mobileInput = it },
            placeholder = "شماره موباايل",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        // Email input
        PersianTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            placeholder = "پست الکترونیکی",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        // Password input
        PersianTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            placeholder = "رمز عبور",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = null
                    )
                }
            }
        )

        // Confirm password input
        PersianTextField(
            value = confirmPasswordInput,
            onValueChange = { confirmPasswordInput = it },
            placeholder = "تایيد رمز",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* TODO: Handle registration */ }
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            visualTransformation = if (confirmPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = { confirmPasswordVisible = !confirmPasswordVisible }
                ) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = null
                    )
                }
            }
        )

        // Terms and conditions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    agreeTerms = !agreeTerms
                },
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = agreeTerms,
                onCheckedChange = { agreeTerms = it },
                enabled = !isLoading
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = "با 
",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "شرايط و ضوابط و سياست حريم عمومی",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        // TODO: Navigate to terms
                    }
                )
                Text(
                    text = "موافقم",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Register button
        PersianButton(
            text = if (isLoading) "در حال ثبت..." else "ثبت نام",
            onClick = {
                if (
                    nameInput.isNotEmpty() &&
                    mobileInput.isNotEmpty() &&
                    emailInput.isNotEmpty() &&
                    passwordInput.isNotEmpty() &&
                    confirmPasswordInput.isNotEmpty() &&
                    passwordInput == confirmPasswordInput &&
                    agreeTerms
                ) {
                    viewModel.register(
                        name = nameInput,
                        mobile = mobileInput,
                        email = emailInput,
                        password = passwordInput
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && 
                nameInput.isNotEmpty() && 
                mobileInput.isNotEmpty() && 
                emailInput.isNotEmpty() && 
                passwordInput.isNotEmpty() && 
                confirmPasswordInput.isNotEmpty() &&
                passwordInput == confirmPasswordInput &&
                agreeTerms
        )

        // Login link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "قبلا ثبت نام کرده ايد؟ ",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "ورود",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
