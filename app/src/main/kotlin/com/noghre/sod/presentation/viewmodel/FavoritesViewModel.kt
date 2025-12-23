package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.usecase.GetFavoritesUseCase
import com.noghre.sod.domain.usecase.RemoveFromFavoritesUseCase
import com.noghre.sod.presentation.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class FavoritesUiState(
    val favorites: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for favorites screen.
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadFavorites()
    }

    /**
     * Load favorites
     */
    fun loadFavorites() {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                val favorites = getFavoritesUseCase()
                updateState { copy(favorites = favorites, isLoading = false) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading favorites")
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Remove from favorites
     *
     * @param productId Product ID
     */
    fun removeFromFavorites(productId: String) {
        viewModelScope.launch {
            try {
                removeFromFavoritesUseCase(productId)
                updateState {
                    copy(favorites = favorites.filter { it.id != productId })
                }
                _events.send(UiEvent.ShowSnackbar("Removed from favorites"))
            } catch (e: Exception) {
                Timber.e(e, "Error removing from favorites")
                _events.send(UiEvent.ShowSnackbar("Error removing from favorites"))
            }
        }
    }

    /**
     * Retry loading
     */
    fun retry() {
        loadFavorites()
    }

    private fun updateState(block: FavoritesUiState.() -> FavoritesUiState) {
        _uiState.update(block)
    }
}
