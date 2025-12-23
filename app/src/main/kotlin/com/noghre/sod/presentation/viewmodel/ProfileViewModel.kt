package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Address
import com.noghre.sod.domain.usecase.GetProfileUseCase
import com.noghre.sod.domain.usecase.UpdateProfileUseCase
import com.noghre.sod.domain.usecase.GetOrdersUseCase
import com.noghre.sod.domain.usecase.GetAddressesUseCase
import com.noghre.sod.domain.usecase.LogoutUseCase
import com.noghre.sod.presentation.profile.ProfileUiState
import com.noghre.sod.presentation.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for user profile screen.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getOrdersUseCase: GetOrdersUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadProfile()
        loadOrders()
        loadAddresses()
    }

    /**
     * Load user profile
     */
    fun loadProfile() {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                val user = getProfileUseCase()
                updateState { copy(user = user, isLoading = false) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading profile")
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Load user orders
     */
    fun loadOrders() {
        viewModelScope.launch {
            try {
                val orders = getOrdersUseCase()
                updateState { copy(orders = orders) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading orders")
            }
        }
    }

    /**
     * Load user addresses
     */
    fun loadAddresses() {
        viewModelScope.launch {
            try {
                val addresses = getAddressesUseCase()
                updateState { copy(addresses = addresses) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading addresses")
            }
        }
    }

    /**
     * Update user profile
     *
     * @param fullName Full name
     * @param email Email address
     */
    fun updateProfile(fullName: String, email: String? = null) {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                val updatedUser = updateProfileUseCase(fullName, email)
                updateState { copy(user = updatedUser, isLoading = false) }
                _events.send(UiEvent.ShowSnackbar("Profile updated"))
            } catch (e: Exception) {
                Timber.e(e, "Error updating profile")
                updateState { copy(isLoading = false, error = e.message) }
                _events.send(UiEvent.ShowSnackbar("Failed to update profile"))
            }
        }
    }

    /**
     * Logout user
     */
    fun logout() {
        viewModelScope.launch {
            try {
                updateState { copy(isLogoutInProgress = true) }
                logoutUseCase()
                _events.send(UiEvent.Navigate("login"))
            } catch (e: Exception) {
                Timber.e(e, "Error logging out")
                updateState { copy(isLogoutInProgress = false) }
                _events.send(UiEvent.ShowSnackbar("Logout failed"))
            }
        }
    }

    /**
     * Retry loading
     */
    fun retry() {
        loadProfile()
    }

    private fun updateState(block: ProfileUiState.() -> ProfileUiState) {
        _uiState.update(block)
    }
}
