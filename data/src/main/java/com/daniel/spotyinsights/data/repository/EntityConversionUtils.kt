package com.daniel.spotyinsights.data.repository

import com.daniel.spotyinsights.data.local.entity.AlbumEntity
import com.daniel.spotyinsights.data.local.entity.DetailedArtistEntity
import com.daniel.spotyinsights.data.local.entity.GenreEntity
import com.daniel.spotyinsights.data.local.entity.ImageEntity
import com.daniel.spotyinsights.data.local.entity.TrackArtistEntity
import com.daniel.spotyinsights.data.local.entity.TrackEntity
import com.daniel.spotyinsights.data.network.model.artist.SpotifyArtist
import com.daniel.spotyinsights.data.network.model.track.SpotifyAlbum
import com.daniel.spotyinsights.data.network.model.track.SpotifyTrack
import com.daniel.spotyinsights.data.network.model.track.SpotifyTrackArtist

internal fun SpotifyTrack.toTrackEntity(fetchTimeMs: Long) = TrackEntity(
    id = id,
    name = name,
    durationMs = durationMs,
    popularity = popularity,
    previewUrl = previewUrl,
    spotifyUrl = externalUrls["spotify"] ?: "",
    explicit = explicit,
    albumId = album.id,
    fetchTimeMs = fetchTimeMs
)

internal fun SpotifyAlbum.toAlbumEntity() = AlbumEntity(
    id = id,
    name = name,
    releaseDate = releaseDate,
    imageUrl = images.firstOrNull()?.url ?: "",
    spotifyUrl = externalUrls["spotify"] ?: ""
)

internal fun SpotifyTrackArtist.toTrackArtistEntity() = TrackArtistEntity(
    id = id,
    name = name,
    spotifyUrl = externalUrls["spotify"] ?: ""
)
