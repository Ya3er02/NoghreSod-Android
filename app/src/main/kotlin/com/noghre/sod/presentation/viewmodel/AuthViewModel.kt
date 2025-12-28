package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.R
import com.noghre.sod.core.exception.GlobalExceptionHandler
import com.noghre.sod.core.util.UiEvent
import com.noghre.sod.domain.common.ResourceProvider
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.usecase.auth.GetCurrentUserUseCase
import com.noghre.sod.domain.usecase.auth.IsAuthenticatedUseCase
import com.noghre.sod.domain.usecase.auth.LoginUseCase
import com.noghre.sod.domain.usecase.auth.LogoutUseCase
import com.noghre.sod.domain.usecase.auth.RegisterUseCase
import com.noghre.sod.domain.usecase.validation.ValidateEmailUseCase
import com.noghre.sod.domain.usecase.validation.ValidatePasswordConfirmationUseCase
import com.noghre.sod.domain.usecase.validation.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

/**
 * ✅ REFACTORED: Auth ViewModel with proper Clean Architecture
 * 
 * Key changes from old version:
 * 1. ❌ NO direct AuthRepository injection
 * 2. ✅ ALL UseCases injected (auth + validation)
 * 3. ❌ NO validation logic in ViewModel
 * 4. ✅ ALL validation delegated to UseCases
 * 5. ✅ Proper error handling
 * 6. ✅ Resource strings from ResourceProvider
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    // ✅ Auth UseCases
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isAuthenticatedUseCase: IsAuthenticatedUseCase,
    
    // ✅ Validation UseCases
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validatePasswordConfirmationUseCase: ValidatePasswordConfirmationUseCase,
    
    // Infrastructure
    private val resourceProvider: ResourceProvider,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    // ==================== UI State ====================
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    
    // ==================== Initialization ====================
    
    init {
        checkAuthentication()
    }
    
    // ==================== Public Methods ====================
    
    fun login(email: String, password: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = AuthUiState.Loading
            
            try {
                // ✅ Validate email
                validateEmailUseCase(email)
                // ✅ Validate password
                validatePasswordUseCase(password)
                
                // ✅ If validation passes, proceed with login
                val params = LoginUseCase.Params(
                    email = email,
                    password = password
                )
                val result = loginUseCase(params)
                
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    _currentUser.value = user
                    _isAuthenticated.value = true
                    _uiState.value = AuthUiState.Success(user!!)
                    
                    _uiEvent.emit(
                        UiEvent.ShowToast(
                            resourceProvider.getString(
                                R.string.welcome_user,
                                user.name
                            )
                        )
                    )
                    Timber.d("User logged in: ${user.email}")
                } else {
                    val error = result.exceptionOrNull()
                    _uiState.value = AuthUiState.Error(error?.message ?: "Login failed")
                    Timber.e(error, "Login failed")
                }
            } catch (e: Exception) {
                // Validation exception caught here
                _uiState.value = AuthUiState.Error(e.message ?: "Validation error")
                Timber.e(e, "Validation error during login")
            }
        }
    }
    
    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = AuthUiState.Loading
            
            try {
                // ✅ Validate all fields (ALL validation in domain layer)
                validateEmailUseCase(email)
                validatePasswordUseCase(password)
                validatePasswordConfirmationUseCase(
                    ValidatePasswordConfirmationUseCase.Params(
                        password = password,
                        confirmPassword = confirmPassword
                    )
                )
                
                // ✅ If validation passes, proceed with registration
                val params = RegisterUseCase.Params(
                    name = name,
                    email = email,
                    password = password
                )
                val result = registerUseCase(params)
                
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    _currentUser.value = user
                    _isAuthenticated.value = true
                    _uiState.value = AuthUiState.Success(user!!)
                    
                    _uiEvent.emit(
                        UiEvent.ShowToast(
                            resourceProvider.getString(R.string.success_registration)
                        )
                    )
                    Timber.d("User registered: ${user.email}")
                } else {
                    val error = result.exceptionOrNull()
                    _uiState.value = AuthUiState.Error(error?.message ?: "Registration failed")
                    Timber.e(error, "Registration failed")
                }
            } catch (e: Exception) {
                // Validation exception caught here
                _uiState.value = AuthUiState.Error(e.message ?: "Validation error")
                Timber.e(e, "Validation error during registration")
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch(exceptionHandler.handler) {
            val result = logoutUseCase(Unit)
            
            if (result.isSuccess) {
                _currentUser.value = null
                _isAuthenticated.value = false
                _uiState.value = AuthUiState.Idle
                
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        resourceProvider.getString(R.string.success_logout)
                    )
                )
                Timber.d("User logged out")
            } else {
                val error = result.exceptionOrNull()
                _uiEvent.emit(UiEvent.ShowError(error?.message ?: "Logout failed"))
            }
        }
    }
    
    fun checkAuthentication() {
        viewModelScope.launch {
            val isAuth = isAuthenticatedUseCase(Unit)
            
            if (isAuth.isSuccess && isAuth.getOrNull() == true) {
                _isAuthenticated.value = true
                
                // Get current user if authenticated
                val userResult = getCurrentUserUseCase(Unit)
                if (userResult.isSuccess) {
                    _currentUser.value = userResult.getOrNull()
                }
            } else {
                _isAuthenticated.value = false
            }
        }
    }
}
