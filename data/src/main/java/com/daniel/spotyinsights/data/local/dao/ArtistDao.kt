package com.daniel.spotyinsights.data.local.dao

import androidx.room.*
import com.daniel.spotyinsights.data.local.entity.*
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
    @Query("""
        SELECT * FROM detailed_artists 
        WHERE fetchTimeMs >= :minFetchTimeMs 
        ORDER BY name ASC
    """)
    fun getTopArtists(minFetchTimeMs: Long): Flow<List<ArtistWithRelations>>

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

    @Transaction
    suspend fun insertArtistsWithRelations(
        artists: List<DetailedArtistEntity>,
        genres: List<GenreEntity>,
        images: List<ImageEntity>,
        artistGenreCrossRefs: List<ArtistGenreCrossRef>,
        artistImageCrossRefs: List<ArtistImageCrossRef>
    ) {
        insertArtists(artists)
        insertGenres(genres)
        insertImages(images)
        insertArtistGenreCrossRefs(artistGenreCrossRefs)
        insertArtistImageCrossRefs(artistImageCrossRefs)
    }

    @Query("DELETE FROM detailed_artists WHERE fetchTimeMs < :minFetchTimeMs")
    suspend fun deleteOldArtists(minFetchTimeMs: Long)
}
