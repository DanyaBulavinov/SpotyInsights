package com.daniel.spotyinsights.data.repository

import com.daniel.spotyinsights.data.local.dao.ArtistDao
import com.daniel.spotyinsights.data.local.dao.ArtistWithRelations
import com.daniel.spotyinsights.data.local.entity.ArtistGenreCrossRef
import com.daniel.spotyinsights.data.local.entity.ArtistImageCrossRef
import com.daniel.spotyinsights.data.local.entity.DetailedArtistEntity
import com.daniel.spotyinsights.data.local.entity.GenreEntity
import com.daniel.spotyinsights.data.local.entity.ImageEntity
import com.daniel.spotyinsights.data.network.api.SpotifyApiService
import com.daniel.spotyinsights.data.network.model.artist.toDetailedArtistEntity
import com.daniel.spotyinsights.domain.model.DetailedArtist
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.repository.TopArtistsRepository
import com.daniel.spotyinsights.domain.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopArtistsRepositoryImpl @Inject constructor(
    private val spotifyApiService: SpotifyApiService,
    private val artistDao: ArtistDao
) : TopArtistsRepository {

    private data class ArtistGenreCrossRefTemp(
        val artistId: String,
        val genreName: String
    )

    private val refreshMutex = Mutex()

    override fun getTopArtists(timeRange: TimeRange): Flow<Result<List<DetailedArtist>>> {
        val minFetchTimeMs = System.currentTimeMillis() - CACHE_DURATION_MS
        return artistDao.getTopArtists(minFetchTimeMs, timeRange.toApiValue()).map { artists ->
            if (artists.isEmpty()) {
                // If we have no data, return an empty list instead of Loading
                // The ViewModel will handle the refresh
                Result.Success(emptyList())
            } else {
                Result.Success(artists.map { it.toDomainModel() })
            }
        }
    }

    override suspend fun refreshTopArtists(timeRange: TimeRange): Result<Unit> {
        return try {
            refreshMutex.withLock {
                Logger.i("Refreshing top artists for time range: $timeRange")
                val response = spotifyApiService.getTopArtists(
                    timeRange = timeRange.toApiValue(),
                    limit = 50
                )

                val currentTimeMs = System.currentTimeMillis()
                val artists = mutableListOf<DetailedArtistEntity>()
                val genres = mutableListOf<GenreEntity>()
                val images = mutableListOf<ImageEntity>()
                val artistGenreCrossRefs = mutableListOf<ArtistGenreCrossRefTemp>()
                val imageUrlToArtistIds = mutableMapOf<String, MutableList<String>>()

                response.items.forEach { spotifyArtist ->
                    val (artistEntity, genreImagePair) = spotifyArtist.toDetailedArtistEntity(
                        currentTimeMs,
                        timeRange.toApiValue()
                    )
                    artists.add(artistEntity)

                    // Handle genres
                    val (genreEntities, imageEntities) = genreImagePair
                    genres.addAll(genreEntities)
                    // We'll create the actual cross-refs after inserting genres and getting their IDs
                    artistGenreCrossRefs.addAll(
                        genreEntities.map { genre ->
                            // Temporarily store artist ID and genre name - we'll convert to proper IDs later
                            ArtistGenreCrossRefTemp(artistEntity.id, genre.name)
                        }
                    )

                    // Store image URLs with artist IDs for later cross-reference creation
                    imageEntities.forEach { image ->
                        images.add(image)
                        imageUrlToArtistIds.getOrPut(image.url) { mutableListOf() }
                            .add(artistEntity.id)
                    }
                }

                // Delete old data for this time range before inserting new
                val apiTimeRange = timeRange.toApiValue()
                artistDao.deleteArtistsByTimeRange(apiTimeRange)

                Logger.i("Inserting ${artists.size} artists into database")

                // First insert artists and genres
                artistDao.insertArtists(artists)

                // Insert genres and get their IDs
                if (genres.isNotEmpty()) {
                    val distinctGenres = genres.distinctBy { it.name }
                    artistDao.insertGenres(distinctGenres)

                    // Query back the inserted genres to get their generated IDs
                    val insertedGenres = artistDao.getGenresByNames(distinctGenres.map { it.name })

                    // Create genre cross-references using the generated IDs
                    val genreCrossRefs = artistGenreCrossRefs.mapNotNull { tempRef ->
                        insertedGenres.find { it.name == tempRef.genreName }?.let { genre ->
                            ArtistGenreCrossRef(artistId = tempRef.artistId, genreId = genre.id)
                        }
                    }

                    if (genreCrossRefs.isNotEmpty()) {
                        artistDao.insertArtistGenreCrossRefs(genreCrossRefs)
                    }
                }

                // Insert images and get their generated IDs
                if (images.isNotEmpty()) {
                    val distinctImages = images.distinctBy { it.url }
                    artistDao.insertImages(distinctImages)

                    // Query back the inserted images to get their generated IDs
                    val insertedImages = artistDao.getImagesByUrls(distinctImages.map { it.url })

                    // Create image cross-references using the generated IDs
                    val artistImageCrossRefs = insertedImages.flatMap { image ->
                        imageUrlToArtistIds[image.url]?.map { artistId ->
                            ArtistImageCrossRef(artistId = artistId, imageId = image.id)
                        } ?: emptyList()
                    }

                    if (artistImageCrossRefs.isNotEmpty()) {
                        artistDao.insertArtistImageCrossRefs(artistImageCrossRefs)
                    }
                }

                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Logger.e("Error refreshing top artists", e)
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
        private val CACHE_DURATION_MS = TimeUnit.HOURS.toMillis(1)
    }
} 