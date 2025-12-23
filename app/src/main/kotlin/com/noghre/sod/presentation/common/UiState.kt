package com.noghre.sod.presentation.common

import com.noghre.sod.core.result.AppError

/**
 * Sealed class representing UI state for loading, displaying, and error handling.
 * Used throughout the presentation layer to communicate between ViewModels and Compose screens.
 *
 * States:
 * - Idle: Initial state, no data loaded yet
 * - Loading: Data is being loaded; may have cached data
 * - Success: Data loaded successfully
 * - Error: Loading failed; may have cached data as fallback
 *
 * @param T Type of data being managed
 */
sealed class UiState<out T> {

    /**
     * Initial state before any data loading attempt.
     */
    object Idle : UiState<Nothing>()

    /**
     * Data is loading from network or database.
     * @param data Cached data if available, null otherwise
     */
    data class Loading<T>(
        val data: T? = null
    ) : UiState<T>()

    /**
     * Data loaded successfully.
     * @param data The loaded data
     */
    data class Success<T>(
        val data: T
    ) : UiState<T>()

    /**
     * An error occurred while loading.
     * @param error The error that occurred
     * @param data Cached data if available as fallback, null otherwise
     */
    data class Error<T>(
        val error: AppError,
        val data: T? = null
    ) : UiState<T>()
}

/**
 * Extension function to get the current data, regardless of state.
 * Returns data if available in Success or cached data in Error/Loading.
 * Returns null for Idle state.
 */
fun <T> UiState<T>.getDataOrNull(): T? = when (this) {
    is UiState.Success -> data
    is UiState.Loading -> data
    is UiState.Error -> data
    is UiState.Idle -> null
}

/**
 * Extension function to check if state is loading.
 */
fun UiState<*>.isLoading(): Boolean = this is UiState.Loading

/**
 * Extension function to check if state is success.
 */
fun UiState<*>.isSuccess(): Boolean = this is UiState.Success

/**
 * Extension function to check if state is error.
 */
fun UiState<*>.isError(): Boolean = this is UiState.Error

/**
 * Extension function to check if state has data available.
 * Returns true for Success, Loading with data, and Error with data.
 */
fun <T> UiState<T>.hasData(): Boolean = getDataOrNull() != null

/**
 * Extension function to map UiState data.
 * Useful for transforming data without changing state.
 */
fun <T, R> UiState<T>.mapData(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Success -> UiState.Success(transform(data))
    is UiState.Loading -> UiState.Loading(data?.let { transform(it) })
    is UiState.Error -> UiState.Error(error, data?.let { transform(it) })
    is UiState.Idle -> UiState.Idle
}
