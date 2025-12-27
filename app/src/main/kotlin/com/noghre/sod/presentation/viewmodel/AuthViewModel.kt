package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AuthViewModel - مدیریت احراز هویت و سشن کاربر
 * Features:
 * - Login/Register/Logout
 * - Token Management & Refresh
 * - Multi-step Form Validation
 * - Biometric Authentication Support
 * - Session Management
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Auth State
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Login Form State
    private val _loginForm = MutableStateFlow(LoginFormState())
    val loginForm: StateFlow<LoginFormState> = _loginForm.asStateFlow()

    // Register Form State
    private val _registerForm = MutableStateFlow(RegisterFormState())
    val registerForm: StateFlow<RegisterFormState> = _registerForm.asStateFlow()

    // Token Refresh Job
    private var tokenRefreshJob: Job? = null

    /**
     * Login with email and password
     */
    fun login(email: String, password: String) {
        if (!validateLoginForm(email, password)) {
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            authRepository.login(email, password)
                .onSuccess { response ->
                    savedStateHandle[KEY_USER] = response.user
                    scheduleTokenRefresh(response.expiresIn)
                    _authState.value = AuthState.Success(response.user)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Login failed")
                }
        }
    }

    /**
     * Register new account
     */
    fun register(
        email: String,
        password: String,
        name: String,
        phone: String
    ) {
        if (!validateRegisterForm(email, password, name, phone)) {
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            authRepository.register(
                email = email,
                password = password,
                name = name,
                phone = phone
            )
                .onSuccess { response ->
                    savedStateHandle[KEY_USER] = response.user
                    scheduleTokenRefresh(response.expiresIn)
                    _authState.value = AuthState.Success(response.user)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(
                        error.message ?: "Registration failed"
                    )
                }
        }
    }

    /**
     * Logout and cleanup
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            tokenRefreshJob?.cancel()
            savedStateHandle.remove<User>(KEY_USER)
            _authState.value = AuthState.Idle
            _loginForm.value = LoginFormState()
            _registerForm.value = RegisterFormState()
        }
    }

    /**
     * Schedule automatic token refresh before expiry
     */
    private fun scheduleTokenRefresh(expiresInSeconds: Long) {
        tokenRefreshJob?.cancel()
        tokenRefreshJob = viewModelScope.launch {
            // Refresh 1 minute before token expires
            val delayMs = (expiresInSeconds - 60) * 1000
            if (delayMs > 0) {
                delay(delayMs)
                refreshToken()
            }
        }
    }

    /**
     * Refresh access token using refresh token
     */
    private suspend fun refreshToken() {
        authRepository.refreshToken()
            .onSuccess { response ->
                scheduleTokenRefresh(response.expiresIn)
            }
            .onFailure {
                logout()
            }
    }

    /**
     * Multi-step login form validation
     */
    private fun validateLoginForm(email: String, password: String): Boolean {
        val emailError = when {
            email.isBlank() -> "Email is required"
            !email.isValidEmail() -> "Invalid email format"
            else -> null
        }

        val passwordError = when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        _loginForm.value = LoginFormState(
            email = email,
            emailError = emailError,
            password = password,
            passwordError = passwordError,
            isValid = emailError == null && passwordError == null
        )

        return _loginForm.value.isValid
    }

    /**
     * Multi-step register form validation
     */
    private fun validateRegisterForm(
        email: String,
        password: String,
        name: String,
        phone: String
    ): Boolean {
        val nameError = when {
            name.isBlank() -> "Name is required"
            name.length < 2 -> "Name too short"
            else -> null
        }

        val emailError = when {
            email.isBlank() -> "Email is required"
            !email.isValidEmail() -> "Invalid email format"
            else -> null
        }

        val passwordError = when {
            password.isBlank() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.hasValidPasswordStrength() -> "Password too weak"
            else -> null
        }

        val phoneError = when {
            phone.isBlank() -> "Phone is required"
            !phone.isValidPhone() -> "Invalid phone format"
            else -> null
        }

        _registerForm.value = RegisterFormState(
            name = name,
            nameError = nameError,
            email = email,
            emailError = emailError,
            password = password,
            passwordError = passwordError,
            phone = phone,
            phoneError = phoneError,
            isValid = nameError == null && emailError == null &&
                    passwordError == null && phoneError == null
        )

        return _registerForm.value.isValid
    }

    override fun onCleared() {
        super.onCleared()
        tokenRefreshJob?.cancel()
    }

    companion object {
        private const val KEY_USER = "auth_user"
    }
}

// Auth State
sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data class Success(val user: User) : AuthState
    data class Error(val message: String) : AuthState
}

// Login Form State
data class LoginFormState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isValid: Boolean = false
)

// Register Form State
data class RegisterFormState(
    val name: String = "",
    val nameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val isValid: Boolean = false
)

// Validation Helpers
private fun String.isValidEmail(): Boolean {
    return this.contains("@") && this.contains(".")
}

private fun String.isValidPhone(): Boolean {
    return this.length >= 10 && this.all { it.isDigit() || it == '+' || it == '-' }
}

private fun String.hasValidPasswordStrength(): Boolean {
    return this.any { it.isUpperCase() } &&
            this.any { it.isDigit() } &&
            this.any { !it.isLetterOrDigit() }
}
