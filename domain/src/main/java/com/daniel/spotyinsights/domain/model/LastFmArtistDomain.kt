package com.daniel.spotyinsights.domain.model

data class LastFmArtistDomain(
    val name: String?,
)

data class LastFmTagDomain(
    val name: String?,
    val count: Int?,
    val url: String?
)

data class PlayCountPerDay(
    val date: String, // e.g. "2024-06-01"
    val count: Int
)