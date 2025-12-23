package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.usecase.profile.UpdateProfileUseCase
import com.noghre.sod.domain.usecase.profile.ChangePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateProfile(fullName: String, email: String?, avatarUrl: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val updatedUser = updateProfileUseCase(
                    UpdateProfileUseCase.Params(fullName, email, avatarUrl)
                )
                _uiState.value = _uiState.value.copy(
                    user = updatedUser,
                    isLoading = false,
                    successMessage = "پروفایل با موفقیت به روز‌رسانی شد"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                changePasswordUseCase(
                    ChangePasswordUseCase.Params(currentPassword, newPassword)
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "رمز عبور با موفقیت تغییر کرد"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
}
