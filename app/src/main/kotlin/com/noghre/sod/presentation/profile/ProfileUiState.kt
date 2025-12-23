package com.noghre.sod.presentation.profile

import com.noghre.sod.domain.model.Address
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.model.User

/**
 * UI state for user profile screen
 */
data class ProfileUiState(
    val user: User? = null,
    val orders: List<Order> = emptyList(),
    val addresses: List<Address> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLogoutInProgress: Boolean = false
)
