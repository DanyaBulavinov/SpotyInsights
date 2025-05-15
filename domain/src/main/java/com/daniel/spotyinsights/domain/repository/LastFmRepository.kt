package com.daniel.spotyinsights.domain.repository

import com.daniel.spotyinsights.domain.model.LastFmArtistDomain


interface LastFmRepository {
    suspend fun getArtistInfo(artist: String): LastFmArtistDomain?
} 