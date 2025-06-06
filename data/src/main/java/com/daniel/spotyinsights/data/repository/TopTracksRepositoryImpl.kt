package com.daniel.spotyinsights.data.repository

import android.util.Log
import com.daniel.spotyinsights.data.local.dao.TrackDao
import com.daniel.spotyinsights.data.local.dao.TrackWithRelations
import com.daniel.spotyinsights.data.local.entity.AlbumEntity
import com.daniel.spotyinsights.data.local.entity.TrackArtistCrossRef
import com.daniel.spotyinsights.data.local.entity.TrackArtistEntity
import com.daniel.spotyinsights.data.local.entity.TrackEntity
import com.daniel.spotyinsights.data.network.api.SpotifyApiService
import com.daniel.spotyinsights.domain.model.Album
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.model.Track
import com.daniel.spotyinsights.domain.model.TrackArtist
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.repository.TopTracksRepository
import com.daniel.spotyinsights.domain.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopTracksRepositoryImpl @Inject constructor(
    private val spotifyApiService: SpotifyApiService,
    private val trackDao: TrackDao
) : TopTracksRepository {

    override fun getTopTracks(timeRange: TimeRange): Flow<Result<List<Track>>> {
        val minFetchTimeMs = System.currentTimeMillis() - CACHE_DURATION_MS
        return trackDao.getTopTracks(minFetchTimeMs)
            .onEach { tracks ->
                if (tracks.isEmpty()) {
                    Logger.i("No tracks found in database, triggering refresh")
                    refreshTopTracks(timeRange)
                }
            }
            .map { tracks ->
                Result.Success(tracks.map { it.toDomainModel() })
            }
    }

    override suspend fun refreshTopTracks(timeRange: TimeRange): Result<Unit> {
        return try {
            Log.d("TopTracksRepository", "Refreshing top tracks for time range: $timeRange")
            val response = spotifyApiService.getTopTracks(
                timeRange = timeRange.toApiValue(),
                limit = 50
            )
            Log.d(
                "TopTracksRepository",
                "API response received. Items count: ${response.items.size}"
            )

            if (response.items.isEmpty()) {
                Log.w("TopTracksRepository", "API returned empty tracks list")
                return Result.Success(Unit)
            }

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

            Log.d(
                "TopTracksRepository",
                "Inserting ${tracks.size} tracks with relations into database"
            )
            trackDao.insertTracksWithRelations(
                tracks = tracks,
                artists = artists.distinctBy { it.id },
                albums = albums.distinctBy { it.id },
                trackArtistCrossRefs = trackArtistRefs
            )
            Log.d("TopTracksRepository", "Tracks inserted successfully")

            // Clean up old data
            trackDao.deleteOldTracks(currentTimeMs - CACHE_DURATION_MS)
            Log.d("TopTracksRepository", "Old tracks cleaned up")

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("TopTracksRepository", "Error refreshing top tracks", e)
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