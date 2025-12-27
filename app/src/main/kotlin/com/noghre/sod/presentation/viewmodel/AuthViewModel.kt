package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.error.GlobalExceptionHandler
import com.noghre.sod.core.util.onError
import com.noghre.sod.core.util.onSuccess
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.repository.AuthRepository
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val loginState: StateFlow<UiState<User>> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val registerState: StateFlow<UiState<User>> = _registerState.asStateFlow()
    
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    fun login(username: String, password: String) {
        // Validation
        if (username.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowToast("لطفاً تمام فیلدها را پر کنید"))
            }
            return
        }
        
        viewModelScope.launch(exceptionHandler.handler) {
            _loginState.value = UiState.Loading
            Timber.d("Attempting login for user: $username")
            
            authRepository.login(username, password)
                .onSuccess { user ->
                    Timber.d("Login successful for user: ${user.email}")
                    _loginState.value = UiState.Success(user)
                    _events.send(UiEvent.ShowToast("خوش آمدید ${user.name}"))
                    _events.send(UiEvent.Navigate("home"))
                }
                .onError { error ->
                    Timber.e("Login failed: ${error.message}")
                    _loginState.value = UiState.Error(error)
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun register(name: String, email: String, password: String, confirmPassword: String) {
        // Validation
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowToast("لطفاً تمام فیلدها را پر کنید"))
            }
            return
        }
        
        if (password.length < 6) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowToast("رمز عبور باید حداقل ۶ کاراکتر باشد"))
            }
            return
        }
        
        if (password != confirmPassword) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowToast("رمز عبور و تأیید آن یکسان نیست"))
            }
            return
        }
        
        viewModelScope.launch(exceptionHandler.handler) {
            _registerState.value = UiState.Loading
            Timber.d("Attempting registration for email: $email")
            
            authRepository.register(name, email, password)
                .onSuccess { user ->
                    Timber.d("Registration successful for user: ${user.email}")
                    _registerState.value = UiState.Success(user)
                    _events.send(UiEvent.ShowToast("ثبت‌نام با موفقیت انجام شد"))
                    _events.send(UiEvent.Navigate("home"))
                }
                .onError { error ->
                    Timber.e("Registration failed: ${error.message}")
                    _registerState.value = UiState.Error(error)
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun logout() {
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Logging out")
            
            authRepository.logout()
                .onSuccess {
                    Timber.d("Logout successful")
                    _events.send(UiEvent.ShowToast("با موفقیت خارج شدید"))
                    _events.send(UiEvent.Navigate("login"))
                }
                .onError { error ->
                    Timber.e("Logout failed: ${error.message}")
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
}
