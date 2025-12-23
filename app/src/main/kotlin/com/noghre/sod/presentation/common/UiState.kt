package com.noghre.sod.presentation.common

/**
 * Generic UI state wrapper for managing loading, success, and error states
 * in the presentation layer.
 *
 * @param T The data type held by this state
 */
sealed class UiState<out T> {
    /**
     * Initial state before any data is loaded
     */
    object Initial : UiState<Nothing>()

    /**
     * Loading state indicating data is being fetched
     */
    object Loading : UiState<Nothing>()

    /**
     * Success state with data
     *
     * @property data The loaded data
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Error state
     *
     * @property message User-friendly error message
     * @property throwable Optional throwable for debugging
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : UiState<Nothing>()

    /**
     * Empty state for collections
     */
    object Empty : UiState<Nothing>()
}
