package com.daniel.spotyinsights.domain.model

data class NewRelease(
    val id: String,
    val name: String,
    val albumType: String,
    val totalTracks: Int,
    val artists: List<NewReleaseArtist>,
    val imageUrl: String,
    val releaseDate: String,
    val spotifyUrl: String
)

data class NewReleaseArtist(
    val id: String,
    val name: String,
    val spotifyUrl: String
) 