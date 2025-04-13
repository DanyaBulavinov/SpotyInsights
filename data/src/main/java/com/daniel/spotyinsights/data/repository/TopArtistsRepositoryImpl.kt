package com.daniel.spotyinsights.data.repository

import com.daniel.spotyinsights.data.local.dao.ArtistDao
import com.daniel.spotyinsights.data.local.dao.ArtistWithRelations
import com.daniel.spotyinsights.data.local.entity.DetailedArtistEntity
import com.daniel.spotyinsights.data.local.entity.GenreEntity
import com.daniel.spotyinsights.data.local.entity.ImageEntity
import com.daniel.spotyinsights.data.local.entity.ArtistGenreCrossRef
import com.daniel.spotyinsights.data.local.entity.ArtistImageCrossRef
import com.daniel.spotyinsights.data.network.api.SpotifyApiService
import com.daniel.spotyinsights.domain.model.DetailedArtist
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.repository.TopArtistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map

@Singleton
class TopArtistsRepositoryImpl @Inject constructor(
    private val spotifyApiService: SpotifyApiService,
    private val artistDao: ArtistDao
) : TopArtistsRepository {

    override fun getTopArtists(timeRange: TimeRange): Flow<Result<List<DetailedArtist>>> {
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
            val artists = mutableListOf<DetailedArtistEntity>()
            val genres = mutableListOf<GenreEntity>()
            val images = mutableListOf<ImageEntity>()
            val artistGenreCrossRefs = mutableListOf<ArtistGenreCrossRef>()
            val artistImageCrossRefs = mutableListOf<ArtistImageCrossRef>()

            response.items.forEach { spotifyArtist ->
                val (artistEntity, genreImagePair) = spotifyArtist.toDetailedArtistEntity(currentTimeMs)
                artists.add(artistEntity)
                genres.addAll(genreImagePair.first)
                images.addAll(genreImagePair.second)

                genreImagePair.first.forEach { genre ->
                    artistGenreCrossRefs.add(ArtistGenreCrossRef(artistEntity.id, genre.id))
                }

                genreImagePair.second.forEach { image ->
                    artistImageCrossRefs.add(ArtistImageCrossRef(artistEntity.id, image.id))
                }
            }

            artistDao.insertArtistsWithRelations(
                artists = artists,
                genres = genres.distinctBy { it.name },
                images = images.distinctBy { it.url },
                artistGenreCrossRefs = artistGenreCrossRefs,
                artistImageCrossRefs = artistImageCrossRefs
            )

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

    private fun ArtistWithRelations.toDomainModel() = DetailedArtist(
        id = artist.id,
        name = artist.name,
        spotifyUrl = artist.spotifyUrl,
        genres = genres.map { it.name },
        images = images.map { it.url },
        popularity = artist.popularity
    )

    companion object {
        private val CACHE_DURATION_MS = TimeUnit.HOURS.toMillis(24) // Cache for 24 hours
    }
} 