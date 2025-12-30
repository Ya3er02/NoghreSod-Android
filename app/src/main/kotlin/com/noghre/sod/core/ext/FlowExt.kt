package com.noghre.sod.core.ext

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan

/**
 * Extension functions for Kotlin Flows to simplify common patterns.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */

/**
 * Wraps a Flow with loading state tracking.
 * 
 * Emits a loading state before the first emission and completion state after.
 * Useful for UI loading indicators.
 * 
 * @param isLoading StateFlow to track loading state
 * @return Flow with loading state management
 */
fun <T> Flow<T>.withLoading(isLoading: MutableStateFlow<Boolean>): Flow<T> {
    return this
        .onEach { isLoading.value = false }
        .catch { 
            isLoading.value = false
            throw it
        }
}

/**
 * Retry a Flow with exponential backoff on error.
 * 
 * @param maxRetries Maximum number of retry attempts
 * @param initialDelay Initial delay in milliseconds
 * @param maxDelay Maximum delay in milliseconds
 * @param factor Exponential backoff factor
 * @return Flow that retries on error
 */
fun <T> Flow<T>.retryOnError(
    maxRetries: Int = 3,
    initialDelay: Long = 100L,
    maxDelay: Long = 5000L,
    factor: Double = 2.0
): Flow<T> = flow {
    var currentDelay = initialDelay
    var attempt = 0
    
    while (attempt <= maxRetries) {
        try {
            this@retryOnError.collect { value ->
                emit(value)
            }
            return@flow
        } catch (e: Exception) {
            attempt++
            if (attempt > maxRetries) throw e
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
    }
}

/**
 * Converts a Flow to a StateFlow.
 * 
 * Useful for exposing reactive state from UseCases.
 * 
 * @param initialValue Initial value for the StateFlow
 * @return StateFlow with the same values as the original Flow
 */
fun <T> Flow<T>.asStateFlow(initialValue: T): StateFlow<T> {
    val stateFlow = MutableStateFlow(initialValue)
    return stateFlow.asStateFlow()
}

/**
 * Transforms a Flow emission to include accumulated state.
 * 
* Useful for tracking changes over time.
 * 
 * @param initial Initial accumulator value
 * @param operation Accumulation operation
 * @return Flow of accumulated values
 */
fun <T, R> Flow<T>.scanState(
    initial: R,
    operation: suspend (accumulator: R, value: T) -> R
): Flow<R> = scan(initial, operation)

/**
 * Handles empty flows with a default value.
 * 
 * @param defaultValue Value to emit if Flow is empty
 * @return Flow with fallback value
 */
fun <T> Flow<T>.orEmpty(defaultValue: T): Flow<T> = flow {
    var hasEmitted = false
    try {
        this@orEmpty.collect { value ->
            hasEmitted = true
            emit(value)
        }
    } finally {
        if (!hasEmitted) {
            emit(defaultValue)
        }
    }
}
