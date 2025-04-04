package com.daniel.spotyinsights.data.repository

import com.daniel.spotyinsights.data.local.dao.ArtistDao
import com.daniel.spotyinsights.data.local.entity.ArtistEntity
import com.daniel.spotyinsights.data.network.api.SpotifyApiService
import com.daniel.spotyinsights.data.network.model.artist.SpotifyArtist
import com.daniel.spotyinsights.domain.model.Artist
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.repository.TopArtistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopArtistsRepositoryImpl @Inject constructor(
    private val spotifyApiService: SpotifyApiService,
    private val artistDao: ArtistDao
) : TopArtistsRepository {

    override fun getTopArtists(timeRange: TimeRange): Flow<Result<List<Artist>>> {
        val minFetchTimeMs = System.currentTimeMillis() - CACHE_DURATION_MS
        return artistDao.getTopArtists(minFetchTimeMs)
            .map { artists ->
                Result.Success(artists.map { it.toDomainModel() })
            }
    }

    override suspend fun refreshTopArtists(timeRange: TimeRange): Result<Unit> {
        return try {
            val response = spotifyApiService.getTopArtists(
                timeRange = timeRange.toApiValue(),
                limit = 50
            )

            val currentTimeMs = System.currentTimeMillis()
            val artists = response.items.map { it.toArtistEntity(currentTimeMs) }

            artistDao.insertArtists(artists)
            artistDao.deleteOldArtists(currentTimeMs - CACHE_DURATION_MS)

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

    private fun ArtistEntity.toDomainModel() = Artist(
        id = id,
        name = name,
        spotifyUrl = spotifyUrl
    )

    private fun SpotifyArtist.toArtistEntity(fetchTimeMs: Long) = ArtistEntity(
        id = id,
        name = name,
        spotifyUrl = externalUrls["spotify"] ?: "",
        fetchTimeMs = fetchTimeMs
    )

    companion object {
        private val CACHE_DURATION_MS = TimeUnit.HOURS.toMillis(24) // Cache for 24 hours
    }
} 