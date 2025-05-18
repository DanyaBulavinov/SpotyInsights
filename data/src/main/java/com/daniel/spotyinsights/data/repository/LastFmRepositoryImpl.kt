package com.daniel.spotyinsights.data.repository

import com.daniel.spotyinsights.data.network.api.LastFmApiService
import com.daniel.spotyinsights.data.network.model.lastfm.LastFmArtist
import com.daniel.spotyinsights.domain.model.LastFmArtistDomain
import com.daniel.spotyinsights.domain.model.LastFmTagDomain
import com.daniel.spotyinsights.domain.model.PlayCountPerDay
import com.daniel.spotyinsights.domain.repository.LastFmRepository
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LastFmRepositoryImpl @Inject constructor(
    private val api: LastFmApiService
) : LastFmRepository {
    override suspend fun getArtistInfo(artist: String): LastFmArtistDomain? {
        return api.getArtistInfo(artist).artist.toDomain()
    }

    override suspend fun getTopTags(): List<LastFmTagDomain> {
        return api.getTopTags().toptags?.tag?.take(10)?.map {
            LastFmTagDomain(
                name = it.name,
                count = it.count,
                url = it.url
            )
        } ?: emptyList()
    }

    override suspend fun getPlayCountPerDay(user: String): List<PlayCountPerDay> {
        val response = api.getRecentTracks(user)
        val tracks = response.recenttracks?.track ?: emptyList()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val grouped = tracks.mapNotNull { it.date?.uts?.toLongOrNull() }
            .map { Date(it * 1000) }
            .groupBy { dateFormat.format(it) }
            .mapValues { it.value.size }
        return grouped.entries.sortedBy { it.key }.map { PlayCountPerDay(it.key, it.value) }
    }
}

private fun LastFmArtist?.toDomain(): LastFmArtistDomain? {
    return this?.let {
        LastFmArtistDomain(
            name = it.name,
        )
    }
}