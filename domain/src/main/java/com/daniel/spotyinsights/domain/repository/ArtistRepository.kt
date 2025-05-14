package com.daniel.spotyinsights.domain.repository

import com.daniel.spotyinsights.domain.model.DetailedArtist

interface ArtistRepository {
    suspend fun getArtistById(id: String): DetailedArtist
} 