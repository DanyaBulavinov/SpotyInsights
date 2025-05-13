package com.daniel.spotyinsights.domain.di

import com.daniel.spotyinsights.domain.repository.ArtistRepository
import com.daniel.spotyinsights.domain.repository.RecommendationsRepository
import com.daniel.spotyinsights.domain.repository.TopArtistsRepository
import com.daniel.spotyinsights.domain.repository.TopTracksRepository
import com.daniel.spotyinsights.domain.usecase.artist.GetArtistByIdUseCase
import com.daniel.spotyinsights.domain.usecase.artist.GetTopArtistsUseCase
import com.daniel.spotyinsights.domain.usecase.artist.RefreshTopArtistsUseCase
import com.daniel.spotyinsights.domain.usecase.recommendations.GetGenreSeedsUseCase
import com.daniel.spotyinsights.domain.usecase.recommendations.GetRecommendationsUseCase
import com.daniel.spotyinsights.domain.usecase.recommendations.RefreshRecommendationsUseCase
import com.daniel.spotyinsights.domain.usecase.tracks.GetTopTracksUseCase
import com.daniel.spotyinsights.domain.usecase.tracks.RefreshTopTracksUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideGetTopTracksUseCase(repository: TopTracksRepository): GetTopTracksUseCase {
        return GetTopTracksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRefreshTopTracksUseCase(repository: TopTracksRepository): RefreshTopTracksUseCase {
        return RefreshTopTracksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTopArtistsUseCase(repository: TopArtistsRepository): GetTopArtistsUseCase {
        return GetTopArtistsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRefreshTopArtistsUseCase(repository: TopArtistsRepository): RefreshTopArtistsUseCase {
        return RefreshTopArtistsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetRecommendationsUseCase(repository: RecommendationsRepository): GetRecommendationsUseCase {
        return GetRecommendationsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetGenreSeedsUseCase(repository: RecommendationsRepository): GetGenreSeedsUseCase {
        return GetGenreSeedsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRefreshRecommendationsUseCase(repository: RecommendationsRepository): RefreshRecommendationsUseCase {
        return RefreshRecommendationsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetArtistByIdUseCase(repository: ArtistRepository): GetArtistByIdUseCase {
        return GetArtistByIdUseCase(repository)
    }
} 