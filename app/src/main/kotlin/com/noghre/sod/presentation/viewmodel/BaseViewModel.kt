package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Base ViewModel for all screens.
 * Provides common functionality: error handling, loading state, lifecycle management.
 * 
 * Features:
 * - Centralized error handling
 * - Automatic job cancellation
 * - Shared error events
 * - Loading state management
 * - Memory leak prevention
 * 
 * @author Yaser
 * @version 1.0.0
 */
abstract class BaseViewModel : ViewModel() {
    
    // Error event stream for UI to consume
    private val _errorEvents = MutableSharedFlow<ErrorEvent>()
    val errorEvents: SharedFlow<ErrorEvent> = _errorEvents
    
    // Job tracker for lifecycle management
    private var currentJob: Job? = null
    
    // Exception handler with logging
    protected val exceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        Timber.e(exception, "Coroutine exception in ${this::class.simpleName}")
        emitError(exception)
    }
    
    /**
     * Launch a coroutine with built-in error handling.
     */
    protected fun launchSafe(block: suspend () -> Unit) {
        currentJob?.cancel() // Cancel previous job if still running
        currentJob = viewModelScope.launch(exceptionHandler) {
            block()
        }
    }
    
    /**
     * Emit error event to UI.
     */
    protected fun emitError(exception: Throwable) {
        viewModelScope.launch {
            val message = when (exception) {
                is TimeoutException -> "درخواست به مهلت زمانی رسید"
                is NetworkException -> "خطای شبکه"
                is ValidationException -> exception.message ?: "خطای اعتبار‌سنجی"
                else -> exception.localizedMessage ?: "خطای نامشخص"
            }
            _errorEvents.emit(ErrorEvent(message, exception))
        }
    }
    
    /**
     * Emit error with custom message.
     */
    protected fun emitError(message: String, exception: Exception? = null) {
        viewModelScope.launch {
            _errorEvents.emit(ErrorEvent(message, exception))
        }
    }
    
    /**
     * Called when ViewModel is no longer used.
     * Automatically cancels all jobs.
     */
    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
        Timber.d("${this::class.simpleName} cleared")
    }
    
    /**
     * Error event data class.
     */
    data class ErrorEvent(
        val message: String,
        val exception: Throwable? = null
    )
}

// Custom exception types
class TimeoutException(message: String = "Request timeout") : Exception(message)
class NetworkException(message: String = "Network error") : Exception(message)
class ValidationException(message: String = "Validation error") : Exception(message)
