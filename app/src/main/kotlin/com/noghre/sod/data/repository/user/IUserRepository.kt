package com.noghre.sod.data.repository.user

import com.noghre.sod.core.result.Result
import com.noghre.sod.domain.model.Address
import com.noghre.sod.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user operations.
 *
 * Defines contract for:
 * - Getting user profile
 * - Updating profile information
 * - Managing addresses
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
interface IUserRepository {

    /**
     * Get current user's profile.
     *
     * @return Flow emitting Result with User profile
     */
    fun getUserProfile(): Flow<Result<User>>

    /**
     * Update user's profile information.
     *
     * @param firstName New first name
     * @param lastName New last name
     * @return Result with updated User
     */
    suspend fun updateUserProfile(
        firstName: String,
        lastName: String
    ): Result<User>

    /**
     * Add new address to user's profile.
     *
     * @param address Address to add
     * @return Result with updated User
     */
    suspend fun addAddress(address: Address): Result<User>

    /**
     * Update existing address.
     *
     * @param address Updated address
     * @return Result with updated User
     */
    suspend fun updateAddress(address: Address): Result<User>

    /**
     * Delete address from user's profile.
     *
     * @param addressId ID of address to delete
     * @return Result<Unit> indicating success or failure
     */
    suspend fun deleteAddress(addressId: String): Result<Unit>
}
