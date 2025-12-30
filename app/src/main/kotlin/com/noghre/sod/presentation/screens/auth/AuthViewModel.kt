package com.noghre.sod.presentation.screens.auth

import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.model.Result
import com.noghre.sod.domain.repository.AuthRepository
import com.noghre.sod.presentation.base.BaseViewModel
import com.noghre.sod.presentation.base.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

/**
 * State for Auth screens.
 */
data class AuthUiState(
    val currentUser: User? = null,
    val isLoggedIn: Boolean = false,
    val mobile: String = "",
    val password: String = "",
    val email: String = "",
    val name: String = "",
    val mobileError: String? = null,
    val passwordError: String? = null,
    val emailError: String? = null,
    val nameError: String? = null
)

/**
 * ViewModel for Authentication screens.
 * 
* Manages login, registration, and user session.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<AuthUiState>() {

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState = _authUiState.asStateFlow()

    init {
        checkAuthStatus()
    }

    /**
     * Check if user is already logged in.
     */
    fun checkAuthStatus() {
        launchAsync(
            block = {
                val isAuth = authRepository.isAuthenticated()
                if (isAuth) {
                    when (val result = authRepository.getCurrentUser()) {
                        is Result.Success -> {
                            _authUiState.value = _authUiState.value.copy(
                                currentUser = result.data,
                                isLoggedIn = true
                            )
                        }
                        is Result.Error -> {
                            Timber.e(result.exception, "Error fetching user")
                        }
                    }
                }
            }
        )
    }

    /**
     * Login with mobile and password.
     */
    fun login(mobile: String, password: String) {
        // Validate
        if (!validateLogin(mobile, password)) return
        
        launchAsync(
            block = {
                when (val result = authRepository.login(mobile, password)) {
                    is Result.Success -> {
                        _authUiState.value = _authUiState.value.copy(
                            currentUser = result.data,
                            isLoggedIn = true,
                            mobile = "",
                            password = ""
                        )
                        emitEvent(UiEvent.ShowToast("به عنوان موفقانه وارد شد"))
                        emitEvent(UiEvent.Navigate("home"))
                    }
                    is Result.Error -> {
                        setError(result.message)
                        emitEvent(UiEvent.ShowToast(result.message))
                    }
                }
            }
        )
    }

    /**
     * Register new user.
     */
    fun register(name: String, mobile: String, email: String, password: String) {
        // Validate
        if (!validateRegister(name, mobile, email, password)) return
        
        launchAsync(
            block = {
                when (val result = authRepository.register(name, mobile, email, password)) {
                    is Result.Success -> {
                        _authUiState.value = _authUiState.value.copy(
                            currentUser = result.data,
                            isLoggedIn = true,
                            mobile = "",
                            password = "",
                            email = "",
                            name = ""
                        )
                        emitEvent(UiEvent.ShowToast("ثبت نام موفق بود"))
                        emitEvent(UiEvent.Navigate("home"))
                    }
                    is Result.Error -> {
                        setError(result.message)
                    }
                }
            }
        )
    }

    /**
     * Logout user.
     */
    fun logout() {
        launchAsync(
            block = {
                when (authRepository.logout()) {
                    is Result.Success -> {
                        _authUiState.value = AuthUiState()
                        emitEvent(UiEvent.ShowToast("خروج موفق"))
                        emitEvent(UiEvent.Navigate("login"))
                    }
                    is Result.Error -> {
                        _authUiState.value = AuthUiState()
                        emitEvent(UiEvent.Navigate("login"))
                    }
                }
            }
        )
    }

    /**
     * Update mobile input.
     */
    fun setMobile(mobile: String) {
        _authUiState.value = _authUiState.value.copy(
            mobile = mobile,
            mobileError = null
        )
    }

    /**
     * Update password input.
     */
    fun setPassword(password: String) {
        _authUiState.value = _authUiState.value.copy(
            password = password,
            passwordError = null
        )
    }

    /**
     * Update email input.
     */
    fun setEmail(email: String) {
        _authUiState.value = _authUiState.value.copy(
            email = email,
            emailError = null
        )
    }

    /**
     * Update name input.
     */
    fun setName(name: String) {
        _authUiState.value = _authUiState.value.copy(
            name = name,
            nameError = null
        )
    }

    /**
     * Request password reset.
     */
    fun requestPasswordReset(mobile: String) {
        launchAsync(
            block = {
                when (val result = authRepository.requestPasswordReset(mobile)) {
                    is Result.Success -> {
                        emitEvent(UiEvent.ShowToast("لینك بازیابی رمز به شما را ارسال شد"))
                    }
                    is Result.Error -> {
                        setError(result.message)
                    }
                }
            }
        )
    }

    /**
     * Validate login form.
     */
    private fun validateLogin(mobile: String, password: String): Boolean {
        var isValid = true
        val state = _authUiState.value.copy()
        
        // Validate mobile
        if (mobile.isEmpty()) {
            state.mobileError = "شماره موبایل معتبر نست"
            isValid = false
        } else if (!mobile.matches(Regex("^09\\d{9}$"))) {
            state.mobileError = "الگوی شماره موبایل"
            isValid = false
        }
        
        // Validate password
        if (password.isEmpty()) {
            state.passwordError = "رمز و خود را وارد کنید"
            isValid = false
        } else if (password.length < 6) {
            state.passwordError = "رمز باید حداقل 6 کاراکتر باشد"
            isValid = false
        }
        
        if (!isValid) {
            _authUiState.value = state
        }
        
        return isValid
    }

    /**
     * Validate registration form.
     */
    private fun validateRegister(
        name: String,
        mobile: String,
        email: String,
        password: String
    ): Boolean {
        var isValid = true
        val state = _authUiState.value.copy()
        
        // Validate all fields
        if (name.isEmpty()) {
            state.nameError = "ارسال هشستان نام"
            isValid = false
        }
        
        if (mobile.isEmpty() || !mobile.matches(Regex("^09\\d{9}$"))) {
            state.mobileError = "شماره موبایل معتبر نست"
            isValid = false
        }
        
        if (email.isEmpty() || !email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))) {
            state.emailError = "ای میل معتبر نست"
            isValid = false
        }
        
        if (password.isEmpty() || password.length < 6) {
            state.passwordError = "رمز باید حداقل 6 کاراکتر باشد"
            isValid = false
        }
        
        if (!isValid) {
            _authUiState.value = state
        }
        
        return isValid
    }
}
