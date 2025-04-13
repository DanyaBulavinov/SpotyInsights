package com.daniel.spotyinsights.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val releaseDate: String,
    val imageUrl: String,
    val spotifyUrl: String
) 