package com.daniel.spotyinsights.domain.model

data class DetailedArtist(
    val id: String,
    val name: String,
    val spotifyUrl: String,
    val genres: List<String>,
    val images: List<String>,
    val popularity: Int
)
