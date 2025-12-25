package com.noghre.sod.presentation.common

import java.util.UUID

/**
 * Base sealed interface for all UI events
 * 
 * One-time events that should not be replayed on rotation/recomposition
 * Uses unique IDs to track consumed events
 * 
 * @since 1.0.0
 */
sealed interface UiEvent {
    val id: String
        get() = UUID.randomUUID().toString()
    val timestamp: Long
        get() = System.currentTimeMillis()
    
    // ==================== Navigation Events ====================
    
    sealed interface Navigation : UiEvent {
        data class ToProductDetail(val productId: String) : Navigation
        data class ToCategory(val categoryId: String) : Navigation
        data class ToSearch(val query: String = "") : Navigation
        object ToCart : Navigation
        object ToCheckout : Navigation
        object ToProfile : Navigation
        object ToFavorites : Navigation
        data class ToUrl(val url: String) : Navigation
        object Back : Navigation
    }
    
    // ==================== Feedback Events ====================
    
    sealed interface Feedback : UiEvent {
        data class ShowSnackbar(
            val message: String,
            val action: SnackbarAction? = null,
            val duration: SnackbarDuration = SnackbarDuration.Short
        ) : Feedback
        
        data class ShowToast(
            val message: String,
            val isLong: Boolean = false
        ) : Feedback
        
        data class ShowDialog(
            val title: String,
            val message: String,
            val positiveButton: String? = null,
            val negativeButton: String? = null,
            val isCancelable: Boolean = true,
            val onPositive: (() -> Unit)? = null,
            val onNegative: (() -> Unit)? = null
        ) : Feedback
        
        data class ShowBottomSheet(
            val title: String,
            val items: List<BottomSheetItem>
        ) : Feedback
        
        object DismissDialog : Feedback
    }
    
    // ==================== Action Events ====================
    
    sealed interface Action : UiEvent {
        object RefreshData : Action
        data class CopyToClipboard(
            val text: String,
            val label: String = "Text"
        ) : Action
        data class ShareProduct(
            val productId: String,
            val productName: String,
            val productUrl: String
        ) : Action
        data class OpenUrl(val url: String) : Action
        data class CallPhone(val phoneNumber: String) : Action
        data class SendEmail(val email: String) : Action
    }
    
    // ==================== Error Events ====================
    
    sealed interface Error : UiEvent {
        data class NetworkError(
            val error: com.noghre.sod.domain.common.AppError.NetworkError,
            val canRetry: Boolean = true,
            val onRetry: (() -> Unit)? = null
        ) : Error
        
        data class ServerError(
            val error: com.noghre.sod.domain.common.AppError.ServerError,
            val canRetry: Boolean = false
        ) : Error
        
        data class ValidationError(
            val field: String,
            val message: String
        ) : Error
        
        data class AuthError(
            val error: com.noghre.sod.domain.common.AppError.AuthError,
            val onRetry: (() -> Unit)? = null
        ) : Error
    }
    
    // ==================== Progress Events ====================
    
    sealed interface Progress : UiEvent {
        data class ShowProgress(
            val message: String = "درحال بارگذاری..."
        ) : Progress
        
        data class UpdateProgress(
            val percentage: Int
        ) : Progress
        
        object DismissProgress : Progress
    }
    
    // ==================== Analytics Events ====================
    
    sealed interface Analytics : UiEvent {
        data class TrackEvent(
            val eventName: String,
            val parameters: Map<String, Any> = emptyMap()
        ) : Analytics
        
        data class TrackScreenView(
            val screenName: String
        ) : Analytics
    }
}

/**
 * Snackbar action configuration
 */
data class SnackbarAction(
    val label: String,
    val action: suspend () -> Unit
)

/**
 * Snackbar duration options
 */
enum class SnackbarDuration {
    Short, Long, Indefinite
}

/**
 * Bottom sheet item
 */
data class BottomSheetItem(
    val id: String,
    val title: String,
    val icon: Int? = null,
    val onClick: () -> Unit
)

/**
 * Event handler with consumption tracking
 * Prevents multiple emissions of same event
 */
class EventHandler<T : UiEvent> {
    private val _events = kotlinx.coroutines.channels.Channel<T>(kotlinx.coroutines.channels.Channel.BUFFERED)
    val events: kotlinx.coroutines.flow.Flow<T> = _events.receiveAsFlow()
    
    private val consumedEvents = mutableSetOf<String>()
    
    suspend fun send(event: T) {
        _events.send(event)
    }
    
    fun trySend(event: T) {
        _events.trySend(event)
    }
    
    fun markConsumed(eventId: String) {
        consumedEvents.add(eventId)
    }
    
    fun isConsumed(eventId: String): Boolean {
        return consumedEvents.contains(eventId)
    }
}
