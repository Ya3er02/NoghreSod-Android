package com.noghre.sod.core.ui

/**
 * Base interface for UI events.
 *
 * UI events represent user interactions and system events
 * that need to be handled by the screen/UI layer.
 *
 * Usage:
 * ```
 * sealed class ProductListEvent : UiEvent {
 *     data class ProductClicked(val productId: String) : ProductListEvent()
 *     data class FilterApplied(val category: String) : ProductListEvent()
 *     object LoadMore : ProductListEvent()
 * }
 * ```
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
interface UiEvent

/**
 * One-time UI side effect/message.
 *
 * Used for events that should be consumed only once,
 * like showing a snackbar, navigation, or dialogs.
 *
 * Usage:
 * ```
 * sealed class UiEffect : UiMessage {
 *     data class ShowSnackbar(val message: String) : UiEffect()
 *     data class NavigateTo(val route: String) : UiEffect()
 *     data class ShowDialog(val title: String) : UiEffect()
 * }
 * ```
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
interface UiMessage

/**
 * Common UI messages/side effects.
 */
sealed class CommonUiMessage : UiMessage {
    data class ShowSnackbar(
        val message: String,
        val duration: Int = 3000
    ) : CommonUiMessage()

    data class ShowDialog(
        val title: String,
        val message: String,
        val positiveButtonText: String = "OK",
        val negativeButtonText: String? = null,
        val onPositive: (() -> Unit)? = null,
        val onNegative: (() -> Unit)? = null
    ) : CommonUiMessage()

    data class Navigate(
        val route: String,
        val clearBackStack: Boolean = false
    ) : CommonUiMessage()

    object PopBackStack : CommonUiMessage()

    object ShowLoading : CommonUiMessage()
    object HideLoading : CommonUiMessage()
}
