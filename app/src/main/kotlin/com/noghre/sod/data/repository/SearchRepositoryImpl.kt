package com.noghre.sod.data.repository

import com.noghre.sod.core.error.AppException
import com.noghre.sod.core.util.Result
import com.noghre.sod.data.local.PreferencesManager
import com.noghre.sod.domain.repository.SearchRepository
import timber.log.Timber
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : SearchRepository {
    
    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 20
    }
    
    override suspend fun saveSearchQuery(query: String): Result<Unit> = try {
        Timber.d("Saving search query: $query")
        
        if (query.isBlank()) {
            Timber.w("Empty query, not saving")
            return Result.Success(Unit)
        }
        
        val currentHistory = getSearchHistoryList()
        
        // Remove if already exists (to add at beginning)
        val updatedHistory = currentHistory.filterNot { it.equals(query, ignoreCase = true) }
            .toMutableList()
            .apply {
                add(0, query)
                // Keep only latest items
                if (size > MAX_HISTORY_SIZE) {
                    removeRange(MAX_HISTORY_SIZE, size)
                }
            }
        
        saveHistoryToPreferences(updatedHistory)
        Timber.d("Search query saved. History size: ${updatedHistory.size}")
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error saving search query: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Failed to save search"))
    }
    
    override suspend fun getSearchHistory(): Result<List<String>> = try {
        Timber.d("Fetching search history")
        
        val history = getSearchHistoryList()
        Timber.d("Search history: ${history.size} items")
        
        Result.Success(history)
    } catch (e: Exception) {
        Timber.e("Error fetching search history: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Failed to fetch history"))
    }
    
    override suspend fun clearSearchHistory(): Result<Unit> = try {
        Timber.d("Clearing search history")
        
        saveHistoryToPreferences(emptyList())
        Timber.d("Search history cleared")
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error clearing search history: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Failed to clear history"))
    }
    
    override suspend fun removeFromHistory(query: String): Result<Unit> = try {
        Timber.d("Removing from history: $query")
        
        val currentHistory = getSearchHistoryList()
        val updatedHistory = currentHistory.filterNot { it.equals(query, ignoreCase = true) }
        
        saveHistoryToPreferences(updatedHistory)
        Timber.d("Removed from history. New size: ${updatedHistory.size}")
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Timber.e("Error removing from history: ${e.message}")
        Result.Error(AppException.DatabaseException(e.message ?: "Failed to remove"))
    }
    
    private suspend fun getSearchHistoryList(): List<String> {
        return try {
            val json = preferencesManager.getString(SEARCH_HISTORY_KEY, "[]")
            // Parse JSON array (simple implementation)
            json.removePrefix("[").removeSuffix("]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
                .filter { it.isNotBlank() }
        } catch (e: Exception) {
            Timber.e("Error parsing search history: ${e.message}")
            emptyList()
        }
    }
    
    private suspend fun saveHistoryToPreferences(history: List<String>) {
        try {
            val json = history.joinToString(",") { "\"$it\"" }
            val jsonArray = "[$json]"
            preferencesManager.saveString(SEARCH_HISTORY_KEY, jsonArray)
        } catch (e: Exception) {
            Timber.e("Error saving history to preferences: ${e.message}")
        }
    }
}
