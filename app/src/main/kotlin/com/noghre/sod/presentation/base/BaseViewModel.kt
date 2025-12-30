package com.noghre.sod.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Result
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Base ViewModel for common functionality across all ViewModels.
 * 
 * Provides:
 * - State management with StateFlow
 * - Event emission with SharedFlow
 * - Error handling
 * - Loading state management
 * - Coroutine lifecycle management
 * 
 * @param T Type of view state
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
abstract class BaseViewModel<T : Any> : ViewModel() {

    // ============== State Management ==============
    
    protected val _uiState = MutableStateFlow<T?>(null)
    val uiState: StateFlow<T?> = _uiState.asStateFlow()

    // ============== Event Management ==============
    
    protected val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    // ============== Loading & Error States ==============
    
    protected val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    protected val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ============== Exception Handler ==============
    
    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "Coroutine exception: ${exception.message}")
        _isLoading.value = false
        _error.value = exception.localizedMessage ?: "Unknown error occurred"
    }

    // ============== Loading Management ==============
    
    protected fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    protected fun setError(error: String?) {
        _error.value = error
    }

    protected fun clearError() {
        _error.value = null
    }

    // ============== Event Emission ==============
    
    /**
     * Emit a one-time UI event.
     * 
     * @param event Event to emit
     */
    protected fun emitEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    // ============== State Update ==============
    
    /**
     * Update current state.
     * 
     * @param newState New state to set
     */
    protected fun setState(newState: T) {
        _uiState.value = newState
    }

    // ============== Safe Async Operations ==============
    
    /**
     * Execute async operation with automatic error and loading handling.
     * 
     * @param block Suspend block to execute
     * @param onSuccess Callback on success
     * @param onError Callback on error (optional)
     */
    protected fun launchAsync(
        block: suspend () -> Unit,
        onSuccess: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        viewModelScope.launch(exceptionHandler) {
            try {
                setLoading(true)
                clearError()
                block()
                onSuccess?.invoke()
            } catch (e: Exception) {
                Timber.e(e, "Error in async operation")
                onError?.invoke(e)
                setError(e.localizedMessage ?: "Unknown error")
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * Execute async operation returning Result.
     * 
     * @param block Suspend block returning Result
     * @param onSuccess Callback on success
     * @param onError Callback on error (optional)
     */
    protected fun <R> launchAsyncWithResult(
        block: suspend () -> Result<R>,
        onSuccess: (R) -> Unit,
        onError: ((Exception) -> Unit)? = null
    ) {
        viewModelScope.launch(exceptionHandler) {
            try {
                setLoading(true)
                clearError()
                when (val result = block()) {
                    is Result.Success -> onSuccess(result.data)
                    is Result.Error -> {
                        setError(result.message)
                        onError?.invoke(result.exception)
                    }
                }
            } catch (e: Exception) {
                setError(e.localizedMessage ?: "Unknown error")
                onError?.invoke(e)
            } finally {
                setLoading(false)
            }
        }
    }

    // ============== Lifecycle ==============
    
    override fun onCleared() {
        super.onCleared()
        clearError()
    }
}

/**
 * Sealed class for UI events (one-time events).
 * 
* Used for navigation, showing dialogs, etc.
 * Should not be used for state that needs to survive configuration changes.
 */
sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class ShowDialog(val title: String, val message: String) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    object NavigateBack : UiEvent()
    data class Error(val exception: Throwable) : UiEvent()
}
