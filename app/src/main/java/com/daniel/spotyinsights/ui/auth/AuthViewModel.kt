package com.daniel.spotyinsights.ui.auth

import androidx.lifecycle.viewModelScope
import com.daniel.spotyinsights.auth.data.AuthDataStore
import com.daniel.spotyinsights.auth.repository.SpotifyAuthRepository
import com.daniel.spotyinsights.base.BaseViewModel
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.util.Logger
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
        Logger.auth("Handling auth event: $event")
        when (event) {
            is AuthEvent.StartAuth -> startAuth()
            is AuthEvent.HandleAuthResponse -> handleAuthResponse(event.code)
            is AuthEvent.CheckAuth -> checkExistingAuth()
            is AuthEvent.Logout -> logout()
        }
    }

    private fun startAuth() {
        viewModelScope.launch {
            Logger.auth("Starting new auth flow")
            // Clear existing tokens before starting new auth flow
            authDataStore.clearTokens()
            Logger.auth("Cleared existing tokens")
            setState { AuthState.Initial }

            val authUrl = authRepository.getAuthorizationUrl()
            Logger.auth("Generated auth URL: $authUrl")
            setEffect { AuthEffect.OpenBrowser(authUrl) }
        }
    }

    private fun handleAuthResponse(code: String) {
        viewModelScope.launch {
            Logger.auth("Handling auth response with code: ${code.take(5)}...")
            setState { AuthState.Loading }

            when (val result = authRepository.getAccessToken(code)) {
                is Result.Success -> {
                    val response = result.data
                    Logger.auth("Successfully obtained access token")
                    authDataStore.saveTokens(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                        expiresIn = response.expiresIn
                    )
                    Logger.auth("Saved tokens to storage, expires in: ${response.expiresIn} seconds")
                    setState { AuthState.Authenticated(response.accessToken) }
                }

                is Result.Error -> {
                    val errorMessage = "Failed to get access token: ${result.exception.message}"
                    Logger.auth(errorMessage, result.exception)
                    setState { AuthState.Error("Authentication failed") }
                    setEffect { AuthEffect.ShowError(errorMessage) }
                }

                is Result.Loading -> {
                    setState { AuthState.Loading }
                }
            }
        }
    }

    private fun checkExistingAuth() {
        viewModelScope.launch {
            Logger.auth("Checking existing authentication")
            setState { AuthState.Loading }

            val accessToken = authDataStore.accessToken.first()
            val refreshToken = authDataStore.refreshToken.first()

            when {
                accessToken != null -> {
                    Logger.auth("Found valid access token")
                    setState { AuthState.Authenticated(accessToken) }
                }

                refreshToken != null -> {
                    Logger.auth("Access token expired, attempting to refresh with refresh token")
                    when (val result = authRepository.refreshAccessToken(refreshToken)) {
                        is Result.Success -> {
                            val response = result.data
                            Logger.auth("Successfully refreshed access token")
                            authDataStore.saveTokens(
                                accessToken = response.accessToken,
                                refreshToken = response.refreshToken ?: refreshToken,
                                expiresIn = response.expiresIn
                            )
                            setState { AuthState.Authenticated(response.accessToken) }
                        }

                        is Result.Error -> {
                            Logger.auth("Failed to refresh token", result.exception)
                            authDataStore.clearTokens()
                            setState { AuthState.Initial }
                        }

                        is Result.Loading -> {
                            setState { AuthState.Loading }
                        }
                    }
                }

                else -> {
                    Logger.auth("No existing tokens found")
                    setState { AuthState.Initial }
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            Logger.auth("Logging out user")
            authDataStore.clearTokens()
            setState { AuthState.Initial }
            Logger.auth("Logout complete")
        }
    }
} 