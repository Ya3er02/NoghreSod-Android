package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.usecase.LoginUseCase
import com.noghre.sod.domain.usecase.RegisterUseCase
import com.noghre.sod.domain.usecase.VerifyOtpUseCase
import com.noghre.sod.domain.usecase.LogoutUseCase
import com.noghre.sod.presentation.auth.AuthUiState
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.common.UiConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for authentication screens (login, register, OTP).
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

    /**
     * Handle phone input
     *
     * @param phone Phone number
     */
    fun onPhoneChanged(phone: String) {
        val cleanedPhone = phone.filter { it.isDigit() }.take(11)
        val error = validatePhone(cleanedPhone)
        updateState { copy(phone = cleanedPhone, phoneError = error) }
    }

    /**
     * Handle password input
     *
     * @param password Password
     */
    fun onPasswordChanged(password: String) {
        val error = validatePassword(password)
        updateState { copy(password = password, passwordError = error) }
    }

    /**
     * Handle full name input
     *
     * @param name Full name
     */
    fun onFullNameChanged(name: String) {
        val error = if (name.length < 3) "Name must be at least 3 characters" else null
        updateState { copy(fullName = name, fullNameError = error) }
    }

    /**
     * Handle OTP input
     *
     * @param code OTP code
     */
    fun onOtpChanged(code: String) {
        val cleanedCode = code.filter { it.isDigit() }.take(UiConstants.OTP_LENGTH)
        updateState { copy(otpCode = cleanedCode) }
    }

    /**
     * Login with phone and password
     */
    fun login() {
        val state = uiState.value
        if (!isLoginValid()) return

        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                loginUseCase(state.phone, state.password)
                updateState { copy(isLoading = false) }
                _events.send(UiEvent.ShowSnackbar("Login successful"))
                _events.send(UiEvent.Navigate("home"))
            } catch (e: Exception) {
                Timber.e(e, "Login failed")
                val errorMsg = e.message ?: "Login failed"
                updateState { copy(isLoading = false, error = errorMsg) }
                _events.send(UiEvent.ShowSnackbar(errorMsg))
            }
        }
    }

    /**
     * Register new account
     */
    fun register() {
        val state = uiState.value
        if (!isRegistrationValid()) return

        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                registerUseCase(state.phone, state.fullName, state.password)
                updateState { copy(isLoading = false, isOtpSent = true) }
                _events.send(UiEvent.ShowSnackbar("Registration successful. Verify OTP"))
                startOtpTimer()
            } catch (e: Exception) {
                Timber.e(e, "Registration failed")
                val errorMsg = e.message ?: "Registration failed"
                updateState { copy(isLoading = false, error = errorMsg) }
                _events.send(UiEvent.ShowSnackbar(errorMsg))
            }
        }
    }

    /**
     * Send OTP to phone
     */
    fun sendOtp() {
        val state = uiState.value
        if (state.phone.length != 11) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowSnackbar("Invalid phone number"))
            }
            return
        }

        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                // Call send OTP use case
                updateState { copy(isLoading = false, isOtpSent = true) }
                _events.send(UiEvent.ShowSnackbar("OTP sent"))
                startOtpTimer()
            } catch (e: Exception) {
                Timber.e(e, "Send OTP failed")
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Verify OTP
     */
    fun verifyOtp() {
        val state = uiState.value
        if (state.otpCode.length != UiConstants.OTP_LENGTH) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowSnackbar("Invalid OTP length"))
            }
            return
        }

        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                verifyOtpUseCase(state.phone, state.otpCode)
                updateState { copy(isLoading = false) }
                _events.send(UiEvent.ShowSnackbar("OTP verified"))
                _events.send(UiEvent.Navigate("home"))
            } catch (e: Exception) {
                Timber.e(e, "OTP verification failed")
                updateState { copy(isLoading = false, error = e.message) }
                _events.send(UiEvent.ShowSnackbar("OTP verification failed"))
            }
        }
    }

    /**
     * Logout
     */
    fun logout() {
        viewModelScope.launch {
            try {
                logoutUseCase()
                resetState()
                _events.send(UiEvent.Navigate("login"))
            } catch (e: Exception) {
                Timber.e(e, "Logout failed")
            }
        }
    }

    /**
     * Start OTP countdown timer
     */
    private fun startOtpTimer() {
        viewModelScope.launch {
            repeat(UiConstants.OTP_TIMEOUT_SECONDS) {
                updateState { copy(otpCountdown = UiConstants.OTP_TIMEOUT_SECONDS - it) }
                delay(1000)
            }
        }
    }

    /**
     * Validate phone (Iran format: 09XXXXXXXXX)
     */
    private fun validatePhone(phone: String): String? {
        return when {
            phone.isEmpty() -> "Phone is required"
            phone.length < 11 -> "Phone must be 11 digits"
            !phone.startsWith("09") -> "Phone must start with 09"
            else -> null
        }
    }

    /**
     * Validate password
     */
    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }

    /**
     * Check if login inputs are valid
     */
    private fun isLoginValid(): Boolean {
        val state = uiState.value
        return validatePhone(state.phone) == null && validatePassword(state.password) == null
    }

    /**
     * Check if registration inputs are valid
     */
    private fun isRegistrationValid(): Boolean {
        val state = uiState.value
        return validatePhone(state.phone) == null &&
                validatePassword(state.password) == null &&
                state.fullName.length >= 3
    }

    /**
     * Reset state
     */
    private fun resetState() {
        _uiState.value = AuthUiState()
    }

    private fun updateState(block: AuthUiState.() -> AuthUiState) {
        _uiState.update(block)
    }
}
