package com.daniel.spotyinsights.data.network.model.lastfm

import com.squareup.moshi.Json

data class LastFmArtistResponse(
    @Json(name = "artist") val artist: LastFmArtist?
)

data class LastFmArtist(
    @Json(name = "name") val name: String?,
    @Json(name = "mbid") val mbid: String?,
    @Json(name = "url") val url: String?,
    @Json(name = "bio") val bio: LastFmArtistBio?
)

data class LastFmArtistBio(
    @Json(name = "summary") val summary: String?,
    @Json(name = "content") val content: String?
) 