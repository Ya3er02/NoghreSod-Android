package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * User Data Access Object (DAO)
 * Manages user profile and account operations
 */
@Dao
interface UserDao {

    // ==================== INSERT Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // ==================== QUERY Operations ====================

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE phone = :phone")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE accountStatus = 'ACTIVE' LIMIT 1")
    suspend fun getActiveUser(): UserEntity?

    @Query("SELECT * FROM users WHERE accountStatus = 'ACTIVE' LIMIT 1")
    fun getActiveUserFlow(): Flow<UserEntity?>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query(
        """SELECT * FROM users 
           WHERE verificationStatus = 'VERIFIED' 
           ORDER BY createdAt DESC"""
    )
    fun getVerifiedUsers(): Flow<List<UserEntity>>

    // ==================== UPDATE Operations ====================

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query(
        """UPDATE users 
           SET firstName = :firstName, lastName = :lastName, updatedAt = :timestamp 
           WHERE id = :userId"""
    )
    suspend fun updateUserName(
        userId: String,
        firstName: String,
        lastName: String,
        timestamp: Long
    )

    @Query(
        """UPDATE users 
           SET email = :email, updatedAt = :timestamp 
           WHERE id = :userId"""
    )
    suspend fun updateEmail(
        userId: String,
        email: String,
        timestamp: Long
    )

    @Query(
        """UPDATE users 
           SET profileImage = :imageUrl, updatedAt = :timestamp 
           WHERE id = :userId"""
    )
    suspend fun updateProfileImage(
        userId: String,
        imageUrl: String,
        timestamp: Long
    )

    @Query(
        """UPDATE users 
           SET verificationStatus = :status, updatedAt = :timestamp 
           WHERE id = :userId"""
    )
    suspend fun updateVerificationStatus(
        userId: String,
        status: String,
        timestamp: Long
    )

    @Query(
        """UPDATE users 
           SET accountStatus = :status, updatedAt = :timestamp 
           WHERE id = :userId"""
    )
    suspend fun updateAccountStatus(
        userId: String,
        status: String,
        timestamp: Long
    )

    @Query(
        """UPDATE users 
           SET lastLoginAt = :timestamp 
           WHERE id = :userId"""
    )
    suspend fun updateLastLoginTime(
        userId: String,
        timestamp: Long
    )

    @Query(
        """UPDATE users 
           SET nationalId = :nationalId, updatedAt = :timestamp 
           WHERE id = :userId"""
    )
    suspend fun updateNationalId(
        userId: String,
        nationalId: String,
        timestamp: Long
    )

    // ==================== DELETE Operations ====================

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
