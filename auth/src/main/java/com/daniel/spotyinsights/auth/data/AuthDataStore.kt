package com.daniel.spotyinsights.auth.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

@Singleton
class AuthDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val TOKEN_EXPIRATION_TIME = longPreferencesKey("token_expiration_time")
    }

    val accessToken: Flow<String?> = context.authDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN]
        }

    val refreshToken: Flow<String?> = context.authDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.REFRESH_TOKEN]
        }

    val tokenExpirationTime: Flow<Long?> = context.authDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.TOKEN_EXPIRATION_TIME]
        }

    suspend fun saveTokens(accessToken: String, refreshToken: String?, expiresIn: Int) {
        context.authDataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = accessToken
            refreshToken?.let {
                preferences[PreferencesKeys.REFRESH_TOKEN] = it
            }
            // Calculate expiration time in milliseconds
            val expirationTime = System.currentTimeMillis() + (expiresIn * 1000L)
            preferences[PreferencesKeys.TOKEN_EXPIRATION_TIME] = expirationTime
        }
    }

    suspend fun clearTokens() {
        context.authDataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.ACCESS_TOKEN)
            preferences.remove(PreferencesKeys.REFRESH_TOKEN)
            preferences.remove(PreferencesKeys.TOKEN_EXPIRATION_TIME)
        }
    }

    suspend fun isTokenExpired(): Boolean {
        val expirationTime = tokenExpirationTime.map { it ?: 0L }.collect { it }
        // Consider token expired 5 minutes before actual expiration to have a safety margin
        val safetyMarginMs = 5 * 60 * 1000L
        return System.currentTimeMillis() + safetyMarginMs >= expirationTime
    }
}