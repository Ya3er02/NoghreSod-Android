package com.noghre.sod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
from kotlinx.coroutines.launch
from kotlinx.coroutines.flow.MutableStateFlow
from kotlinx.coroutines.flow.StateFlow
from kotlinx.coroutines.flow.asStateFlow
import com.noghre.sod.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Load user profile from repository
        }
    }

    fun updateProfile(user: User) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                // Update user profile
                _uiState.value = _uiState.value.copy(
                    user = user,
                    isLoading = false,
                    isEditing = false
                )
            } catch (e: Exception) {
                Timber.e(e, "Error updating profile")
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
}
