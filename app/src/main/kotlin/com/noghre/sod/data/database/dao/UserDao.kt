package com.noghre.sod.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * UserDao - Data Access Object for user database operations.
 * 
 * Handles all database queries for user profiles and authentication.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Dao
interface UserDao {
    
    /**
     * Insert a new user
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    /**
     * Update user profile
     */
    @Update
    suspend fun updateUser(user: UserEntity)
    
    /**
     * Delete user
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    /**
     * Get user by ID
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    /**
     * Get user by ID as Flow for real-time updates
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>
    
    /**
     * Get user by email
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    /**
     * Get user by phone number
     */
    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getUserByPhoneNumber(phoneNumber: String): UserEntity?
    
    /**
     * Check if email exists
     */
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun emailExists(email: String): Int
    
    /**
     * Check if phone exists
     */
    @Query("SELECT COUNT(*) FROM users WHERE phoneNumber = :phoneNumber")
    suspend fun phoneExists(phoneNumber: String): Int
    
    /**
     * Update user login time
     */
    @Query("""
        UPDATE users 
        SET lastLoginAt = datetime('now'), 
            lastActivityAt = datetime('now')
        WHERE id = :userId
    """)
    suspend fun updateLastLogin(userId: String)
    
    /**
     * Update user activity time
     */
    @Query("UPDATE users SET lastActivityAt = datetime('now') WHERE id = :userId")
    suspend fun updateLastActivity(userId: String)
    
    /**
     * Update user profile
     */
    @Query("""
        UPDATE users 
        SET firstName = :firstName,
            lastName = :lastName,
            avatar = :avatar,
            bio = :bio,
            updatedAt = datetime('now')
        WHERE id = :userId
    """)
    suspend fun updateUserProfile(
        userId: String,
        firstName: String?,
        lastName: String?,
        avatar: String?,
        bio: String?
    )
    
    /**
     * Update user preferences
     */
    @Query("""
        UPDATE users 
        SET language = :language,
            currency = :currency,
            notificationsEnabled = :notificationsEnabled,
            emailNotificationsEnabled = :emailNotificationsEnabled,
            smsNotificationsEnabled = :smsNotificationsEnabled,
            updatedAt = datetime('now')
        WHERE id = :userId
    """)
    suspend fun updateUserPreferences(
        userId: String,
        language: String,
        currency: String,
        notificationsEnabled: Boolean,
        emailNotificationsEnabled: Boolean,
        smsNotificationsEnabled: Boolean
    )
    
    /**
     * Update email verification status
     */
    @Query("""
        UPDATE users 
        SET isEmailVerified = 1,
            updatedAt = datetime('now')
        WHERE id = :userId
    """)
    suspend fun verifyEmail(userId: String)
    
    /**
     * Update phone verification status
     */
    @Query("""
        UPDATE users 
        SET isPhoneVerified = 1,
            updatedAt = datetime('now')
        WHERE id = :userId
    """)
    suspend fun verifyPhoneNumber(userId: String)
    
    /**
     * Update membership tier
     */
    @Query("""
        UPDATE users 
        SET membershipTier = :tier,
            updatedAt = datetime('now')
        WHERE id = :userId
    """)
    suspend fun updateMembershipTier(userId: String, tier: String)
    
    /**
     * Update loyalty points
     */
    @Query("""
        UPDATE users 
        SET membershipPoints = membershipPoints + :points,
            loyaltyBalance = loyaltyBalance + :balance,
            updatedAt = datetime('now')
        WHERE id = :userId
    """)
    suspend fun updateLoyaltyPoints(userId: String, points: Long, balance: Double)
    
    /**
     * Update order statistics
     */
    @Query("""
        UPDATE users 
        SET totalOrderCount = totalOrderCount + 1,
            totalSpent = totalSpent + :amount,
            updatedAt = datetime('now')
        WHERE id = :userId
    """)
    suspend fun updateOrderStatistics(userId: String, amount: Double)
    
    /**
     * Block user account
     */
    @Query("""
        UPDATE users 
        SET isBlocked = 1,
            accountStatus = 'suspended',
            updatedAt = datetime('now')
        WHERE id = :userId
    """)
    suspend fun blockUser(userId: String)
    
    /**
     * Unblock user account
     */
    @Query("""
        UPDATE users 
        SET isBlocked = 0,
            accountStatus = 'active',
            updatedAt = datetime('now')
        WHERE id = :userId
    """)
    suspend fun unblockUser(userId: String)
    
    /**
     * Update auth token
     */
    @Query("""
        UPDATE users 
        SET authToken = :token,
            tokenExpiresAt = :expiresAt,
            updatedAt = datetime('now')
        WHERE id = :userId
    """)
    suspend fun updateAuthToken(userId: String, token: String, expiresAt: java.util.Date)
    
    /**
     * Get active users count
     */
    @Query("SELECT COUNT(*) FROM users WHERE isActive = 1 AND isBlocked = 0")
    suspend fun getActiveUsersCount(): Int
    
    /**
     * Get users by membership tier
     */
    @Query("SELECT * FROM users WHERE membershipTier = :tier")
    suspend fun getUsersByMembershipTier(tier: String): List<UserEntity>
    
    /**
     * Search users
     */
    @Query("""
        SELECT * FROM users 
        WHERE email LIKE '%' || :query || '%' 
        OR firstName LIKE '%' || :query || '%' 
        OR lastName LIKE '%' || :query || '%'
        LIMIT :limit
    """)
    suspend fun searchUsers(query: String, limit: Int = 20): List<UserEntity>
    
    /**
     * Clear auth token on logout
     */
    @Query("""
        UPDATE users 
        SET authToken = NULL,
            tokenExpiresAt = NULL,
            updatedAt = datetime('now')
        WHERE id = :userId
    """)
    suspend fun clearAuthToken(userId: String)
}
