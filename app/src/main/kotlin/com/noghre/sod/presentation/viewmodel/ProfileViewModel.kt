package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.R
import com.noghre.sod.core.exception.GlobalExceptionHandler
import com.noghre.sod.core.util.UiEvent
import com.noghre.sod.domain.common.ResourceProvider
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.usecase.profile.DeleteAccountUseCase
import com.noghre.sod.domain.usecase.profile.GetUserProfileUseCase
import com.noghre.sod.domain.usecase.profile.UpdateProfileImageUseCase
import com.noghre.sod.domain.usecase.profile.UpdateUserProfileUseCase
import com.noghre.sod.domain.usecase.validation.ValidateEmailUseCase
import com.noghre.sod.domain.usecase.validation.ValidatePhoneNumberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // ✅ Profile UseCases
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    
    // ✅ Validation UseCases
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase,
    
    // Infrastructure
    private val resourceProvider: ResourceProvider,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    
    init {
        loadProfile()
    }
    
    fun loadProfile() {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = ProfileUiState.Loading
            
            val result = getUserProfileUseCase(Unit)
            
            if (result.isSuccess) {
                val user = result.getOrNull()
                _userProfile.value = user
                _uiState.value = ProfileUiState.Success(user!!)
                Timber.d("Profile loaded: ${user.email}")
            } else {
                val error = result.exceptionOrNull()
                _uiState.value = ProfileUiState.Error(error?.message ?: "Failed to load profile")
            }
        }
    }
    
    fun updateProfile(
        name: String? = null,
        email: String? = null,
        phoneNumber: String? = null,
        address: String? = null,
        city: String? = null,
        postalCode: String? = null
    ) {
        viewModelScope.launch(exceptionHandler.handler) {
            _uiState.value = ProfileUiState.Loading
            
            try {
                // ✅ Validate fields if provided
                if (email != null) {
                    validateEmailUseCase(email)
                }
                if (phoneNumber != null) {
                    validatePhoneNumberUseCase(phoneNumber)
                }
                
                // ✅ Update profile
                val params = UpdateUserProfileUseCase.Params(
                    name = name,
                    email = email,
                    phoneNumber = phoneNumber,
                    address = address,
                    city = city,
                    postalCode = postalCode
                )
                val result = updateUserProfileUseCase(params)
                
                if (result.isSuccess) {
                    val updatedUser = result.getOrNull()
                    _userProfile.value = updatedUser
                    _uiState.value = ProfileUiState.Success(updatedUser!!)
                    _uiEvent.emit(UiEvent.ShowToast("Profile updated successfully"))
                    Timber.d("Profile updated")
                } else {
                    val error = result.exceptionOrNull()
                    _uiState.value = ProfileUiState.Error(error?.message ?: "Failed to update")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Validation error")
            }
        }
    }
    
    fun updateProfileImage(imagePath: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            val result = updateProfileImageUseCase(imagePath)
            
            if (result.isSuccess) {
                val updatedUser = result.getOrNull()
                _userProfile.value = updatedUser
                _uiEvent.emit(UiEvent.ShowToast("Photo updated"))
                Timber.d("Profile image updated")
            } else {
                val error = result.exceptionOrNull()
                _uiEvent.emit(UiEvent.ShowError(error?.message ?: "Failed to update photo"))
            }
        }
    }
    
    fun deleteAccount(password: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            val result = deleteAccountUseCase(password)
            
            if (result.isSuccess) {
                _userProfile.value = null
                _uiState.value = ProfileUiState.Idle
                _uiEvent.emit(UiEvent.ShowToast("Account deleted"))
                Timber.d("Account deleted")
            } else {
                val error = result.exceptionOrNull()
                _uiEvent.emit(UiEvent.ShowError(error?.message ?: "Failed to delete account"))
            }
        }
    }
}
