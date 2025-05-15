package com.daniel.spotyinsights.data.repository

import com.daniel.spotyinsights.data.network.api.LastFmApiService
import com.daniel.spotyinsights.data.network.model.lastfm.LastFmArtist
import com.daniel.spotyinsights.domain.model.LastFmArtistDomain
import com.daniel.spotyinsights.domain.repository.LastFmRepository
import javax.inject.Inject

class LastFmRepositoryImpl @Inject constructor(
    private val api: LastFmApiService
) : LastFmRepository {
    override suspend fun getArtistInfo(artist: String): LastFmArtistDomain? {
        return api.getArtistInfo(artist).artist.toDomain()
    }
}

private fun LastFmArtist?.toDomain(): LastFmArtistDomain? {
    return this?.let {
        LastFmArtistDomain(
            name = it.name,
        )
    }
}