package com.noghre.sod.data.repository.user

import com.noghre.sod.core.result.Result
import com.noghre.sod.data.database.dao.UserDao
import com.noghre.sod.data.error.ExceptionHandler
import com.noghre.sod.data.mapper.UserMapper.toDomain
import com.noghre.sod.data.mapper.UserMapper.toEntity
import com.noghre.sod.data.network.NoghreSodApi
import com.noghre.sod.data.repository.networkBoundResource
import com.noghre.sod.domain.model.Address
import com.noghre.sod.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

/**
 * User Repository Implementation.
 *
 * Manages user profile data:
 * - Caches user information locally
 * - Manages addresses
 * - Syncs with network
 *
 * @param api Retrofit API client
 * @param userDao Database access object for users
 * @param ioDispatcher Dispatcher for I/O operations
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
class UserRepositoryImpl @Inject constructor(
    private val api: NoghreSodApi,
    private val userDao: UserDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IUserRepository {

    override fun getUserProfile(): Flow<Result<User>> = networkBoundResource(
        query = {
            userDao.getCurrentUser()?.toDomain()
                ?: throw Exception("User not found")
        },
        fetch = {
            api.getUserProfile()
        },
        saveFetchResult = { response ->
            val user = response.toDomain()
            userDao.insertUser(user.toEntity())
            Timber.d("User profile cached: ${user.id}")
        },
        shouldFetch = { _ -> true },
        onFetchFailed = { exception ->
            Timber.e(exception, "Failed to fetch user profile")
        }
    ).flowOn(ioDispatcher)

    override suspend fun updateUserProfile(
        firstName: String,
        lastName: String
    ): Result<User> = try {
        val requestBody = mapOf(
            "firstName" to firstName,
            "lastName" to lastName
        )

        val response = api.updateUserProfile(requestBody)

        if (response.isSuccessful && response.body()?.success == true) {
            val userDto = response.body()?.data
            if (userDto != null) {
                val user = userDto.toDomain()
                userDao.insertUser(user.toEntity())
                Timber.d("User profile updated")
                Result.success(user)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            Result.failure(Exception("Failed to update profile: ${response.code()}"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "updateUserProfile")
        Timber.e(e, "Error updating user profile")
        Result.failure(e)
    }

    override suspend fun addAddress(address: Address): Result<User> = try {
        val requestBody = mapOf(
            "title" to address.title,
            "fullAddress" to address.fullAddress,
            "province" to address.province,
            "city" to address.city,
            "postalCode" to address.postalCode
        )

        val response = api.addAddress(requestBody)

        if (response.isSuccessful && response.body()?.success == true) {
            val userDto = response.body()?.data
            if (userDto != null) {
                val user = userDto.toDomain()
                userDao.insertUser(user.toEntity())
                Timber.d("Address added: ${address.title}")
                Result.success(user)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            Result.failure(Exception("Failed to add address"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "addAddress")
        Timber.e(e, "Error adding address")
        Result.failure(e)
    }

    override suspend fun updateAddress(address: Address): Result<User> = try {
        val requestBody = mapOf(
            "title" to address.title,
            "fullAddress" to address.fullAddress,
            "province" to address.province,
            "city" to address.city,
            "postalCode" to address.postalCode
        )

        val response = api.updateAddress(address.id, requestBody)

        if (response.isSuccessful && response.body()?.success == true) {
            val userDto = response.body()?.data
            if (userDto != null) {
                val user = userDto.toDomain()
                userDao.insertUser(user.toEntity())
                Timber.d("Address updated: ${address.id}")
                Result.success(user)
            } else {
                Result.failure(Exception("Empty response body"))
            }
        } else {
            Result.failure(Exception("Failed to update address"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "updateAddress")
        Timber.e(e, "Error updating address")
        Result.failure(e)
    }

    override suspend fun deleteAddress(addressId: String): Result<Unit> = try {
        val response = api.deleteAddress(addressId)

        if (response.isSuccessful && response.body()?.success == true) {
            // Refresh user profile to remove address
            val profileResponse = api.getUserProfile()
            if (profileResponse.isSuccessful && profileResponse.body()?.data != null) {
                val user = profileResponse.body()?.data?.toDomain()
                if (user != null) {
                    userDao.insertUser(user.toEntity())
                }
            }
            Timber.d("Address deleted: $addressId")
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to delete address"))
        }
    } catch (e: Exception) {
        ExceptionHandler.handle(e, "deleteAddress")
        Timber.e(e, "Error deleting address")
        Result.failure(e)
    }
}
