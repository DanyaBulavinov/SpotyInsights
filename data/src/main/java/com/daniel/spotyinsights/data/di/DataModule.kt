package com.daniel.spotyinsights.data.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.daniel.spotyinsights.data.local.SpotifyDatabase
import com.daniel.spotyinsights.data.local.dao.ArtistDao
import com.daniel.spotyinsights.data.local.dao.TrackDao
import com.daniel.spotyinsights.data.network.api.SpotifyApiService
import com.daniel.spotyinsights.data.repository.NewReleasesRepositoryImpl
import com.daniel.spotyinsights.data.repository.RecommendationsRepositoryImpl
import com.daniel.spotyinsights.data.repository.TopArtistsRepositoryImpl
import com.daniel.spotyinsights.data.repository.TopTracksRepositoryImpl
import com.daniel.spotyinsights.domain.repository.NewReleasesRepository
import com.daniel.spotyinsights.data.repository.TrackRepositoryImpl
import com.daniel.spotyinsights.domain.repository.RecommendationsRepository
import com.daniel.spotyinsights.domain.repository.TopArtistsRepository
import com.daniel.spotyinsights.domain.repository.TopTracksRepository
import com.daniel.spotyinsights.domain.repository.TrackRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideSpotifyDatabase(
        @ApplicationContext context: Context
    ): SpotifyDatabase {
        Log.d("DataModule", "Creating SpotifyDatabase instance")
        return Room.databaseBuilder(
            context,
            SpotifyDatabase::class.java,
            SpotifyDatabase.DATABASE_NAME
        )
            .addMigrations(
                SpotifyDatabase.MIGRATION_2_3,
                SpotifyDatabase.MIGRATION_3_4
            )
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d("DataModule", "Database created")
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    Log.d("DataModule", "Database opened")
                }
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideTrackDao(database: SpotifyDatabase): TrackDao {
        return database.trackDao()
    }

    @Provides
    @Singleton
    fun provideArtistDao(database: SpotifyDatabase): ArtistDao {
        return database.artistDao()
    }

    @Provides
    @Singleton
    fun provideSpotifyApiService(@Named("spotify") retrofit: Retrofit): SpotifyApiService {
        return retrofit.create(SpotifyApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTopTracksRepository(
        spotifyApiService: SpotifyApiService,
        trackDao: TrackDao
    ): TopTracksRepository {
        return TopTracksRepositoryImpl(spotifyApiService, trackDao)
    }

    @Provides
    @Singleton
    fun provideTopArtistsRepository(
        spotifyApiService: SpotifyApiService,
        artistDao: ArtistDao
    ): TopArtistsRepository {
        return TopArtistsRepositoryImpl(spotifyApiService, artistDao)
    }

    @Provides
    @Singleton
    fun provideRecommendationsRepository(
        spotifyApiService: SpotifyApiService,
        trackDao: TrackDao
    ): RecommendationsRepository {
        return RecommendationsRepositoryImpl(spotifyApiService, trackDao)
    }

    @Provides
    @Singleton
    fun provideNewReleasesRepository(
        spotifyApiService: SpotifyApiService
    ): NewReleasesRepository {
        return NewReleasesRepositoryImpl(spotifyApiService)
    }

    @Provides
    @Singleton
    fun provideTrackRepository(
        spotifyApiService: SpotifyApiService
    ): TrackRepository {
        return TrackRepositoryImpl(spotifyApiService)
    }
}