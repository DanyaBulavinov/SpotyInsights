package com.daniel.spotyinsights.data.repository

import com.daniel.spotyinsights.data.local.dao.TrackDao
import com.daniel.spotyinsights.data.local.dao.TrackWithRelations
import com.daniel.spotyinsights.data.local.entity.TrackArtistCrossRef
import com.daniel.spotyinsights.data.local.entity.TrackEntity
import com.daniel.spotyinsights.data.local.entity.AlbumEntity
import com.daniel.spotyinsights.data.local.entity.TrackArtistEntity
import com.daniel.spotyinsights.data.network.api.SpotifyApiService
import com.daniel.spotyinsights.domain.model.*
import com.daniel.spotyinsights.domain.repository.RecommendationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map

@Singleton
class RecommendationsRepositoryImpl @Inject constructor(
    private val spotifyApiService: SpotifyApiService,
    private val trackDao: TrackDao
) : RecommendationsRepository {

    override fun getRecommendations(parameters: RecommendationParameters): Flow<Result<Recommendations>> {
        // Since recommendations are highly dynamic, we don't cache them
        // Instead, we trigger a refresh every time
        return trackDao.getTopTracks(Long.MAX_VALUE)
            .map { tracks ->
                if (tracks.isEmpty()) {
                    refreshRecommendations(parameters)
                }
                Result.Success(Recommendations(
                    tracks = tracks.map { it.toDomainModel() },
                    seeds = emptyList() // Seeds are not stored in DB
                ))
            }
    }

    override suspend fun getAvailableGenreSeeds(): Result<List<String>> {
        return try {
            val response = spotifyApiService.getAvailableGenreSeeds()
            Result.Success(response.genres)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun refreshRecommendations(parameters: RecommendationParameters): Result<Unit> {
        return try {
            val response = spotifyApiService.getRecommendations(
                seedArtists = parameters.seedArtists.takeIf { it.isNotEmpty() }?.joinToString(","),
                seedTracks = parameters.seedTracks.takeIf { it.isNotEmpty() }?.joinToString(","),
                seedGenres = parameters.seedGenres.takeIf { it.isNotEmpty() }?.joinToString(","),
                limit = parameters.limit,
                minPopularity = parameters.minPopularity,
                maxPopularity = parameters.maxPopularity,
                targetPopularity = parameters.targetPopularity
            )

            val currentTimeMs = System.currentTimeMillis()
            val tracks = mutableListOf<TrackEntity>()
            val artists = mutableListOf<TrackArtistEntity>()
            val albums = mutableListOf<AlbumEntity>()
            val trackArtistRefs = mutableListOf<TrackArtistCrossRef>()

            response.tracks.forEach { spotifyTrack ->
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

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
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
}