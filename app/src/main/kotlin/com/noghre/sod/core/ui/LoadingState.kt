package com.noghre.sod.core.ui

/**
 * Sealed class representing different loading states.
 *
 * Usage:
 * ```
 * val state = mutableStateOf<LoadingState>(LoadingState.Idle)
 *
 * when (state.value) {
 *     LoadingState.Idle -> { /* Show normal state */ }
 *     LoadingState.Loading -> { /* Show progress */ }
 *     LoadingState.Loaded -> { /* Show content */ }
 *     is LoadingState.LoadingMore -> { /* Append items */ }
 * }
 * ```
 *
 * @author NoghreSod Team
 * @version 1.0.0
 */
sealed class LoadingState {
    /**
     * Initial state before any loading.
     */
    object Idle : LoadingState()

    /**
     * Data is currently being loaded.
     */
    object Loading : LoadingState()

    /**
     * Initial data loaded successfully.
     */
    object Loaded : LoadingState()

    /**
     * Loading more data (pagination).
     */
    data class LoadingMore(
        val currentPage: Int = 1
    ) : LoadingState()

    /**
     * All data loaded (no more pages available).
     */
    object AllLoaded : LoadingState()

    /**
     * Refreshing data.
     */
    object Refreshing : LoadingState()
}

/**
 * Extension to check if state represents any loading.
 */
val LoadingState.isLoading: Boolean
    get() = this is LoadingState.Loading || this is LoadingState.LoadingMore

/**
 * Extension to check if state allows showing content.
 */
val LoadingState.showContent: Boolean
    get() = this is LoadingState.Loaded || this is LoadingState.LoadingMore || this is LoadingState.AllLoaded
