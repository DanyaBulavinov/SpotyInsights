package com.daniel.spotyinsights.data.network

object SpotifyApiConfig {
    const val BASE_URL = "https://api.spotify.com/v1/"
    const val AUTH_URL = "https://accounts.spotify.com/api/"
    
    // Scopes for Spotify API access
    val REQUIRED_SCOPES = arrayOf(
        "user-read-private",
        "user-read-email",
        "user-top-read",
        "user-library-read",
        "playlist-read-private"
    ).joinToString(" ")
}