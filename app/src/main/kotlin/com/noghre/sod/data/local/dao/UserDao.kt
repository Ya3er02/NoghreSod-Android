package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for User profile caching.
 */
@Dao
interface UserDao {

    /**
     * Insert or replace user profile.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /**
     * Update user profile.
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Get current user profile.
     */
    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    /**
     * Get user synchronously.
     */
    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUserSync(): UserEntity?

    /**
     * Delete user profile.
     */
    @Query("DELETE FROM user")
    suspend fun deleteUser()
}
