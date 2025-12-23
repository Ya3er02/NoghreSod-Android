package com.noghre.sod.data.local.dao

import androidx.room.*
import com.noghre.sod.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // ==================== User Profile ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUser(userId: String): Flow<UserEntity?>

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    // ==================== Addresses ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddresses(addresses: List<AddressEntity>)

    @Query("SELECT * FROM user_addresses ORDER BY CASE WHEN isDefault THEN 0 ELSE 1 END, createdAt DESC")
    fun getAddresses(): Flow<List<AddressEntity>>

    @Query("SELECT * FROM user_addresses WHERE id = :addressId")
    fun getAddress(addressId: String): Flow<AddressEntity?>

    @Query("SELECT * FROM user_addresses WHERE isDefault = 1 LIMIT 1")
    fun getDefaultAddress(): Flow<AddressEntity?>

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Delete
    suspend fun deleteAddress(address: AddressEntity)

    // ==================== User Preferences ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: UserPreferencesEntity)

    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    fun getPreferences(userId: String): Flow<UserPreferencesEntity?>

    @Update
    suspend fun updatePreferences(preferences: UserPreferencesEntity)

    // ==================== Auth Tokens ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: AuthTokenEntity)

    @Query("SELECT * FROM auth_tokens WHERE id = 'current_token'")
    fun getToken(): Flow<AuthTokenEntity?>

    @Update
    suspend fun updateToken(token: AuthTokenEntity)

    @Query("DELETE FROM auth_tokens")
    suspend fun deleteToken()
}
