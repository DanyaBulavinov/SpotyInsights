package com.daniel.spotyinsights.auth.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyAuthResponse(
    @Json(name = "access_token")
    val accessToken: String,
    
    @Json(name = "token_type")
    val tokenType: String,
    
    @Json(name = "expires_in")
    val expiresIn: Int,
    
    @Json(name = "refresh_token")
    val refreshToken: String?,
    
    @Json(name = "scope")
    val scope: String
)

@JsonClass(generateAdapter = true)
data class SpotifyAuthError(
    @Json(name = "error")
    val error: String,
    
    @Json(name = "error_description")
    val errorDescription: String
) 