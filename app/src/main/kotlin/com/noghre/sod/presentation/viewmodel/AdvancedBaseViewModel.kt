package com.noghre.sod.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.noghre.sod.presentation.navigation.Route

/**
 * Advanced base ViewModel with enterprise-grade state management
 * 
 * Features:
 * - Type-safe state management with StateFlow
 * - Event-driven side effects with SharedFlow
 * - Centralized error handling
 * - Built-in analytics tracking
 * - Automatic scope management
 * - Navigation state preservation
 */
abstract class AdvancedBaseViewModel<State : Any, Event : Any> : ViewModel() {
    
    // ============================================
    // State Management
    // ============================================
    
    protected val _state = MutableStateFlow<State>(getInitialState())
    val state: StateFlow<State> = _state.asStateFlow()
    
    protected val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()
    
    protected val _navigationEvents = MutableSharedFlow<Route>()
    val navigationEvents: SharedFlow<Route> = _navigationEvents.asSharedFlow()
    
    protected val _errorEvents = MutableSharedFlow<String>()
    val errorEvents: SharedFlow<String> = _errorEvents.asSharedFlow()
    
    protected val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()
    
    // ============================================
    // Exception Handling
    // ============================================
    
    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleException(exception)
    }
    
    // ============================================
    // Abstract Methods
    // ============================================
    
    /**
     * Provides the initial state for this ViewModel
     */
    protected abstract fun getInitialState(): State
    
    /**
     * Called when ViewModel is initialized
     */
    protected open fun onInitialize() {}
    
    // ============================================
    // State Management Helpers
    // ============================================
    
    /**
     * Update state with safe casting
     */
    protected fun setState(newState: State) {
        if (_state.value != newState) {
            _state.value = newState
            logStateChange()
        }
    }
    
    /**
     * Update state based on current state
     */
    protected fun updateState(block: (State) -> State) {
        setState(block(_state.value))
    }
    
    /**
     * Emit a new event
     */
    protected fun emitEvent(event: Event) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
    
    /**
     * Navigate to a new route
     */
    protected fun navigateTo(route: Route) {
        viewModelScope.launch {
            _navigationEvents.emit(route)
        }
    }
    
    /**
     * Show error message
     */
    protected fun showError(message: String) {
        viewModelScope.launch {
            _errorEvents.emit(message)
        }
    }
    
    /**
     * Execute a suspending task with loading state
     */
    protected fun <T> executeAsync(
        task: suspend () -> T,
        onSuccess: (T) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    ): Job {
        return viewModelScope.launch(exceptionHandler) {
            try {
                _loadingState.value = true
                val result = task()
                onSuccess(result)
            } catch (e: Exception) {
                onError?.invoke(e) ?: handleException(e)
            } finally {
                _loadingState.value = false
            }
        }
    }
    
    /**
     * Execute multiple async tasks in parallel
     */
    protected fun <T> executeParallel(
        vararg tasks: suspend () -> T,
        onSuccess: (List<T>) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    ): Job {
        return viewModelScope.launch(exceptionHandler) {
            try {
                _loadingState.value = true
                val results = tasks.map { task ->
                    task.invoke()
                }
                onSuccess(results)
            } catch (e: Exception) {
                onError?.invoke(e) ?: handleException(e)
            } finally {
                _loadingState.value = false
            }
        }
    }
    
    // ============================================
    // Error Handling
    // ============================================
    
    protected fun handleException(exception: Throwable) {
        val errorMessage = when (exception) {
            is IllegalArgumentException -> "Invalid input: ${exception.message}"
            is SecurityException -> "Permission denied: ${exception.message}"
            is Exception -> exception.message ?: "Unknown error occurred"
            else -> "An unexpected error occurred"
        }
        
        logError(errorMessage, exception)
        showError(errorMessage)
        onErrorOccurred(exception)
    }
    
    /**
     * Override to handle specific errors
     */
    protected open fun onErrorOccurred(exception: Throwable) {}
    
    // ============================================
    // Logging & Analytics
    // ============================================
    
    private fun logStateChange() {
        val tag = this::class.simpleName ?: "ViewModel"
        Log.d(tag, "State updated: ${_state.value}")
    }
    
    private fun logError(message: String, exception: Throwable) {
        val tag = this::class.simpleName ?: "ViewModel"
        Log.e(tag, message, exception)
        trackAnalyticsEvent("error", mapOf(
            "message" to message,
            "exception" to (exception.javaClass.simpleName ?: "Unknown")
        ))
    }
    
    /**
     * Track analytics event
     * Override in subclass to implement actual analytics
     */
    protected open fun trackAnalyticsEvent(
        eventName: String,
        params: Map<String, Any> = emptyMap()
    ) {
        val tag = this::class.simpleName ?: "Analytics"
        Log.d(tag, "Event: $eventName, Params: $params")
    }
    
    // ============================================
    // Lifecycle Management
    // ============================================
    
    init {
        onInitialize()
    }
    
    override fun onCleared() {
        super.onCleared()
        Log.d(this::class.simpleName ?: "ViewModel", "ViewModel cleared")
    }
}

/**
 * Simplified base ViewModel for single state management
 */
abstract class SimplifiedViewModel<State : Any> : ViewModel() {
    
    protected val _state = MutableStateFlow<State>(getInitialState())
    val state: StateFlow<State> = _state.asStateFlow()
    
    protected val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    protected val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    protected abstract fun getInitialState(): State
    
    protected fun setState(newState: State) {
        _state.value = newState
    }
    
    protected fun setLoading(loading: Boolean) {
        _loading.value = loading
    }
    
    protected fun setError(error: String?) {
        _error.value = error
    }
    
    protected fun <T> execute(
        block: suspend () -> T,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit = { setError(it) }
    ) {
        viewModelScope.launch {
            try {
                setLoading(true)
                onSuccess(block())
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            } finally {
                setLoading(false)
            }
        }
    }
}
