package com.noghre.sod.data.datasource.local

import com.noghre.sod.data.local.dao.UserDao
import com.noghre.sod.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalUserDataSource @Inject constructor(
    private val userDao: UserDao,
) {

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    fun getUser(userId: String): Flow<UserEntity?> {
        return userDao.getUser(userId)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(userId: String) {
        userDao.deleteUser(userId)
    }

    suspend fun insertAddress(address: AddressEntity) {
        userDao.insertAddress(address)
    }

    suspend fun insertAddresses(addresses: List<AddressEntity>) {
        userDao.insertAddresses(addresses)
    }

    fun getAddresses(): Flow<List<AddressEntity>> {
        return userDao.getAddresses()
    }

    fun getAddress(addressId: String): Flow<AddressEntity?> {
        return userDao.getAddress(addressId)
    }

    fun getDefaultAddress(): Flow<AddressEntity?> {
        return userDao.getDefaultAddress()
    }

    suspend fun updateAddress(address: AddressEntity) {
        userDao.updateAddress(address)
    }

    suspend fun deleteAddress(address: AddressEntity) {
        userDao.deleteAddress(address)
    }

    suspend fun insertPreferences(preferences: UserPreferencesEntity) {
        userDao.insertPreferences(preferences)
    }

    fun getPreferences(userId: String): Flow<UserPreferencesEntity?> {
        return userDao.getPreferences(userId)
    }

    suspend fun updatePreferences(preferences: UserPreferencesEntity) {
        userDao.updatePreferences(preferences)
    }

    suspend fun insertToken(token: AuthTokenEntity) {
        userDao.insertToken(token)
    }

    fun getToken(): Flow<AuthTokenEntity?> {
        return userDao.getToken()
    }

    suspend fun updateToken(token: AuthTokenEntity) {
        userDao.updateToken(token)
    }

    suspend fun deleteToken() {
        userDao.deleteToken()
    }
}
