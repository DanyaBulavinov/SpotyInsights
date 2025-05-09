package com.daniel.spotyinsights.data.repository

import android.util.Log
import com.daniel.spotyinsights.data.network.api.SpotifyApiService
import com.daniel.spotyinsights.domain.model.NewRelease
import com.daniel.spotyinsights.domain.model.NewReleaseArtist
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.NewReleasesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewReleasesRepositoryImpl @Inject constructor(
    private val spotifyApiService: SpotifyApiService
) : NewReleasesRepository {

    override fun getNewReleases(): Flow<Result<List<NewRelease>>> = flow {
        emit(Result.Loading)
        try {
            val result = refreshNewReleases()
            when (result) {
                is Result.Success -> emit(Result.Success(fetchedReleases))
                is Result.Error -> emit(Result.Error(result.exception))
                is Result.Loading -> { /* No-op */ }
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun refreshNewReleases(): Result<Unit> {
        return try {
            Log.d("NewReleasesRepository", "Refreshing new releases")
            val response = spotifyApiService.getNewReleases(limit = 50)
            Log.d("NewReleasesRepository", "API response received. Items count: ${response.albums.items.size}")

            if (response.albums.items.isEmpty()) {
                Log.w("NewReleasesRepository", "API returned empty new releases list")
                fetchedReleases = emptyList()
                return Result.Success(Unit)
            }

            fetchedReleases = response.albums.items.map { album ->
                NewRelease(
                    id = album.id,
                    name = album.name,
                    albumType = album.albumType,
                    totalTracks = album.totalTracks,
                    artists = album.artists.map { artist ->
                        NewReleaseArtist(
                            id = artist.id,
                            name = artist.name,
                            spotifyUrl = artist.externalUrls["spotify"] ?: ""
                        )
                    },
                    imageUrl = album.images.firstOrNull()?.url ?: "",
                    releaseDate = album.releaseDate,
                    spotifyUrl = album.externalUrls["spotify"] ?: ""
                )
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("NewReleasesRepository", "Error refreshing new releases", e)
            Result.Error(e)
        }
    }

    companion object {
        private var fetchedReleases = emptyList<NewRelease>()
    }
} 