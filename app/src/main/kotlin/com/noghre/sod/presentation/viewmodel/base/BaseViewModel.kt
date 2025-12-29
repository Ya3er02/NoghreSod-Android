package com.noghre.sod.presentation.viewmodel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Abstract base ViewModel implementing the Model-View-Intent (MVI) architectural pattern.
 *
 * MVI provides:
 * - Unidirectional data flow (Intent → Reducer → State → UI)
 * - Single source of truth (StateFlow)
 * - One-time events via Effects (SharedFlow)
 * - Lifecycle-aware coroutine management
 *
 * Generic types:
 * @param State The UI state type (sealed interface or data class)
 * @param Intent User interactions (sealed interface)
 * @param Effect One-time side effects (sealed class)
 *
 * Usage:
 * ```kotlin
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *     private val useCase: MyUseCase
 * ) : BaseViewModel<MyState, MyIntent, MyEffect>() {
 *     override fun createInitialState() = MyState.Idle
 *     override fun handleIntent(intent: MyIntent) {
 *         when(intent) {
 *             is MyIntent.Action -> // handle action
 *         }
 *     }
 * }
 * ```
 *
 * @author NoghreSod Team
 * @version 1.0.0
 * @since Refactor Phase 2
 */
abstract class BaseViewModel<State : Any, Intent : Any, Effect : Any> : ViewModel() {

    // ========== State Management ==========

    /**
     * Lazy initialization of initial state.
     * Called once when first accessed.
     */
    private val _initialState: State by lazy { createInitialState() }

    /**
     * Mutable state flow for internal mutations
     */
    private val _uiState = MutableStateFlow(_initialState)

    /**
     * Public read-only state flow for UI observation
     */
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    /**
     * Current state accessor
     */
    val currentState: State
        get() = _uiState.value

    // ========== Effect Management ==========

    /**
     * Channel for emitting one-time effects
     * Using Channel instead of SharedFlow for guaranteed delivery of each effect
     */
    private val _effectChannel = Channel<Effect>(capacity = Channel.UNLIMITED)

    /**
     * Public read-only flow for observing effects
     * Each collector receives all emitted effects
     */
    val effectFlow: Flow<Effect> = _effectChannel.receiveAsFlow()

    // ========== Abstract Methods (Override in subclasses) ==========

    /**
     * Create the initial state for this ViewModel.
     * Called once, lazily on first access.
     *
     * @return Initial state instance
     */
    protected abstract fun createInitialState(): State

    /**
     * Process user intents and update state or emit effects.
     *
     * @param intent The user intent to process
     */
    protected abstract fun handleIntent(intent: Intent)

    // ========== Public Intent Entry Point ==========

    /**
     * Send a user intent to the ViewModel.
     * Delegates to handleIntent for processing.
     *
     * @param intent The intent to process
     */
    fun sendIntent(intent: Intent) {
        handleIntent(intent)
    }

    // ========== State Update Helpers ==========

    /**
     * Update state using a reducer function.
     * Thread-safe via StateFlow synchronization.
     *
     * Usage:
     * ```kotlin
     * setState { copy(isLoading = true) }
     * ```
     *
     * @param reduce Lambda that takes current state and returns new state
     */
    protected fun setState(reduce: State.() -> State) {
        _uiState.value = _uiState.value.reduce()
    }

    /**
     * Convenient state update for simple value replacements.
     * When you have a single new state value.
     *
     * @param newState The complete new state
     */
    protected fun setStateValue(newState: State) {
        _uiState.value = newState
    }

    /**
     * Update state conditionally based on current state.
     * Useful for checking state before updating.
     *
     * @param predicate Check function
     * @param reduce State update function
     */
    protected fun setStateIf(predicate: (State) -> Boolean, reduce: State.() -> State) {
        if (predicate(_uiState.value)) {
            setState(reduce)
        }
    }

    // ========== Effect Emission Helpers ==========

    /**
     * Emit a one-time effect to observers.
     * Non-blocking and safe to call from any thread.
     *
     * Usage:
     * ```kotlin
     * emitEffect(MyEffect.ShowError("Something went wrong"))
     * ```
     *
     * @param effect The effect to emit
     */
    protected fun emitEffect(effect: Effect) {
        viewModelScope.launch {
            _effectChannel.send(effect)
        }
    }

    /**
     * Emit effect built from a builder function.
     * Useful when effect construction requires computation.
     *
     * @param builder Lambda that produces the effect
     */
    protected fun emitEffect(builder: () -> Effect) {
        emitEffect(builder())
    }

    // ========== Lifecycle Management ==========

    /**
     * Clear all state and effects on ViewModel destruction.
     * Called automatically by framework.
     */
    override fun onCleared() {
        super.onCleared()
        _effectChannel.close()
    }
}
