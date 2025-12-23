package com.noghre.sod.domain.repository

interface SearchRepository {
    suspend fun getSearchSuggestions(query: String): List<String>
    suspend fun getRecentSearches(): List<String>
    suspend fun addRecentSearch(query: String)
}
