package com.daniel.spotyinsights.data.network.model.track

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyTrackResponse(
    @Json(name = "items")
    val items: List<SpotifyTrack>,
    @Json(name = "total")
    val total: Int,
    @Json(name = "limit")
    val limit: Int,
    @Json(name = "offset")
    val offset: Int
)

@JsonClass(generateAdapter = true)
data class SpotifyTrack(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "duration_ms")
    val durationMs: Long,
    @Json(name = "popularity")
    val popularity: Int,
    @Json(name = "preview_url")
    val previewUrl: String?,
    @Json(name = "external_urls")
    val externalUrls: Map<String, String>,
    @Json(name = "explicit")
    val explicit: Boolean,
    @Json(name = "album")
    val album: SpotifyAlbum,
    @Json(name = "artists")
    val artists: List<SpotifyTrackArtist>
)

@JsonClass(generateAdapter = true)
data class SpotifyAlbum(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "release_date")
    val releaseDate: String,
    @Json(name = "images")
    val images: List<SpotifyImage>,
    @Json(name = "external_urls")
    val externalUrls: Map<String, String>
)

@JsonClass(generateAdapter = true)
data class SpotifyTrackArtist(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "external_urls")
    val externalUrls: Map<String, String>
)

@JsonClass(generateAdapter = true)
data class SpotifyImage(
    @Json(name = "url")
    val url: String,
    @Json(name = "height")
    val height: Int?,
    @Json(name = "width")
    val width: Int?
)