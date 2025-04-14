package com.daniel.spotyinsights.auth.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.daniel.spotyinsights.domain.repository.TokenRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

@Singleton
class AuthDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenRepository {

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val TOKEN_EXPIRY = longPreferencesKey("token_expiry")
    }

    override val accessToken: Flow<String?> = context.authDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN]
        }

    val refreshToken: Flow<String?> = context.authDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.REFRESH_TOKEN]
        }

    override suspend fun clearTokens() {
        context.authDataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.ACCESS_TOKEN)
            preferences.remove(PreferencesKeys.REFRESH_TOKEN)
            preferences.remove(PreferencesKeys.TOKEN_EXPIRY)
        }
    }

    override suspend fun isTokenExpired(): Boolean {
        val expiry = context.authDataStore.data.first()[PreferencesKeys.TOKEN_EXPIRY] ?: 0
        return System.currentTimeMillis() >= expiry
    }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String?,
        expiresIn: Int
    ) {
        context.authDataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = accessToken
            refreshToken?.let { preferences[PreferencesKeys.REFRESH_TOKEN] = it }
            preferences[PreferencesKeys.TOKEN_EXPIRY] = System.currentTimeMillis() + (expiresIn * 1000L)
        }
    }
} 