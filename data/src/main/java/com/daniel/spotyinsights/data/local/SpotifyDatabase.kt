package com.daniel.spotyinsights.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.daniel.spotyinsights.data.local.dao.TrackDao
import com.daniel.spotyinsights.data.local.entity.*

@Database(
    entities = [
        TrackEntity::class,
        ArtistEntity::class,
        AlbumEntity::class,
        TrackArtistCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SpotifyDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    companion object {
        const val DATABASE_NAME = "spotify_database"
    }
} 