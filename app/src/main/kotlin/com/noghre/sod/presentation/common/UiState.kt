package com.noghre.sod.presentation.common

import com.noghre.sod.core.error.AppError

/**
 * 🎯 Generic UI state for screens
 * 
 * Represents Loading, Success, Error, Empty, and Idle states.
 * Used with StateFlow for reactive UI updates.
 * 
 * Usage:
 * ```kotlin
 * @HiltViewModel
 * class ProductListViewModel @Inject constructor(
 *     private val repository: ProductRepository
 * ) : ViewModel() {
 *     private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
 *     val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()
 *     
 *     fun loadProducts() {
 *         viewModelScope.launch {
 *             _uiState.value = UiState.Loading
 *             when(val result = repository.getProducts()) {
 *                 is Result.Success -> {
 *                     _uiState.value = if(result.data.isEmpty()) {
 *                         UiState.Empty
 *                     } else {
 *                         UiState.Success(result.data)
 *                     }
 *                 }
 *                 is Result.Error -> _uiState.value = UiState.Error(result.error)
 *                 else -> {}
 *             }
 *         }
 *     }
 * }
 * ```
 */
sealed class UiState<out T> {
    /**
     * Initial idle state (before any action)
     */
    object Idle : UiState<Nothing>()
    
    /**
     * Loading state
     */
    object Loading : UiState<Nothing>()
    
    /**
     * Success state with data
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Error state with error information
     */
    data class Error(val error: AppError) : UiState<Nothing>()
    
    /**
     * Empty state (when data exists but is empty)
     */
    object Empty : UiState<Nothing>()
    
    // ===== Convenience Properties =====
    
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isEmpty: Boolean get() = this is Empty
    val isIdle: Boolean get() = this is Idle
}

/**
 * 📨 One-time UI events (navigation, toasts, dialogs)
 * 
 * Used with Channel for one-time event delivery.
 * Events are consumed after being processed.
 * 
 * Usage:
 * ```kotlin
 * private val _events = Channel<UiEvent>(Channel.BUFFERED)
 * val events: Flow<UiEvent> = _events.receiveAsFlow()
 * 
 * // Emit event
 * _events.send(UiEvent.ShowToast("Item added to cart"))
 * 
 * // Collect events in UI
 * LaunchedEffect(Unit) {
 *     viewModel.events.collect { event ->
 *         when(event) {
 *             is UiEvent.ShowToast -> showToast(event.message)
 *             is UiEvent.Navigate -> navController.navigate(event.route)
 *             // ...
 *         }
 *     }
 * }
 * ```
 */
sealed class UiEvent {
    /**
     * Show a simple toast message
     */
    data class ShowToast(val message: String) : UiEvent()
    
    /**
     * Show a snackbar with optional action
     */
    data class ShowSnackbar(
        val message: String,
        val action: String? = null,
        val onActionClick: (() -> Unit)? = null
    ) : UiEvent()
    
    /**
     * Show error message (converts AppError to user message)
     */
    data class ShowError(val error: AppError) : UiEvent()
    
    /**
     * Navigate to another screen
     */
    data class Navigate(val route: String, val args: Map<String, String> = emptyMap()) : UiEvent()
    
    /**
     * Navigate back to previous screen
     */
    object NavigateBack : UiEvent()
    
    /**
     * Show a dialog
     */
    data class ShowDialog(
        val title: String,
        val message: String,
        val positiveButtonText: String = "بله",
        val negativeButtonText: String = "خیر",
        val onPositiveClick: (() -> Unit)? = null,
        val onNegativeClick: (() -> Unit)? = null
    ) : UiEvent()
    
    /**
     * Close the current screen
     */
    object CloseScreen : UiEvent()
    
    /**
     * Refresh/retry action
     */
    object RefreshData : UiEvent()
}

/**
 * 🔐 Data class for pagination state
 */
data class PaginationState(
    val page: Int = 1,
    val pageSize: Int = 20,
    val hasMorePages: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: AppError? = null
)