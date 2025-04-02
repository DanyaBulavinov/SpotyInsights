package com.daniel.spotyinsights.auth.di

import com.daniel.spotyinsights.auth.BuildConfig
import com.daniel.spotyinsights.auth.model.SpotifyAuthConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthConfigModule {

    @Provides
    @Singleton
    fun provideSpotifyAuthConfig(): SpotifyAuthConfig {
        return SpotifyAuthConfig(
            clientId = BuildConfig.SPOTIFY_CLIENT_ID,
            clientSecret = BuildConfig.SPOTIFY_CLIENT_SECRET,
            redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI
        )
    }
} 