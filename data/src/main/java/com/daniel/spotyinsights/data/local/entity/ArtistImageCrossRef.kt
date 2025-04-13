package com.daniel.spotyinsights.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "artist_image_cross_ref",
    primaryKeys = ["artistId", "imageId"],
    foreignKeys = [
        ForeignKey(
            entity = DetailedArtistEntity::class,
            parentColumns = ["id"],
            childColumns = ["artistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ImageEntity::class,
            parentColumns = ["id"],
            childColumns = ["imageId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ArtistImageCrossRef(
    val artistId: String,
    val imageId: Long
) 