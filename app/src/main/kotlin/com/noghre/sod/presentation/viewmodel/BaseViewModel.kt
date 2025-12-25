package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.error.AppError
import com.noghre.sod.presentation.event.EventHandler
import com.noghre.sod.presentation.event.UiEvent
import com.noghre.sod.presentation.uistate.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
* 📺 Base ViewModel
 * 
 * Provides common functionality for all ViewModels:
 * - State management
 * - Event handling
 * - Error handling
 * - Coroutine management
 * - Analytics tracking
 * 
 * @since 1.0.0
 */
abstract class BaseViewModel<T : Any>(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    
    // ==================== State Management ====================
    
    protected val _uiState = MutableStateFlow<UiState<T>>(UiState.Idle)
    val uiState: StateFlow<UiState<T>> = _uiState.asStateFlow()
    
    // ==================== Event Handling ====================
    
    protected val eventHandler = EventHandler<UiEvent>()
    val events: Flow<UiEvent> = eventHandler.events
    
    // ==================== Job Tracking ====================
    
    private val jobs = mutableListOf<Job>()
    
    // ==================== Exception Handler ====================
    
    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "Uncaught exception in ${this::class.simpleName}")
        handleException(exception)
    }
    
    // ==================== State Methods ====================
    
    /**
     * Update UI state
     */
    protected fun setState(state: UiState<T>) {
        _uiState.value = state
    }
    
    /**
     * Set loading state
     */
    protected fun setLoading(hasData: Boolean = false) {
        _uiState.value = if (hasData) {
            val currentData = _uiState.value.getData()
            if (currentData != null) {
                UiState.LoadingWithData(currentData)
            } else {
                UiState.Loading
            }
        } else {
            UiState.Loading
        }
    }
    
    /**
     * Set success state
     */
    protected fun setSuccess(data: T) {
        _uiState.value = UiState.Success(data)
    }
    
    /**
     * Set error state
     */
    protected fun setError(error: AppError, canRetry: Boolean = true) {
        val currentData = _uiState.value.getData()
        _uiState.value = if (currentData != null) {
            UiState.ErrorWithData(currentData, error, canRetry)
        } else {
            UiState.Error(error, null, null)
        }
    }
    
    /**
     * Set empty state
     */
    protected fun setEmpty(message: String? = null) {
        _uiState.value = UiState.Empty(message)
    }
    
    // ==================== Event Methods ====================
    
    /**
     * Send UI event
     */
    protected fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            eventHandler.send(event)
        }
    }
    
    /**
     * Send navigation event
     */
    protected fun navigate(destination: UiEvent.Navigation) {
        sendEvent(destination)
    }
    
    /**
     * Send feedback event (snackbar, toast, dialog)
     */
    protected fun sendFeedback(feedback: UiEvent.Feedback) {
        sendEvent(feedback)
    }
    
    /**
     * Send error event
     */
    protected fun sendError(error: UiEvent.Error) {
        sendEvent(error)
    }
    
    // ==================== Coroutine Helpers ====================
    
    /**
     * Launch coroutine with error handling
     */
    protected fun launchIO(
        onStart: (() -> Unit)? = null,
        block: suspend () -> Unit
    ): Job {
        val job = viewModelScope.launch(ioDispatcher + exceptionHandler) {
            onStart?.invoke()
            block()
        }
        jobs.add(job)
        return job
    }
    
    /**
     * Launch coroutine on main dispatcher
     */
    protected fun launchMain(
        onStart: (() -> Unit)? = null,
        block: suspend () -> Unit
    ): Job {
        val job = viewModelScope.launch(mainDispatcher + exceptionHandler) {
            onStart?.invoke()
            block()
        }
        jobs.add(job)
        return job
    }
    
    // ==================== Error Handling ====================
    
    /**
     * Handle exception
     */
    protected open fun handleException(exception: Throwable) {
        val appError = when (exception) {
            is AppError -> exception
            else -> AppError.UnknownError(exception)
        }
        
        setError(appError)
        sendError(UiEvent.Error.ServerError(
            error = AppError.ServerError(
                statusCode = 500,
                serverMessage = exception.message,
                code = "UNKNOWN_ERROR",
                userMessage = "خطایی رخ داد"
            )
        ))
    }
    
    /**
     * Handle network error
     */
    protected fun handleNetworkError(error: AppError.NetworkError) {
        setError(error)
        sendError(UiEvent.Error.NetworkError(error))
    }
    
    /**
     * Handle server error
     */
    protected fun handleServerError(error: AppError.ServerError) {
        setError(error, canRetry = error.canRetry)
        sendError(UiEvent.Error.ServerError(error))
    }
    
    /**
     * Handle auth error
     */
    protected fun handleAuthError(error: AppError.AuthError) {
        setError(error)
        if (error.requiresLogin) {
            navigate(UiEvent.Navigation.ToUrl("login"))
        }
        sendError(UiEvent.Error.AuthError(error))
    }
    
    // ==================== Analytics ====================
    
    /**
     * Track screen view
     */
    protected fun trackScreenView(screenName: String, screenClass: String? = null) {
        sendEvent(UiEvent.Analytics.TrackScreenView(
            screenName = screenName,
            screenClass = screenClass ?: this::class.simpleName
        ))
    }
    
    /**
     * Track event
     */
    protected fun trackEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        sendEvent(UiEvent.Analytics.TrackEvent(
            eventName = eventName,
            parameters = parameters
        ))
    }
    
    /**
     * Track exception
     */
    protected fun trackException(exception: Throwable, fatal: Boolean = false) {
        sendEvent(UiEvent.Analytics.TrackException(
            exception = exception,
            fatal = fatal
        ))
    }
    
    // ==================== Lifecycle ====================
    
    override fun onCleared() {
        super.onCleared()
        
        // Cancel all jobs
        jobs.forEach { it.cancel() }
        jobs.clear()
        
        Timber.d("${this::class.simpleName} cleared")
    }
}
