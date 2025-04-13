package com.daniel.spotyinsights.data.repository

import com.daniel.spotyinsights.data.local.dao.TrackDao
import com.daniel.spotyinsights.data.local.dao.TrackWithRelations
import com.daniel.spotyinsights.data.local.entity.TrackArtistCrossRef
import com.daniel.spotyinsights.data.local.entity.TrackEntity
import com.daniel.spotyinsights.data.local.entity.AlbumEntity
import com.daniel.spotyinsights.data.local.entity.TrackArtistEntity
import com.daniel.spotyinsights.data.network.api.SpotifyApiService
import com.daniel.spotyinsights.domain.model.*
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.repository.TopTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class TopTracksRepositoryImpl @Inject constructor(
    private val spotifyApiService: SpotifyApiService,
    private val trackDao: TrackDao
) : TopTracksRepository {

    override fun getTopTracks(timeRange: TimeRange): Flow<Result<List<Track>>> {
        val minFetchTimeMs = System.currentTimeMillis() - CACHE_DURATION_MS
        return trackDao.getTopTracks(minFetchTimeMs)
            .map { tracks ->
                Result.Success(tracks.map { it.toDomainModel() })
            }
    }

    override suspend fun refreshTopTracks(timeRange: TimeRange): Result<Unit> {
        return try {
            val response = spotifyApiService.getTopTracks(
                timeRange = timeRange.toApiValue(),
                limit = 50
            )

            val currentTimeMs = System.currentTimeMillis()
            val tracks = mutableListOf<TrackEntity>()
            val artists = mutableListOf<TrackArtistEntity>()
            val albums = mutableListOf<AlbumEntity>()
            val trackArtistRefs = mutableListOf<TrackArtistCrossRef>()

            response.items.forEach { spotifyTrack ->
                tracks.add(spotifyTrack.toTrackEntity(currentTimeMs))
                artists.addAll(spotifyTrack.artists.map { it.toTrackArtistEntity() })
                albums.add(spotifyTrack.album.toAlbumEntity())
                trackArtistRefs.addAll(
                    spotifyTrack.artists.map { artist ->
                        TrackArtistCrossRef(spotifyTrack.id, artist.id)
                    }
                )
            }

            trackDao.insertTracksWithRelations(
                tracks = tracks,
                artists = artists.distinctBy { it.id },
                albums = albums.distinctBy { it.id },
                trackArtistCrossRefs = trackArtistRefs
            )

            // Clean up old data
            trackDao.deleteOldTracks(currentTimeMs - CACHE_DURATION_MS)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun TimeRange.toApiValue(): String = when (this) {
        TimeRange.SHORT_TERM -> "short_term"
        TimeRange.MEDIUM_TERM -> "medium_term"
        TimeRange.LONG_TERM -> "long_term"
    }

    private fun TrackWithRelations.toDomainModel() = Track(
        id = track.id,
        name = track.name,
        artists = artists.map { artist ->
            TrackArtist(
                id = artist.id,
                name = artist.name,
                spotifyUrl = artist.spotifyUrl
            )
        },
        album = Album(
            id = album.id,
            name = album.name,
            releaseDate = album.releaseDate,
            imageUrl = album.imageUrl,
            spotifyUrl = album.spotifyUrl
        ),
        durationMs = track.durationMs,
        popularity = track.popularity,
        previewUrl = track.previewUrl,
        spotifyUrl = track.spotifyUrl,
        explicit = track.explicit
    )

    companion object {
        private val CACHE_DURATION_MS = TimeUnit.HOURS.toMillis(24) // Cache for 24 hours
    }
}