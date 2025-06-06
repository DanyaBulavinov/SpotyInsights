package com.daniel.spotyinsights.data.di

import com.daniel.spotyinsights.data.network.SpotifyApiConfig
import com.daniel.spotyinsights.data.network.interceptor.SpotifyAuthInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    @Named("base")
    fun provideBaseOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @Named("spotify")
    fun provideSpotifyOkHttpClient(
        @Named("base") baseClient: OkHttpClient,
        spotifyAuthInterceptor: SpotifyAuthInterceptor
    ): OkHttpClient = baseClient.newBuilder()
        .addInterceptor(spotifyAuthInterceptor)
        .build()

    @Provides
    @Singleton
    @Named("spotify")
    fun provideSpotifyRetrofit(
        @Named("spotify") okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(SpotifyApiConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    @Named("spotify-auth")
    fun provideSpotifyAuthRetrofit(
        @Named("base") okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(SpotifyApiConfig.AUTH_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    @Named("lastfm")
    fun provideLastFmRetrofit(
        @Named("base") okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(com.daniel.spotyinsights.data.network.LastFmApiConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideLastFmApiService(@Named("lastfm") retrofit: Retrofit): com.daniel.spotyinsights.data.network.api.LastFmApiService {
        return retrofit.create(com.daniel.spotyinsights.data.network.api.LastFmApiService::class.java)
    }
}