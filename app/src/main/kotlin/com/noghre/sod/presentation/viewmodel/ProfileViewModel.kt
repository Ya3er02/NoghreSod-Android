package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.noghre.sod.domain.model.User
import com.noghre.sod.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ProfileViewModel - مدیریت پروفایل کاربر
 * Features:
 * - User Profile Display
 * - Profile Editing
 * - Profile Picture Upload
 * - Preference Management
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // Profile State
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    // Edit Mode State
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    // Form State for editing
    private val _editForm = MutableStateFlow(EditFormState())
    val editForm: StateFlow<EditFormState> = _editForm.asStateFlow()

    init {
        loadProfile()
    }

    /**
     * Load user profile
     */
    private fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading

            userRepository.getProfile()
                .onSuccess { user ->
                    _profileState.value = ProfileState.Success(user)
                    _editForm.value = EditFormState(
                        name = user.name,
                        email = user.email,
                        phone = user.phone,
                        bio = user.bio
                    )
                }
                .onFailure { error ->
                    _profileState.value = ProfileState.Error(
                        message = error.message ?: "Failed to load profile"
                    )
                }
        }
    }

    /**
     * Enable edit mode
     */
    fun enableEditMode() {
        _isEditMode.value = true
    }

    /**
     * Cancel editing
     */
    fun cancelEdit() {
        _isEditMode.value = false
        loadProfile()
    }

    /**
     * Update form field
     */
    fun updateFormField(field: String, value: String) {
        _editForm.update { currentForm ->
            when (field) {
                "name" -> currentForm.copy(
                    name = value,
                    nameError = validateName(value)
                )
                "email" -> currentForm.copy(
                    email = value,
                    emailError = validateEmail(value)
                )
                "phone" -> currentForm.copy(
                    phone = value,
                    phoneError = validatePhone(value)
                )
                "bio" -> currentForm.copy(bio = value)
                else -> currentForm
            }
        }
    }

    /**
     * Save profile changes
     */
    fun saveProfile() {
        val form = _editForm.value

        // Validate all fields
        val nameError = validateName(form.name)
        val emailError = validateEmail(form.email)
        val phoneError = validatePhone(form.phone)

        if (nameError != null || emailError != null || phoneError != null) {
            _editForm.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    phoneError = phoneError
                )
            }
            return
        }

        viewModelScope.launch {
            _profileState.value = ProfileState.Updating

            val currentUser = (_profileState.value as? ProfileState.Success)?.user
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    name = form.name,
                    email = form.email,
                    phone = form.phone,
                    bio = form.bio
                )

                userRepository.updateProfile(updatedUser)
                    .onSuccess { user ->
                        _profileState.value = ProfileState.Success(user)
                        _isEditMode.value = false
                    }
                    .onFailure { error ->
                        _profileState.value = ProfileState.Error(
                            message = error.message ?: "Failed to update profile"
                        )
                    }
            }
        }
    }

    /**
     * Upload profile picture
     */
    fun uploadProfilePicture(uri: Uri) {
        viewModelScope.launch {
            _profileState.value = ProfileState.UploadingImage

            userRepository.uploadProfilePicture(uri)
                .onSuccess { imageUrl ->
                    val currentUser = (_profileState.value as? ProfileState.Success)?.user
                    if (currentUser != null) {
                        updateProfile(currentUser.copy(profilePictureUrl = imageUrl))
                    }
                }
                .onFailure { error ->
                    _profileState.value = ProfileState.Error(
                        message = "Image upload failed: ${error.message}"
                    )
                }
        }
    }

    /**
     * Update profile with new user data
     */
    private fun updateProfile(user: User) {
        viewModelScope.launch {
            userRepository.updateProfile(user)
                .onSuccess {
                    _profileState.value = ProfileState.Success(user)
                    loadProfile()
                }
                .onFailure { error ->
                    _profileState.value = ProfileState.Error(
                        message = error.message ?: "Update failed"
                    )
                }
        }
    }

    // Validation Helpers
    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            name.length < 2 -> "Name too short"
            name.length > 50 -> "Name too long"
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !email.contains("@") -> "Invalid email format"
            else -> null
        }
    }

    private fun validatePhone(phone: String): String? {
        return when {
            phone.isBlank() -> "Phone is required"
            phone.length < 10 -> "Phone too short"
            else -> null
        }
    }
}

// Profile State
sealed interface ProfileState {
    data object Loading : ProfileState
    data class Success(val user: User) : ProfileState
    data object Updating : ProfileState
    data object UploadingImage : ProfileState
    data class Error(val message: String) : ProfileState
}

// Edit Form State
data class EditFormState(
    val name: String = "",
    val nameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val bio: String = ""
)
