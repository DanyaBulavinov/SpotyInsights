package com.daniel.spotyinsights.data.repository

import com.daniel.spotyinsights.data.network.api.SpotifyApiService
import com.daniel.spotyinsights.domain.model.Track
import com.daniel.spotyinsights.domain.repository.TrackRepository
import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor(
    private val api: SpotifyApiService
) : TrackRepository {
    override suspend fun getTrackById(id: String): Track {
        val networkTrack = api.getTrackById(id)
        return Track(
            id = networkTrack.id,
            name = networkTrack.name,
            artists = networkTrack.artists.map {
                com.daniel.spotyinsights.domain.model.TrackArtist(
                    id = it.id,
                    name = it.name,
                    spotifyUrl = it.externalUrls["spotify"] ?: ""
                )
            },
            album = com.daniel.spotyinsights.domain.model.Album(
                id = networkTrack.album.id,
                name = networkTrack.album.name,
                releaseDate = networkTrack.album.releaseDate,
                imageUrl = networkTrack.album.images.firstOrNull()?.url ?: "",
                spotifyUrl = networkTrack.album.externalUrls["spotify"] ?: ""
            ),
            durationMs = networkTrack.durationMs,
            popularity = networkTrack.popularity,
            previewUrl = networkTrack.previewUrl,
            spotifyUrl = networkTrack.externalUrls["spotify"] ?: "",
            explicit = networkTrack.explicit
        )
    }

    override suspend fun getArtistTopTracks(artistId: String): List<Track> {
        val response = api.getArtistTopTracks(artistId)
        return response.tracks.map { networkTrack ->
            Track(
                id = networkTrack.id,
                name = networkTrack.name,
                artists = networkTrack.artists.map {
                    com.daniel.spotyinsights.domain.model.TrackArtist(
                        id = it.id,
                        name = it.name,
                        spotifyUrl = it.externalUrls["spotify"] ?: ""
                    )
                },
                album = com.daniel.spotyinsights.domain.model.Album(
                    id = networkTrack.album.id,
                    name = networkTrack.album.name,
                    releaseDate = networkTrack.album.releaseDate,
                    imageUrl = networkTrack.album.images.firstOrNull()?.url ?: "",
                    spotifyUrl = networkTrack.album.externalUrls["spotify"] ?: ""
                ),
                durationMs = networkTrack.durationMs,
                popularity = networkTrack.popularity,
                previewUrl = networkTrack.previewUrl,
                spotifyUrl = networkTrack.externalUrls["spotify"] ?: "",
                explicit = networkTrack.explicit
            )
        }
    }
} 