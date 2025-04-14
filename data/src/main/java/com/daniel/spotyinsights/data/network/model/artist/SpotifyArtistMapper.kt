package com.daniel.spotyinsights.data.network.model.artist

import com.daniel.spotyinsights.data.local.entity.DetailedArtistEntity
import com.daniel.spotyinsights.data.local.entity.GenreEntity
import com.daniel.spotyinsights.data.local.entity.ImageEntity

fun SpotifyArtist.toDetailedArtistEntity(
    fetchTimeMs: Long,
    timeRange: String
): Pair<DetailedArtistEntity, Pair<List<GenreEntity>, List<ImageEntity>>> {
    val artist = DetailedArtistEntity(
        id = id,
        name = name,
        spotifyUrl = externalUrls["spotify"] ?: "",
        popularity = popularity,
        fetchTimeMs = fetchTimeMs,
        timeRange = timeRange
    )

    val genreEntities = genres.map { genreName ->
        GenreEntity(name = genreName)
    }

    val imageEntities = images.map { image ->
        ImageEntity(
            url = image.url,
            height = image.height,
            width = image.width
        )
    }

    return artist to (genreEntities to imageEntities)
} 