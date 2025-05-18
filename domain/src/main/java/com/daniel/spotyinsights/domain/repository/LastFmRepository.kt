package com.daniel.spotyinsights.domain.repository

import com.daniel.spotyinsights.domain.model.LastFmArtistDomain
import com.daniel.spotyinsights.domain.model.LastFmTagDomain
import com.daniel.spotyinsights.domain.model.PlayCountPerDay


interface LastFmRepository {
    suspend fun getArtistInfo(artist: String): LastFmArtistDomain?
    suspend fun getTopTags(): List<LastFmTagDomain>
    suspend fun getPlayCountPerDay(user: String): List<PlayCountPerDay>
} 