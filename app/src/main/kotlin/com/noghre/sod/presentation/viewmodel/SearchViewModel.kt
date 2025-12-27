package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.error.GlobalExceptionHandler
import com.noghre.sod.core.util.onError
import com.noghre.sod.core.util.onSuccess
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
import com.noghre.sod.domain.repository.SearchRepository
import com.noghre.sod.presentation.common.UiEvent
import com.noghre.sod.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val searchRepository: SearchRepository,
    private val exceptionHandler: GlobalExceptionHandler
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val searchResults: StateFlow<UiState<List<Product>>> = _searchResults.asStateFlow()
    
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()
    
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    init {
        loadSearchHistory()
    }
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        
        if (query.isBlank()) {
            _searchResults.value = UiState.Idle
            return
        }
        
        if (query.length >= 2) {
            performSearch(query)
        }
    }
    
    private fun performSearch(query: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            _searchResults.value = UiState.Loading
            Timber.d("Searching for: $query")
            
            productRepository.searchProducts(query)
                .onSuccess { products ->
                    Timber.d("Search results found: ${products.size}")
                    _searchResults.value = if (products.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(products)
                    }
                    // Save to search history
                    saveSearchQuery(query)
                }
                .onError { error ->
                    Timber.e("Search failed: ${error.message}")
                    _searchResults.value = UiState.Error(error)
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onSearchSubmit() {
        val query = _searchQuery.value.trim()
        if (query.isNotBlank()) {
            Timber.d("Search submitted: $query")
            performSearch(query)
        }
    }
    
    private fun saveSearchQuery(query: String) {
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Saving search query: $query")
            
            searchRepository.saveSearchQuery(query)
                .onSuccess {
                    Timber.d("Search query saved")
                    loadSearchHistory()
                }
                .onError { error ->
                    Timber.e("Failed to save search query: ${error.message}")
                }
        }
    }
    
    fun loadSearchHistory() {
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Loading search history")
            
            searchRepository.getSearchHistory()
                .onSuccess { history ->
                    Timber.d("Search history loaded: ${history.size}")
                    _searchHistory.value = history
                }
                .onError { error ->
                    Timber.e("Failed to load search history: ${error.message}")
                }
        }
    }
    
    fun onHistoryItemClick(query: String) {
        Timber.d("History item clicked: $query")
        _searchQuery.value = query
        performSearch(query)
    }
    
    fun onClearHistory() {
        viewModelScope.launch(exceptionHandler.handler) {
            Timber.d("Clearing search history")
            
            searchRepository.clearSearchHistory()
                .onSuccess {
                    Timber.d("Search history cleared")
                    _searchHistory.value = emptyList()
                    _events.send(UiEvent.ShowToast("تاریخچه با موفقیت حذف شد"))
                }
                .onError { error ->
                    Timber.e("Failed to clear history: ${error.message}")
                    _events.send(UiEvent.ShowError(error))
                }
        }
    }
    
    fun onProductClick(productId: String) {
        viewModelScope.launch {
            Timber.d("Product clicked: $productId")
            _events.send(UiEvent.Navigate("product_detail/$productId"))
        }
    }
    
    fun onRetryClick() {
        Timber.d("Retry clicked")
        onSearchSubmit()
    }
}
