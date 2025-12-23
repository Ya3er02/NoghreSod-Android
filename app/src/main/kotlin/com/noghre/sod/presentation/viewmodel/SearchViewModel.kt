package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.SearchProductsUseCase
import com.noghre.sod.domain.usecase.GetSearchHistoryUseCase
import com.noghre.sod.domain.usecase.SaveSearchHistoryUseCase
import com.noghre.sod.presentation.common.UiConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class SearchUiState(
    val results: List<Product> = emptyList(),
    val searchHistory: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val query: String = ""
)

/**
 * ViewModel for search screen.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val saveSearchHistoryUseCase: SaveSearchHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadSearchHistory()
        setupSearchDebounce()
    }

    /**
     * Update search query
     *
     * @param query Search query
     */
    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Load search history
     */
    private fun loadSearchHistory() {
        viewModelScope.launch {
            try {
                val history = getSearchHistoryUseCase()
                updateState { copy(searchHistory = history) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading search history")
            }
        }
    }

    /**
     * Search products
     *
     * @param query Search query
     */
    private suspend fun performSearch(query: String) {
        if (query.isEmpty()) {
            updateState { copy(results = emptyList(), query = "") }
            return
        }

        try {
            updateState { copy(isLoading = true, error = null, query = query) }
            val results = searchProductsUseCase(query)
            updateState { copy(results = results, isLoading = false) }
            saveSearchHistoryUseCase(query)
            loadSearchHistory()
        } catch (e: Exception) {
            Timber.e(e, "Error searching")
            updateState { copy(isLoading = false, error = e.message) }
        }
    }

    /**
     * Clear search history
     */
    fun clearSearchHistory() {
        viewModelScope.launch {
            updateState { copy(searchHistory = emptyList()) }
        }
    }

    /**
     * Setup debounced search
     */
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(UiConstants.DEBOUNCE_DELAY_MS)
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    private fun updateState(block: SearchUiState.() -> SearchUiState) {
        _uiState.update(block)
    }
}
