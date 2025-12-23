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
 * Data Access Object for categories.
 * Provides all database operations for CategoryEntity.
 */
@Dao
interface CategoryDao {

    /**
     * Inserts or replaces a single category.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    /**
     * Inserts or replaces multiple categories.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    /**
     * Updates a category that already exists.
     */
    @Update
    suspend fun updateCategory(category: CategoryEntity)

    /**
     * Deletes a category.
     */
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    /**
     * Deletes all categories.
     */
    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()

    /**
     * Gets a single category by ID as a Flow.
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryFlow(id: String): Flow<CategoryEntity?>

    /**
     * Gets a single category by ID (one-time fetch).
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategory(id: String): CategoryEntity?

    /**
     * Gets a category by slug.
     */
    @Query("SELECT * FROM categories WHERE slug = :slug")
    suspend fun getCategoryBySlug(slug: String): CategoryEntity?

    /**
     * Gets all categories as a Flow.
     * Ordered alphabetically by name.
     */
    @Query("""
        SELECT * FROM categories
        ORDER BY name ASC
    """)
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    /**
     * Gets all categories (one-time fetch).
     */
    @Query("""
        SELECT * FROM categories
        ORDER BY name ASC
    """)
    suspend fun getAllCategories(): List<CategoryEntity>

    /**
     * Gets the count of all categories.
     * Useful for checking if cache is empty.
     */
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int

    /**
     * Gets the most recently cached category timestamp.
     */
    @Query("SELECT MAX(cachedAt) FROM categories")
    suspend fun getMostRecentCacheTime(): Long?
}
