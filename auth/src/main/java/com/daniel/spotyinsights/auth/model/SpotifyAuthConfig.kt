package com.daniel.spotyinsights.auth.model

data class SpotifyAuthConfig(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val scopes: List<String> = listOf(
        "user-read-private",
        "user-read-email",
        "user-top-read",
        "user-read-recently-played",
        "user-library-read"
    )
) 