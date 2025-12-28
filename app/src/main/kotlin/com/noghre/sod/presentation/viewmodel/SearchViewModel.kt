package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.exception.GlobalExceptionHandler
import com.noghre.sod.core.util.UiEvent
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.search.ClearSearchHistoryUseCase
import com.noghre.sod.domain.usecase.search.GetSearchHistoryUseCase
import com.noghre.sod.domain.usecase.search.SaveSearchQueryUseCase
import com.noghre.sod.domain.usecase.search.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: List<Product>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    // ✅ Search UseCases
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val saveSearchQueryUseCase: SaveSearchQueryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
    
    // Infrastructure
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults.asStateFlow()
    
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    
    init {
        loadSearchHistory()
    }
    
    fun search(
        query: String,
        page: Int = 1,
        pageSize: Int = 20,
        filters: Map<String, String> = emptyMap()
    ) {
        viewModelScope.launch(exceptionHandler.handler) {
            if (query.trim().isBlank()) {
                _uiState.value = SearchUiState.Idle
                _searchResults.value = emptyList()
                return@launch
            }
            
            _uiState.value = SearchUiState.Loading
            
            // ✅ Save to history
            saveSearchQueryUseCase(query)
            loadSearchHistory()
            
            // ✅ Perform search
            val params = SearchProductsUseCase.Params(
                query = query,
                page = page,
                pageSize = pageSize,
                filters = filters
            )
            val result = searchProductsUseCase(params)
            
            if (result.isSuccess) {
                val results = result.getOrNull() ?: emptyList()
                _searchResults.value = results
                _uiState.value = if (results.isEmpty()) {
                    SearchUiState.Success(emptyList())
                } else {
                    SearchUiState.Success(results)
                }
                Timber.d("Found ${results.size} products for query: $query")
            } else {
                val error = result.exceptionOrNull()
                _uiState.value = SearchUiState.Error(error?.message ?: "Search failed")
            }
        }
    }
    
    private fun loadSearchHistory() {
        viewModelScope.launch {
            val result = getSearchHistoryUseCase(10)
            
            if (result.isSuccess) {
                val history = result.getOrNull() ?: emptyList()
                _searchHistory.value = history
            }
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch(exceptionHandler.handler) {
            val result = clearSearchHistoryUseCase(Unit)
            
            if (result.isSuccess) {
                _searchHistory.value = emptyList()
                _uiEvent.emit(UiEvent.ShowToast("Search history cleared"))
                Timber.d("Search history cleared")
            }
        }
    }
}
