package com.daniel.spotyinsights.domain.model

data class Track(
    val id: String,
    val name: String,
    val artists: List<TrackArtist>,
    val album: Album,
    val durationMs: Long,
    val popularity: Int,
    val previewUrl: String?,
    val spotifyUrl: String,
    val explicit: Boolean
)

data class TrackArtist(
    val id: String,
    val name: String,
    val spotifyUrl: String
)

data class Album(
    val id: String,
    val name: String,
    val releaseDate: String,
    val imageUrl: String,
    val spotifyUrl: String
)
