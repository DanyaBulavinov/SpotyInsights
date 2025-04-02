package com.daniel.spotyinsights.ui.auth

import androidx.lifecycle.viewModelScope
import com.daniel.spotyinsights.auth.data.AuthDataStore
import com.daniel.spotyinsights.auth.repository.SpotifyAuthRepository
import com.daniel.spotyinsights.base.BaseViewModel
import com.daniel.spotyinsights.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthState : com.daniel.spotyinsights.base.UiState {
    data object Initial : AuthState
    data object Loading : AuthState
    data class Authenticated(val accessToken: String) : AuthState
    data class Error(val message: String) : AuthState
}

sealed interface AuthEvent : com.daniel.spotyinsights.base.UiEvent {
    data object StartAuth : AuthEvent
    data class HandleAuthResponse(val code: String) : AuthEvent
    data object CheckAuth : AuthEvent
    data object Logout : AuthEvent
}

sealed interface AuthEffect : com.daniel.spotyinsights.base.UiEffect {
    data class OpenBrowser(val url: String) : AuthEffect
    data class ShowError(val message: String) : AuthEffect
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: SpotifyAuthRepository,
    private val authDataStore: AuthDataStore
) : BaseViewModel<AuthState, AuthEvent, AuthEffect>() {

    override fun createInitialState(): AuthState = AuthState.Initial

    override fun handleEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.StartAuth -> startAuth()
            is AuthEvent.HandleAuthResponse -> handleAuthResponse(event.code)
            is AuthEvent.CheckAuth -> checkExistingAuth()
            is AuthEvent.Logout -> logout()
        }
    }

    private fun startAuth() {
        val authUrl = authRepository.getAuthorizationUrl()
        setEffect { AuthEffect.OpenBrowser(authUrl) }
    }

    private fun handleAuthResponse(code: String) {
        viewModelScope.launch {
            setState { AuthState.Loading }
            
            when (val result = authRepository.getAccessToken(code)) {
                is Result.Success -> {
                    val response = result.data
                    authDataStore.saveTokens(response.accessToken, response.refreshToken)
                    setState { AuthState.Authenticated(response.accessToken) }
                }
                is Result.Error -> {
                    setState { AuthState.Error("Authentication failed") }
                    setEffect { AuthEffect.ShowError("Failed to get access token: ${result.exception.message}") }
                }
            }
        }
    }

    private fun checkExistingAuth() {
        viewModelScope.launch {
            setState { AuthState.Loading }
            
            val accessToken = authDataStore.accessToken.first()
            val refreshToken = authDataStore.refreshToken.first()

            if (accessToken != null) {
                setState { AuthState.Authenticated(accessToken) }
            } else if (refreshToken != null) {
                when (val result = authRepository.refreshAccessToken(refreshToken)) {
                    is Result.Success -> {
                        val response = result.data
                        authDataStore.saveTokens(response.accessToken, response.refreshToken ?: refreshToken)
                        setState { AuthState.Authenticated(response.accessToken) }
                    }
                    is Result.Error -> {
                        authDataStore.clearTokens()
                        setState { AuthState.Initial }
                    }
                }
            } else {
                setState { AuthState.Initial }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authDataStore.clearTokens()
            setState { AuthState.Initial }
        }
    }
} 