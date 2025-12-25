package com.noghre.sod.presentation.event

import com.noghre.sod.domain.model.error.AppError

/**
 * 🌟 UI Events Framework
 * 
 * One-time events for navigation, feedback, and actions.
 * Uses sealed interface hierarchy for type safety.
 * 
 * @since 1.0.0
 */
sealed interface UiEvent {
    
    // ==================== Navigation Events ====================
    
    sealed interface Navigation : UiEvent {
        data class ToProductDetail(val productId: String) : Navigation
        data class ToCategory(val categoryId: String) : Navigation
        data class ToSearch(val query: String = "") : Navigation
        object ToCart : Navigation
        object ToCheckout : Navigation
        object ToOrders : Navigation
        object ToProfile : Navigation
        object ToSettings : Navigation
        object Back : Navigation
        data class ToUrl(val url: String) : Navigation
    }
    
    // ==================== Feedback Events ====================
    
    sealed interface Feedback : UiEvent {
        data class ShowSnackbar(
            val message: String,
            val action: SnackbarAction? = null,
            val duration: SnackbarDuration = SnackbarDuration.Short
        ) : Feedback
        
        data class ShowToast(val message: String) : Feedback
        
        data class ShowDialog(
            val title: String,
            val message: String,
            val positiveButton: String? = null,
            val negativeButton: String? = null,
            val onPositive: (() -> Unit)? = null,
            val onNegative: (() -> Unit)? = null,
            val isDismissible: Boolean = true
        ) : Feedback
        
        data class ShowLoading(
            val message: String? = null
        ) : Feedback
        
        object DismissLoading : Feedback
    }
    
    // ==================== Action Events ====================
    
    sealed interface Action : UiEvent {
        object Retry : Action
        object RefreshData : Action
        data class CopyToClipboard(val text: String) : Action
        data class ShareProduct(val productId: String, val name: String) : Action
        data class RateProduct(val productId: String) : Action
        data class AddToWishlist(val productId: String) : Action
        data class RemoveFromWishlist(val productId: String) : Action
        object GoToCheckout : Action
        object SubmitOrder : Action
    }
    
    // ==================== Error Events ====================
    
    sealed interface Error : UiEvent {
        data class NetworkError(
            val error: AppError.NetworkError,
            val canRetry: Boolean = true
        ) : Error
        
        data class ServerError(
            val error: AppError.ServerError,
            val canRetry: Boolean = false
        ) : Error
        
        data class AuthError(
            val error: AppError.AuthError,
            val requiresLogin: Boolean = true
        ) : Error
        
        data class ValidationError(
            val field: String,
            val message: String
        ) : Error
        
        data class BusinessError(
            val error: AppError.BusinessError
        ) : Error
    }
    
    // ==================== Analytics Events ====================
    
    sealed interface Analytics : UiEvent {
        data class TrackScreenView(
            val screenName: String,
            val screenClass: String? = null,
            val parameters: Map<String, Any> = emptyMap()
        ) : Analytics
        
        data class TrackEvent(
            val eventName: String,
            val parameters: Map<String, Any> = emptyMap()
        ) : Analytics
        
        data class TrackException(
            val exception: Throwable,
            val fatal: Boolean = false
        ) : Analytics
    }
}

/**
 * Snackbar action configuration
 */
data class SnackbarAction(
    val label: String,
    val action: () -> Unit
)

/**
 * Snackbar duration
 */
enum class SnackbarDuration {
    Short, Long, Indefinite
}

/**
 * Event handler for managing one-time events
 * Prevents event replay on recomposition
 */
class EventHandler<T> {
    private val _events = kotlinx.coroutines.channels.Channel<T>(
        kotlinx.coroutines.channels.Channel.BUFFERED
    )
    val events: kotlinx.coroutines.flow.Flow<T> = _events.receiveAsFlow()
    
    suspend fun send(event: T) {
        _events.send(event)
    }
    
    fun trySend(event: T) {
        _events.trySend(event)
    }
}
