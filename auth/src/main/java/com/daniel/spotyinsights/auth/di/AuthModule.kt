package com.daniel.spotyinsights.auth.di

import com.daniel.spotyinsights.auth.api.SpotifyAuthService
import com.daniel.spotyinsights.auth.interceptor.AuthInterceptor
import com.daniel.spotyinsights.auth.repository.SpotifyAuthRepository
import com.daniel.spotyinsights.auth.repository.SpotifyAuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.inject.Named

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindSpotifyAuthRepository(
        repository: SpotifyAuthRepositoryImpl
    ): SpotifyAuthRepository

    companion object {
        @Provides
        @Singleton
        @AuthOkHttpClient
        fun provideAuthOkHttpClient(
            @Named("base") baseClient: OkHttpClient
        ): OkHttpClient {
            return baseClient
        }

        @Provides
        @Singleton
        fun provideSpotifyAuthService(
            @AuthOkHttpClient okHttpClient: OkHttpClient
        ): SpotifyAuthService {
            return Retrofit.Builder()
                .baseUrl("https://accounts.spotify.com/")
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(SpotifyAuthService::class.java)
        }

        @Provides
        @Singleton
        fun provideAuthenticatedOkHttpClient(
            @Named("base") baseClient: OkHttpClient,
            authInterceptor: AuthInterceptor
        ): OkHttpClient {
            return baseClient.newBuilder()
                .addInterceptor(authInterceptor)
                .build()
        }
    }
} 