package com.daniel.spotyinsights.domain.repository

import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    val accessToken: Flow<String?>
    suspend fun clearTokens()
    suspend fun isTokenExpired(): Boolean
} 