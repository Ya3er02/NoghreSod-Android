package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.local.entity.AddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddresses(addresses: List<AddressEntity>)

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Delete
    suspend fun deleteAddress(address: AddressEntity)

    @Query("SELECT * FROM addresses WHERE id = :id")
    fun getAddressById(id: String): Flow<AddressEntity?>

    @Query("SELECT * FROM addresses WHERE userId = :userId ORDER BY isDefault DESC, createdAt DESC")
    fun getUserAddresses(userId: String): Flow<List<AddressEntity>>

    @Query("SELECT * FROM addresses WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    fun getDefaultAddress(userId: String): Flow<AddressEntity?>

    @Query("DELETE FROM addresses WHERE userId = :userId")
    suspend fun deleteUserAddresses(userId: String)

    @Query("DELETE FROM addresses")
    suspend fun clearAll()
}