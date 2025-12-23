package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noghre.sod.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Category database operations.
 */
@Dao
interface CategoryDao {

    /**
     * Insert categories, replacing existing entries.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    /**
     * Get all categories.
     */
    @Query("SELECT * FROM categories WHERE is_active = 1 ORDER BY display_order")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    /**
     * Get category by ID.
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): CategoryEntity?

    /**
     * Get subcategories by parent ID.
     */
    @Query("SELECT * FROM categories WHERE parent_id = :parentId AND is_active = 1 ORDER BY display_order")
    fun getCategoriesByParent(parentId: String): Flow<List<CategoryEntity>>

    /**
     * Update a category.
     */
    @Update
    suspend fun updateCategory(category: CategoryEntity)

    /**
     * Delete a category.
     */
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    /**
     * Delete old cached categories.
     */
    @Query("DELETE FROM categories WHERE cached_at < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)

    /**
     * Clear all categories.
     */
    @Query("DELETE FROM categories")
    suspend fun clearAll()
}
