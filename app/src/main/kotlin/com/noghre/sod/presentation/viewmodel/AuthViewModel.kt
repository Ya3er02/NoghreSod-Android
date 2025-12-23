package com.noghre.sod.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noghre.sod.domain.Result
import com.noghre.sod.domain.model.AuthToken
import com.noghre.sod.domain.usecase.auth.LoginParams
import com.noghre.sod.domain.usecase.auth.LoginUseCase
import com.noghre.sod.domain.usecase.auth.RegisterParams
import com.noghre.sod.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val authToken: AuthToken? = null,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = loginUseCase.execute(LoginParams(email, password))
            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        authToken = result.data,
                        isAuthenticated = true,
                        isLoading = false,
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.exception.message,
                        isLoading = false,
                    )
                }
                is Result.Loading -> {}
            }
        }
    }

    fun register(
        email: String,
        phone: String,
        password: String,
        firstName: String,
        lastName: String,
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = registerUseCase.execute(
                RegisterParams(email, phone, password, firstName, lastName)
            )
            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        authToken = result.data,
                        isAuthenticated = true,
                        isLoading = false,
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.exception.message,
                        isLoading = false,
                    )
                }
                is Result.Loading -> {}
            }
        }
    }
}
