package com.daniel.spotyinsights.data.network.model.artist

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyArtistResponse(
    @Json(name = "items")
    val items: List<SpotifyArtist>,
    @Json(name = "total")
    val total: Int,
    @Json(name = "limit")
    val limit: Int,
    @Json(name = "offset")
    val offset: Int
)

@JsonClass(generateAdapter = true)
data class SpotifyArtist(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "external_urls")
    val externalUrls: Map<String, String>,
    @Json(name = "genres")
    val genres: List<String>,
    @Json(name = "images")
    val images: List<SpotifyImageResponse>,
    @Json(name = "popularity")
    val popularity: Int,
    @Json(name = "followers")
    val followers: SpotifyFollowersResponse? = null
)

@JsonClass(generateAdapter = true)
data class SpotifyImageResponse(
    @Json(name = "url")
    val url: String,
    @Json(name = "height")
    val height: Int?,
    @Json(name = "width")
    val width: Int?
)

@JsonClass(generateAdapter = true)
data class SpotifyFollowersResponse(
    @Json(name = "total")
    val total: Int
) 