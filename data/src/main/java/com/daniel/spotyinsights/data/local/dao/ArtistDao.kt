package com.daniel.spotyinsights.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.daniel.spotyinsights.data.local.entity.ArtistGenreCrossRef
import com.daniel.spotyinsights.data.local.entity.ArtistImageCrossRef
import com.daniel.spotyinsights.data.local.entity.DetailedArtistEntity
import com.daniel.spotyinsights.data.local.entity.GenreEntity
import com.daniel.spotyinsights.data.local.entity.ImageEntity
import kotlinx.coroutines.flow.Flow

data class ArtistWithRelations(
    @Embedded val artist: DetailedArtistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ArtistGenreCrossRef::class,
            parentColumn = "artistId",
            entityColumn = "genreId"
        )
    )
    val genres: List<GenreEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ArtistImageCrossRef::class,
            parentColumn = "artistId",
            entityColumn = "imageId"
        )
    )
    val images: List<ImageEntity>
)

@Dao
interface ArtistDao {
    @Transaction
    @Query(
        """
        SELECT * FROM detailed_artists 
        WHERE fetchTimeMs >= :minFetchTimeMs 
        AND timeRange = :timeRange
        ORDER BY name ASC
    """
    )
    fun getTopArtists(minFetchTimeMs: Long, timeRange: String): Flow<List<ArtistWithRelations>>

    @Query("SELECT * FROM genres WHERE name IN (:names)")
    suspend fun getGenresByNames(names: List<String>): List<GenreEntity>

    @Query("SELECT * FROM images WHERE url IN (:urls)")
    suspend fun getImagesByUrls(urls: List<String>): List<ImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<DetailedArtistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ImageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtistGenreCrossRefs(crossRefs: List<ArtistGenreCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtistImageCrossRefs(crossRefs: List<ArtistImageCrossRef>)

    @Query("DELETE FROM detailed_artists WHERE timeRange = :timeRange")
    suspend fun deleteArtistsByTimeRange(timeRange: String)

    @Transaction
    suspend fun insertArtistsWithRelations(
        artists: List<DetailedArtistEntity>,
        genres: List<GenreEntity>,
        images: List<ImageEntity>,
        artistGenreCrossRefs: List<ArtistGenreCrossRef>,
        artistImageCrossRefs: List<ArtistImageCrossRef>
    ) {
        insertArtists(artists)
        if (genres.isNotEmpty()) {
            insertGenres(genres)
        }
        if (images.isNotEmpty()) {
            insertImages(images)
        }
        insertArtistGenreCrossRefs(artistGenreCrossRefs)
        insertArtistImageCrossRefs(artistImageCrossRefs)
    }

    @Query("DELETE FROM detailed_artists WHERE fetchTimeMs < :minFetchTimeMs")
    suspend fun deleteOldArtists(minFetchTimeMs: Long)

    @Transaction
    @Query("SELECT * FROM detailed_artists WHERE timeRange = :timeRange")
    fun getArtistsByTimeRange(timeRange: String): Flow<List<ArtistWithRelations>>
}
