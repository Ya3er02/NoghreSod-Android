package com.noghre.sod.presentation.common

import com.noghre.sod.core.error.AppError

/**
 * 🗋 Generic UI state for Compose screens
 * 
 * Represents Loading, Success, Error, Empty, and Idle states.
 * Use this to manage screen state in ViewModels.
 * 
 * Usage in ViewModel:
 * ```
 * private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
 * val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()
 * 
 * fun loadProducts() {
 *     _uiState.value = UiState.Loading
 *     viewModelScope.launch(exceptionHandler.handler) {
 *         val result = repository.getProducts()
 *         _uiState.value = when (result) {
 *             is Result.Success -> {
 *                 if (result.data.isEmpty()) UiState.Empty
 *                 else UiState.Success(result.data)
 *             }
 *             is Result.Error -> UiState.Error(result.error)
 *             is Result.Loading -> UiState.Loading
 *         }
 *     }
 * }
 * ```
 * 
 * Usage in Composable:
 * ```
 * val uiState by viewModel.uiState.collectAsStateWithLifecycle()
 * 
 * when (uiState) {
 *     UiState.Idle -> Unit
 *     UiState.Loading -> LoadingView()
 *     is UiState.Success -> ProductList(uiState.data)
 *     is UiState.Error -> ErrorView(uiState.error) { viewModel.retry() }
 *     UiState.Empty -> EmptyView()
 * }
 * ```
 */
sealed class UiState<out T> {
    
    /**
     * Initial state, no action taken yet
     */
    object Idle : UiState<Nothing>()
    
    /**
     * Loading state, waiting for data
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
     * Empty state, no data available
     */
    object Empty : UiState<Nothing>()
    
    // ===== Convenience Properties =====
    
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isEmpty: Boolean get() = this is Empty
    val isIdle: Boolean get() = this is Idle
    
    /**
     * Returns data if Success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Returns error if Error, null otherwise
     */
    fun getErrorOrNull(): AppError? = when (this) {
        is Error -> error
        else -> null
    }
}

/**
 * 🗋 One-time UI events
 * 
 * Use Channel for one-time events that should not be remembered.
 * Examples: Navigation, Toasts, Dialogs, Snackbars
 * 
 * Usage in ViewModel:
 * ```
 * private val _events = Channel<UiEvent>(capacity = Channel.BUFFERED)
 * val events: Flow<UiEvent> = _events.receiveAsFlow()
 * 
 * fun goToProductDetail(productId: String) {
 *     viewModelScope.launch {
 *         _events.send(UiEvent.Navigate("product/$productId"))
 *     }
 * }
 * 
 * fun showError(error: AppError) {
 *     viewModelScope.launch {
 *         _events.send(UiEvent.ShowError(error))
 *     }
 * }
 * ```
 * 
 * Usage in Composable:
 * ```
 * LaunchedEffect(Unit) {
 *     viewModel.events.collect { event ->
 *         when (event) {
 *             is UiEvent.ShowToast -> showToast(event.message)
 *             is UiEvent.Navigate -> navController.navigate(event.route)
 *             is UiEvent.ShowError -> showErrorDialog(event.error)
 *             else -> {}
 *         }
 *     }
 * }
 * ```
 */
sealed class UiEvent {
    
    /**
     * Show a toast message (short duration)
     */
    data class ShowToast(val message: String) : UiEvent()
    
    /**
     * Show a snackbar message (with optional action)
     */
    data class ShowSnackbar(
        val message: String,
        val action: String? = null,
        val onAction: (() -> Unit)? = null
    ) : UiEvent()
    
    /**
     * Show error message in snackbar or dialog
     */
    data class ShowError(val error: AppError) : UiEvent()
    
    /**
     * Navigate to another screen
     */
    data class Navigate(val route: String) : UiEvent()
    
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
        val positiveText: String = "تایید",
        val negativeText: String? = null,
        val onPositive: (() -> Unit)? = null,
        val onNegative: (() -> Unit)? = null
    ) : UiEvent()
    
    /**
     * Show confirmation dialog
     */
    data class ShowConfirmation(
        val title: String,
        val message: String,
        val onConfirm: (() -> Unit)?,
        val onCancel: (() -> Unit)? = null
    ) : UiEvent()
    
    /**
     * Retry the last failed operation
     */
    object RetryLastOperation : UiEvent()
}

/**
 * 🌟 Effect state for handling side effects
 * 
 * Represents different effect states during async operations.
 */
sealed class EffectState {
    object Idle : EffectState()
    object Loading : EffectState()
    object Success : EffectState()
    data class Error(val error: AppError) : EffectState()
}
