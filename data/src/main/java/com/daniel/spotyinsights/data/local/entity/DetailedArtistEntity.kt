package com.daniel.spotyinsights.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detailed_artists")
data class DetailedArtistEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val spotifyUrl: String,
    val popularity: Int,
    val fetchTimeMs: Long // To track when the data was fetched
)

@Entity(tableName = "track_artists")
data class TrackArtistEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val spotifyUrl: String
)
