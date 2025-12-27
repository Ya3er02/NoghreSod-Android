package com.noghre.sod.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.core.error.GlobalExceptionHandler
import com.noghre.sod.core.util.Result
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.repository.ProductRepository
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

/**
 * 📅 Product List ViewModel
 * 
 * Manages product list state and events with comprehensive error handling.
 * 
 * Example of proper ViewModel architecture with:
 * - GlobalExceptionHandler for coroutine exceptions
 * - StateFlow for UI state
 * - Channel for one-time events
 * - Proper error classification
 * - Persian error messages
 */
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val exceptionHandler: GlobalExceptionHandler,
) : ViewModel() {

    // ========== UI State ==========
    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    // ========== Events ==========
    private val _events = Channel<UiEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // ========== Filter State ==========
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    // ========== Private State ==========
    private var lastQuery = ""
    private var lastCategory: String? = null

    init {
        Timber.d("[ProductListVM] Initialized")
        loadProducts()
    }

    /**
     * 🔄 Load products with current filters
     */
    fun loadProducts() {
        Timber.d("[ProductListVM] Loading products")
        _uiState.value = UiState.Loading

        viewModelScope.launch(exceptionHandler.handler) {
            val result = repository.getProducts()

            when (result) {
                is Result.Success -> {
                    Timber.d("[ProductListVM] Products loaded: ${result.data.size}")
                    
                    _uiState.value = if (result.data.isEmpty()) {
                        Timber.d("[ProductListVM] No products found")
                        UiState.Empty
                    } else {
                        UiState.Success(result.data)
                    }
                }

                is Result.Error -> {
                    Timber.e("[ProductListVM] Error loading products: ${result.error.message}")
                    _uiState.value = UiState.Error(result.error)
                    sendEvent(UiEvent.ShowError(result.error))
                }

                is Result.Loading -> {
                    _uiState.value = UiState.Loading
                }
            }
        }
    }

    /**
     * 🔍 Search products
     */
    fun searchProducts(query: String) {
        Timber.d("[ProductListVM] Searching: $query")
        
        if (query.isBlank()) {
            Timber.d("[ProductListVM] Search query empty, reloading all")
            _searchQuery.value = ""
            loadProducts()
            return
        }

        _searchQuery.value = query
        lastQuery = query
        _uiState.value = UiState.Loading

        viewModelScope.launch(exceptionHandler.handler) {
            val result = repository.searchProducts(query)

            when (result) {
                is Result.Success -> {
                    Timber.d("[ProductListVM] Search results: ${result.data.size}")
                    
                    _uiState.value = if (result.data.isEmpty()) {
                        Timber.d("[ProductListVM] No search results")
                        UiState.Empty
                    } else {
                        UiState.Success(result.data)
                    }
                }

                is Result.Error -> {
                    Timber.e("[ProductListVM] Search error: ${result.error.message}")
                    _uiState.value = UiState.Error(result.error)
                    sendEvent(UiEvent.ShowError(result.error))
                }

                is Result.Loading -> {
                    _uiState.value = UiState.Loading
                }
            }
        }
    }

    /**
     * 🌟 Filter by category
     */
    fun filterByCategory(categoryId: String?) {
        Timber.d("[ProductListVM] Filtering by category: $categoryId")
        
        _selectedCategory.value = categoryId
        lastCategory = categoryId
        _currentPage.value = 1

        if (categoryId == null) {
            loadProducts()
            return
        }

        _uiState.value = UiState.Loading

        viewModelScope.launch(exceptionHandler.handler) {
            val result = repository.getProductsByCategory(categoryId)

            when (result) {
                is Result.Success -> {
                    Timber.d("[ProductListVM] Filtered products: ${result.data.size}")
                    
                    _uiState.value = if (result.data.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(result.data)
                    }
                }

                is Result.Error -> {
                    Timber.e("[ProductListVM] Filter error: ${result.error.message}")
                    _uiState.value = UiState.Error(result.error)
                    sendEvent(UiEvent.ShowError(result.error))
                }

                is Result.Loading -> {
                    _uiState.value = UiState.Loading
                }
            }
        }
    }

    /**
     * 🔄 Retry last operation
     */
    fun retry() {
        Timber.d("[ProductListVM] Retrying last operation")
        
        when {
            lastQuery.isNotEmpty() -> searchProducts(lastQuery)
            lastCategory != null -> filterByCategory(lastCategory)
            else -> loadProducts()
        }
    }

    /**
     * 🔍 Navigate to product detail
     */
    fun goToProductDetail(productId: String) {
        Timber.d("[ProductListVM] Navigating to product: $productId")
        viewModelScope.launch {
            _events.send(UiEvent.Navigate("products/$productId"))
        }
    }

    /**
     * ❤️ Toggle product favorite
     */
    fun toggleFavorite(product: Product) {
        Timber.d("[ProductListVM] Toggling favorite for: ${product.id}")
        viewModelScope.launch(exceptionHandler.handler) {
            val result = if (product.isFavorite) {
                repository.removeFavorite(product.id)
            } else {
                repository.addFavorite(product.id)
            }

            when (result) {
                is Result.Success -> {
                    Timber.d("[ProductListVM] Favorite toggled")
                    sendEvent(UiEvent.ShowToast(
                        if (product.isFavorite) "حذف شد" else "افزوده شد"
                    ))
                }

                is Result.Error -> {
                    Timber.e("[ProductListVM] Favorite error: ${result.error.message}")
                    sendEvent(UiEvent.ShowError(result.error))
                }

                is Result.Loading -> {}
            }
        }
    }

    /**
     * 🛒 Add product to cart
     */
    fun addToCart(productId: String) {
        Timber.d("[ProductListVM] Adding to cart: $productId")
        viewModelScope.launch {
            _events.send(UiEvent.ShowToast("به سبد افزوده شد"))
            _events.send(UiEvent.Navigate("cart"))
        }
    }

    /**
     * Send one-time event to UI
     */
    private suspend fun sendEvent(event: UiEvent) {
        try {
            _events.send(event)
        } catch (e: Exception) {
            Timber.e(e, "[ProductListVM] Error sending event")
        }
    }

    companion object {
        private const val TAG = "ProductListVM"
    }
}
