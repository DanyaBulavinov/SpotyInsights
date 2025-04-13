package com.daniel.spotyinsights.auth

import com.daniel.spotyinsights.auth.data.AuthDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

enum class AuthenticationState {
    AUTHENTICATED,
    UNAUTHENTICATED
}

@Singleton
class AuthStateHolder @Inject constructor(
    private val authDataStore: AuthDataStore
) {
    val authState: Flow<AuthenticationState> = authDataStore.accessToken.map { token ->
        if (token != null) AuthenticationState.AUTHENTICATED
        else AuthenticationState.UNAUTHENTICATED
    }

    fun logout() {
        runBlocking {
            authDataStore.clearTokens()
        }
    }
} 