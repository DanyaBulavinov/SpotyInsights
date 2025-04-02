package com.daniel.spotyinsights.auth.repository

import android.net.Uri
import com.daniel.spotyinsights.auth.api.SpotifyAuthService
import com.daniel.spotyinsights.auth.model.SpotifyAuthConfig
import com.daniel.spotyinsights.auth.model.SpotifyAuthResponse
import com.daniel.spotyinsights.domain.model.Result
import java.util.Base64
import javax.inject.Inject

interface SpotifyAuthRepository {
    fun getAuthorizationUrl(): String
    suspend fun getAccessToken(code: String): Result<SpotifyAuthResponse>
    suspend fun refreshAccessToken(refreshToken: String): Result<SpotifyAuthResponse>
}

class SpotifyAuthRepositoryImpl @Inject constructor(
    private val authService: SpotifyAuthService,
    private val authConfig: SpotifyAuthConfig
) : SpotifyAuthRepository {

    override fun getAuthorizationUrl(): String {
        return Uri.parse("https://accounts.spotify.com/authorize")
            .buildUpon()
            .appendQueryParameter("client_id", authConfig.clientId)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", authConfig.redirectUri)
            .appendQueryParameter("scope", authConfig.scopes.joinToString(" "))
            .build()
            .toString()
    }

    override suspend fun getAccessToken(code: String): Result<SpotifyAuthResponse> {
        return try {
            val authorization = getBasicAuthHeader()
            val response = authService.getAccessToken(
                authorization = authorization,
                grantType = "authorization_code",
                code = code,
                redirectUri = authConfig.redirectUri
            )
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun refreshAccessToken(refreshToken: String): Result<SpotifyAuthResponse> {
        return try {
            val authorization = getBasicAuthHeader()
            val response = authService.refreshAccessToken(
                authorization = authorization,
                refreshToken = refreshToken
            )
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun getBasicAuthHeader(): String {
        val credentials = "${authConfig.clientId}:${authConfig.clientSecret}"
        val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray())
        return "Basic $encodedCredentials"
    }
} 