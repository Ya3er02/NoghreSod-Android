package com.noghre.sod.presentation.common

/**
 * One-time UI events that should not be replayed on configuration changes.
 * Use with StateFlow + Channel pattern.
 */
sealed interface UiEvent {
    /**
     * Show a snackbar message
     *
     * @property message The message to display
     */
    data class ShowSnackbar(val message: String) : UiEvent

    /**
     * Navigate to a specific route
     *
     * @property route The navigation route
     */
    data class Navigate(val route: String) : UiEvent

    /**
     * Navigate back to previous screen
     */
    object NavigateBack : UiEvent

    /**
     * Show a toast message
     *
     * @property message The message to display
     */
    data class ShowToast(val message: String) : UiEvent
}
