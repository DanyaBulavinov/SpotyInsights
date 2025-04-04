package com.daniel.spotyinsights.data.local.dao

import androidx.room.*
import com.daniel.spotyinsights.data.local.entity.ArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
    @Query("""
        SELECT * FROM artists 
        WHERE fetchTimeMs >= :minFetchTimeMs 
        ORDER BY name ASC
    """)
    fun getTopArtists(minFetchTimeMs: Long): Flow<List<ArtistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Query("DELETE FROM artists WHERE fetchTimeMs < :minFetchTimeMs")
    suspend fun deleteOldArtists(minFetchTimeMs: Long)
} 