package com.daniel.spotyinsights.data.network.model.lastfm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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

data class LastFmUserTopTagsResponse(
    @Json(name = "toptags") val toptags: LastFmTopTags?
)

data class LastFmTopTags(
    @Json(name = "tag") val tag: List<LastFmTag>?
)

data class LastFmTag(
    @Json(name = "name") val name: String?,
    @Json(name = "count") val count: Int?,
    @Json(name = "url") val url: String?
)

@JsonClass(generateAdapter = true)
data class LastFmRecentTracksResponse(
    @Json(name = "recenttracks") val recenttracks: LastFmRecentTracks?
)

@JsonClass(generateAdapter = true)
data class LastFmRecentTracks(
    @Json(name = "track") val track: List<LastFmRecentTrack>?
)

@JsonClass(generateAdapter = true)
data class LastFmRecentTrack(
    @Json(name = "date") val date: LastFmRecentTrackDate?
)

@JsonClass(generateAdapter = true)
data class LastFmRecentTrackDate(
    @Json(name = "uts") val uts: String?
) 