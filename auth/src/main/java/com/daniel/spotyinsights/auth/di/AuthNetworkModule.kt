package com.daniel.spotyinsights.auth.di

import com.daniel.spotyinsights.auth.api.SpotifyAuthService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthNetworkModule {

    private const val AUTH_BASE_URL = "https://accounts.spotify.com/"

    @Provides
    @Singleton
    @Named("auth")
    fun provideSpotifyAuthRetrofit(
        @Named("auth_refresh") okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(AUTH_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideSpotifyAuthService(@Named("auth") retrofit: Retrofit): SpotifyAuthService =
        retrofit.create(SpotifyAuthService::class.java)
} 