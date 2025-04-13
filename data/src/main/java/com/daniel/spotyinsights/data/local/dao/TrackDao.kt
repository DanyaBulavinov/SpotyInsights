package com.daniel.spotyinsights.data.local.dao

import androidx.room.*
import com.daniel.spotyinsights.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Transaction
    @Query("""
        SELECT * FROM tracks 
        WHERE fetchTimeMs >= :minFetchTimeMs 
        ORDER BY popularity DESC
    """)
    fun getTopTracks(minFetchTimeMs: Long): Flow<List<TrackWithRelations>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<TrackArtistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackArtistCrossRefs(crossRefs: List<TrackArtistCrossRef>)

    @Transaction
    suspend fun insertTracksWithRelations(
        tracks: List<TrackEntity>,
        artists: List<TrackArtistEntity>,
        albums: List<AlbumEntity>,
        trackArtistCrossRefs: List<TrackArtistCrossRef>
    ) {
        insertTracks(tracks)
        insertArtists(artists)
        insertAlbums(albums)
        insertTrackArtistCrossRefs(trackArtistCrossRefs)
    }

    @Query("DELETE FROM tracks WHERE fetchTimeMs < :minFetchTimeMs")
    suspend fun deleteOldTracks(minFetchTimeMs: Long)
}

data class TrackWithRelations(
    @Embedded val track: TrackEntity,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "id"
    )
    val album: AlbumEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TrackArtistCrossRef::class,
            parentColumn = "trackId",
            entityColumn = "artistId"
        )
    )
    val artists: List<TrackArtistEntity>
) 