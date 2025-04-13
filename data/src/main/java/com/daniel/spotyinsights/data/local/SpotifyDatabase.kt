package com.daniel.spotyinsights.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.daniel.spotyinsights.data.local.dao.ArtistDao
import com.daniel.spotyinsights.data.local.dao.TrackDao
import com.daniel.spotyinsights.data.local.entity.*

@Database(
    entities = [
        TrackEntity::class,
        DetailedArtistEntity::class,
        AlbumEntity::class,
        TrackArtistEntity::class,
        GenreEntity::class,
        ImageEntity::class,
        TrackArtistCrossRef::class,
        ArtistGenreCrossRef::class,
        ArtistImageCrossRef::class
    ],
    version = 3,
    exportSchema = true
)
abstract class SpotifyDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun artistDao(): ArtistDao

    companion object {
        const val DATABASE_NAME = "spotify_database"

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Drop existing tables
                database.execSQL("DROP TABLE IF EXISTS artists")
                database.execSQL("DROP TABLE IF EXISTS track_artist_refs")

                // Recreate artists table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS artists (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        spotifyUrl TEXT NOT NULL,
                        fetchTimeMs INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}
                    )
                """)

                // Recreate track_artist_refs table with index
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS track_artist_refs (
                        trackId TEXT NOT NULL,
                        artistId TEXT NOT NULL,
                        PRIMARY KEY(trackId, artistId),
                        FOREIGN KEY(trackId) REFERENCES tracks(id) ON DELETE CASCADE,
                        FOREIGN KEY(artistId) REFERENCES artists(id) ON DELETE CASCADE
                    )
                """)

                // Create index on artistId
                database.execSQL("CREATE INDEX IF NOT EXISTS index_track_artist_refs_artistId ON track_artist_refs(artistId)")
            }
        }
    }
} 