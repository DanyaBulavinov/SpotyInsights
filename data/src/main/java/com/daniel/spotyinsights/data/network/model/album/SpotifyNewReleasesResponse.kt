package com.daniel.spotyinsights.data.network.model.album

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyNewReleasesResponse(
    @Json(name = "albums")
    val albums: SpotifyAlbumsObjectResponse
)

@JsonClass(generateAdapter = true)
data class SpotifyAlbumsObjectResponse(
    @Json(name = "href")
    val href: String,
    @Json(name = "limit")
    val limit: Int,
    @Json(name = "next")
    val next: String?,
    @Json(name = "offset")
    val offset: Int,
    @Json(name = "previous")
    val previous: String?,
    @Json(name = "total")
    val total: Int,
    @Json(name = "items")
    val items: List<SpotifyNewReleaseAlbum>
)

@JsonClass(generateAdapter = true)
data class SpotifyNewReleaseAlbum(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "album_type")
    val albumType: String,
    @Json(name = "total_tracks")
    val totalTracks: Int,
    @Json(name = "available_markets")
    val availableMarkets: List<String>,
    @Json(name = "external_urls")
    val externalUrls: Map<String, String>,
    @Json(name = "href")
    val href: String,
    @Json(name = "images")
    val images: List<SpotifyImageReleaseResponse>,
    @Json(name = "release_date")
    val releaseDate: String,
    @Json(name = "release_date_precision")
    val releaseDatePrecision: String,
    @Json(name = "restrictions")
    val restrictions: SpotifyAlbumRestrictions?,
    @Json(name = "type")
    val type: String,
    @Json(name = "uri")
    val uri: String,
    @Json(name = "artists")
    val artists: List<SpotifyArtistReleaseResponse>
)

@JsonClass(generateAdapter = true)
data class SpotifyArtistReleaseResponse(
    @Json(name = "external_urls")
    val externalUrls: Map<String, String>,
    @Json(name = "href")
    val href: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "uri")
    val uri: String
)

@JsonClass(generateAdapter = true)
data class SpotifyImageReleaseResponse(
    @Json(name = "url")
    val url: String,
    @Json(name = "height")
    val height: Int?,
    @Json(name = "width")
    val width: Int?
)

@JsonClass(generateAdapter = true)
data class SpotifyAlbumRestrictions(
    @Json(name = "reason")
    val reason: String
)