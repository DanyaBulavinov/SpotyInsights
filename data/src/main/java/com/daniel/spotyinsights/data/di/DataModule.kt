package com.daniel.spotyinsights.data.di

import android.content.Context
import androidx.room.Room
import com.daniel.spotyinsights.data.local.SpotifyDatabase
import com.daniel.spotyinsights.data.local.dao.ArtistDao
import com.daniel.spotyinsights.data.local.dao.TrackDao
import com.daniel.spotyinsights.data.network.api.SpotifyApiService
import com.daniel.spotyinsights.data.repository.RecommendationsRepositoryImpl
import com.daniel.spotyinsights.data.repository.TopArtistsRepositoryImpl
import com.daniel.spotyinsights.data.repository.TopTracksRepositoryImpl
import com.daniel.spotyinsights.domain.repository.RecommendationsRepository
import com.daniel.spotyinsights.domain.repository.TopArtistsRepository
import com.daniel.spotyinsights.domain.repository.TopTracksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideSpotifyDatabase(
        @ApplicationContext context: Context
    ): SpotifyDatabase {
        return Room.databaseBuilder(
            context,
            SpotifyDatabase::class.java,
            SpotifyDatabase.DATABASE_NAME
        ).build()
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
    fun provideSpotifyApiService(retrofit: Retrofit): SpotifyApiService {
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
} 