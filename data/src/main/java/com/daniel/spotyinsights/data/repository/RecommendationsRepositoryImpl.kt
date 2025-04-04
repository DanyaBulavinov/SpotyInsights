package com.daniel.spotyinsights.data.repository

import com.daniel.spotyinsights.data.local.dao.TrackDao
import com.daniel.spotyinsights.data.local.entity.TrackArtistCrossRef
import com.daniel.spotyinsights.data.network.api.SpotifyApiService
import com.daniel.spotyinsights.data.network.model.recommendations.SpotifyRecommendationSeed
import com.daniel.spotyinsights.domain.model.*
import com.daniel.spotyinsights.domain.repository.RecommendationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

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
            val artists = mutableListOf<ArtistEntity>()
            val albums = mutableListOf<AlbumEntity>()
            val trackArtistRefs = mutableListOf<TrackArtistCrossRef>()

            response.tracks.forEach { spotifyTrack ->
                tracks.add(spotifyTrack.toTrackEntity(currentTimeMs))
                artists.addAll(spotifyTrack.artists.map { it.toArtistEntity() })
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

    private fun SpotifyRecommendationSeed.toDomainModel() = RecommendationSeed(
        id = id,
        type = when (type.lowercase()) {
            "artist" -> SeedType.ARTIST
            "track" -> SeedType.TRACK
            "genre" -> SeedType.GENRE
            else -> throw IllegalArgumentException("Unknown seed type: $type")
        },
        href = href
    )
} 