package com.noghre.sod.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.data.local.preferences.PreferencesManager
import com.noghre.sod.presentation.screens.settings.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Settings Screen.
 * 
 * Handles app preferences, theme, language,
 * and notification settings.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _settingsUiState = MutableStateFlow(SettingsUiState())
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * Load all settings from preferences.
     */
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                Timber.d("Loading settings")

                val language = preferencesManager.getLanguage() ?: "fa"
                val theme = preferencesManager.getTheme() ?: "light"
                val notificationsEnabled = preferencesManager.getNotificationsEnabled() ?: true
                val cartNotificationsEnabled = preferencesManager.getCartNotificationsEnabled() ?: true
                val priceDropNotificationsEnabled = preferencesManager.getPriceDropNotificationsEnabled() ?: true

                _settingsUiState.value = SettingsUiState(
                    language = language,
                    theme = theme,
                    notificationsEnabled = notificationsEnabled,
                    cartNotificationsEnabled = cartNotificationsEnabled,
                    priceDropNotificationsEnabled = priceDropNotificationsEnabled
                )

                Timber.d("Settings loaded successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error loading settings")
                _error.value = e.localizedMessage ?: "خطای نامشخص رخ داد"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Set application language.
     */
    fun setLanguage(language: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                Timber.d("Setting language: $language")

                preferencesManager.setLanguage(language)

                _settingsUiState.value = _settingsUiState.value.copy(
                    language = language
                )

                Timber.d("Language set successfully")
                // TODO: Trigger app restart to apply language change
            } catch (e: Exception) {
                Timber.e(e, "Error setting language")
                _error.value = e.localizedMessage ?: "خطا در تعیین زبان"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Set application theme.
     */
    fun setTheme(theme: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                Timber.d("Setting theme: $theme")

                preferencesManager.setTheme(theme)

                _settingsUiState.value = _settingsUiState.value.copy(
                    theme = theme
                )

                Timber.d("Theme set successfully")
                // TODO: Apply theme change to UI
            } catch (e: Exception) {
                Timber.e(e, "Error setting theme")
                _error.value = e.localizedMessage ?: "خطا در تعیین زبان رنگ"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Set notifications enabled/disabled.
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                Timber.d("Setting notifications enabled: $enabled")

                preferencesManager.setNotificationsEnabled(enabled)

                _settingsUiState.value = _settingsUiState.value.copy(
                    notificationsEnabled = enabled
                )

                Timber.d("Notifications setting updated")
            } catch (e: Exception) {
                Timber.e(e, "Error setting notifications")
                _error.value = e.localizedMessage ?: "خطا در تغيیر تنظیمات"
            }
        }
    }

    /**
     * Set cart notifications enabled/disabled.
     */
    fun setCartNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                Timber.d("Setting cart notifications enabled: $enabled")

                preferencesManager.setCartNotificationsEnabled(enabled)

                _settingsUiState.value = _settingsUiState.value.copy(
                    cartNotificationsEnabled = enabled
                )

                Timber.d("Cart notifications setting updated")
            } catch (e: Exception) {
                Timber.e(e, "Error setting cart notifications")
                _error.value = e.localizedMessage ?: "خطا در تغيیر تنظیمات"
            }
        }
    }

    /**
     * Set price drop notifications enabled/disabled.
     */
    fun setPriceDropNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                Timber.d("Setting price drop notifications enabled: $enabled")

                preferencesManager.setPriceDropNotificationsEnabled(enabled)

                _settingsUiState.value = _settingsUiState.value.copy(
                    priceDropNotificationsEnabled = enabled
                )

                Timber.d("Price drop notifications setting updated")
            } catch (e: Exception) {
                Timber.e(e, "Error setting price drop notifications")
                _error.value = e.localizedMessage ?: "خطا در تغيیر تنظیمات"
            }
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _error.value = null
    }
}
