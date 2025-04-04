package com.daniel.spotyinsights.data.network.model.recommendations

import com.daniel.spotyinsights.data.network.model.track.SpotifyTrack
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyRecommendationsResponse(
    @Json(name = "tracks")
    val tracks: List<SpotifyTrack>,
    @Json(name = "seeds")
    val seeds: List<SpotifyRecommendationSeed>
)

@JsonClass(generateAdapter = true)
data class SpotifyRecommendationSeed(
    @Json(name = "id")
    val id: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "href")
    val href: String
)

@JsonClass(generateAdapter = true)
data class SpotifyAvailableGenreSeedsResponse(
    @Json(name = "genres")
    val genres: List<String>
) 