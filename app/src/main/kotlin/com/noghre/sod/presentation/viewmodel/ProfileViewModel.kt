package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.error.GlobalExceptionHandler
import com.noghre.sod.core.util.onError
import com.noghre.sod.core.util.onSuccess
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.repository.UserRepository
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
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val uiState: StateFlow<UiState<User>> = _uiState.asStateFlow()
    
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    init {
        loadProfile()
    }
    
    fun loadProfile() {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = UiState.Loading
            Timber.d("Loading user profile")
            
            userRepository.getCurrentUser()
                .onSuccess { user ->
                    Timber.d("Profile loaded: ${user.email}")
                    _uiState.value = UiState.Success(user)
                }
                .onError { error ->
                    Timber.e("Failed to load profile: ${error.message}")
                    _uiState.value = UiState.Error(error)
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onEditProfile(name: String, phone: String) {
        if (name.isBlank() || phone.isBlank()) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowToast("لطفاً تمام فیلدها را پر کنید"))
            }
            return
        }
        
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Updating profile")
            
            userRepository.updateProfile(name = name, phone = phone)
                .onSuccess {
                    Timber.d("Profile updated successfully")
                    _events.send(UiEvent.ShowToast("پروفایل به روز رسانی شد"))
                    loadProfile()
                }
                .onError { error ->
                    Timber.e("Failed to update profile: ${error.message}")
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onChangePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowToast("لطفاً تمام فیلدها را پر کنید"))
            }
            return
        }
        
        if (newPassword.length < 6) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowToast("رمز عبور باید حداقل ۶ کاراکتر باشد"))
            }
            return
        }
        
        if (newPassword != confirmPassword) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowToast("رمز عبور و تأیید آن یکسان نیست"))
            }
            return
        }
        
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Changing password")
            
            userRepository.changePassword(oldPassword, newPassword)
                .onSuccess {
                    Timber.d("Password changed successfully")
                    _events.send(UiEvent.ShowToast("رمز عبور با موفقیت تغییر یافت"))
                }
                .onError { error ->
                    Timber.e("Failed to change password: ${error.message}")
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onLogout() {
        viewModelScope.launch {
            Timber.d("Logout triggered from profile")
            _events.send(UiEvent.Navigate("login"))
        }
    }
    
    fun onRetryClick() {
        Timber.d("Retry clicked")
        loadProfile()
    }
}
