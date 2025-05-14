package com.daniel.spotyinsights.data.repository

import com.daniel.spotyinsights.data.network.api.SpotifyApiService
import com.daniel.spotyinsights.domain.model.DetailedArtist
import com.daniel.spotyinsights.domain.repository.ArtistRepository
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val api: SpotifyApiService
) : ArtistRepository {
    override suspend fun getArtistById(id: String): DetailedArtist {
        val networkArtist = api.getArtistById(id)
        return DetailedArtist(
            id = networkArtist.id,
            name = networkArtist.name,
            spotifyUrl = networkArtist.externalUrls["spotify"] ?: "",
            genres = networkArtist.genres,
            images = networkArtist.images.map { it.url },
            popularity = networkArtist.popularity,
            followers = networkArtist.followers?.total
        )
    }
} 