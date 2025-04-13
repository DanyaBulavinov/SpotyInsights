package com.daniel.spotyinsights.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "artist_genre_cross_ref",
    primaryKeys = ["artistId", "genreId"],
    foreignKeys = [
        ForeignKey(
            entity = DetailedArtistEntity::class,
            parentColumns = ["id"],
            childColumns = ["artistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GenreEntity::class,
            parentColumns = ["id"],
            childColumns = ["genreId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ArtistGenreCrossRef(
    val artistId: String,
    val genreId: Long
) 