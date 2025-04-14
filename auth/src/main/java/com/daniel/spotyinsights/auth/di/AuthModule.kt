package com.daniel.spotyinsights.auth.di

import com.daniel.spotyinsights.auth.api.SpotifyAuthService
import com.daniel.spotyinsights.auth.interceptor.AuthInterceptor
import com.daniel.spotyinsights.auth.repository.SpotifyAuthRepository
import com.daniel.spotyinsights.auth.repository.SpotifyAuthRepositoryImpl
import com.daniel.spotyinsights.auth.data.AuthDataStore
import com.daniel.spotyinsights.domain.repository.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindSpotifyAuthRepository(
        repository: SpotifyAuthRepositoryImpl
    ): SpotifyAuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenRepository(impl: AuthDataStore): TokenRepository

    companion object {
        @Provides
        @Singleton
        @Named("auth_refresh")
        fun provideAuthRefreshOkHttpClient(
            @Named("base") baseClient: OkHttpClient
        ): OkHttpClient = baseClient

        @Provides
        @Singleton
        @AuthOkHttpClient
        fun provideAuthenticatedOkHttpClient(
            @Named("base") baseClient: OkHttpClient,
            authInterceptor: AuthInterceptor
        ): OkHttpClient = baseClient.newBuilder()
            .addInterceptor(authInterceptor)
            .build()
    }
} 