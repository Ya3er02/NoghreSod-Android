package com.noghre.sod.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.repository.UserRepository
import com.noghre.sod.domain.repository.OrderRepository
import com.noghre.sod.domain.repository.WishlistRepository
import com.noghre.sod.domain.repository.AuthRepository
import com.noghre.sod.presentation.screens.profile.ProfileUiState
import com.noghre.sod.presentation.screens.profile.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Profile Screen.
 * 
 * Handles user profile management, order history,
 * wishlist, and logout functionality.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val wishlistRepository: WishlistRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _profileUiState = MutableStateFlow(ProfileUiState())
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadProfileData()
    }

    /**
     * Load all profile data.
     */
    private fun loadProfileData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Timber.d("Loading profile data")

                // Load user profile
                val user = userRepository.getUserProfile()

                // Load orders
                val orders = orderRepository.getOrders()
                val totalOrders = orders.size
                val totalSpent = orders.sumOf { it.totalAmount.toDoubleOrNull() ?: 0.0 }

                // Load wishlist count
                val wishlist = wishlistRepository.getWishlist()
                val wishlistCount = wishlist.size

                _profileUiState.value = ProfileUiState(
                    user = User(
                        id = user.id,
                        name = user.name,
                        email = user.email,
                        mobile = user.mobile
                    ),
                    totalOrders = totalOrders,
                    totalSpent = totalSpent,
                    wishlistCount = wishlistCount
                )

                Timber.d("Profile data loaded successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error loading profile data")
                _error.value = e.localizedMessage ?: "خطای نامشخص رخ داد"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update user profile.
     */
    fun updateProfile(
        name: String,
        email: String,
        mobile: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Timber.d("Updating profile")

                userRepository.updateProfile(
                    name = name,
                    email = email,
                    mobile = mobile
                )

                // Reload profile
                loadProfileData()

                Timber.d("Profile updated successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error updating profile")
                _error.value = e.localizedMessage ?: "خطا در به روز رسانی"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Logout user.
     */
    fun logout() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Timber.d("Logging out user")

                authRepository.logout()

                Timber.d("User logged out successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error logging out")
                _error.value = e.localizedMessage ?: "خطا در خروج"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _error.value = null
    }
}
