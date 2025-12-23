package com.noghre.sod.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.noghre.sod.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Search History management.
 */
@Dao
interface SearchHistoryDao {

    /**
     * Insert search query.
     */
    @Insert
    suspend fun insertSearchQuery(searchHistory: SearchHistoryEntity)

    /**
     * Get recent search queries.
     */
    @Query("SELECT * FROM search_history ORDER BY searched_at DESC LIMIT :limit")
    fun getRecentSearches(limit: Int = 10): Flow<List<SearchHistoryEntity>>

    /**
     * Delete a search history entry.
     */
    @Delete
    suspend fun deleteSearchHistory(searchHistory: SearchHistoryEntity)

    /**
     * Clear all search history.
     */
    @Query("DELETE FROM search_history")
    suspend fun clearAll()
}
