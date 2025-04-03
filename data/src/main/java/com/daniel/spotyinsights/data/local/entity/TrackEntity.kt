package com.daniel.spotyinsights.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Junction

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val durationMs: Long,
    val popularity: Int,
    val previewUrl: String?,
    val spotifyUrl: String,
    val explicit: Boolean,
    val albumId: String,
    val fetchTimeMs: Long // To track when the data was fetched
)

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val spotifyUrl: String
)

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val releaseDate: String,
    val imageUrl: String,
    val spotifyUrl: String
)

@Entity(
    tableName = "track_artist_cross_ref",
    primaryKeys = ["trackId", "artistId"],
    foreignKeys = [
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["id"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["id"],
            childColumns = ["artistId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TrackArtistCrossRef(
    val trackId: String,
    val artistId: String
) 