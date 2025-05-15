package com.daniel.spotyinsights.data.network.api

import com.daniel.spotyinsights.data.network.model.lastfm.LastFmArtistResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmApiService {
    @GET("?method=artist.getinfo&format=json")
    suspend fun getArtistInfo(
        @Query("artist") artist: String,
        @Query("api_key") apiKey: String = com.daniel.spotyinsights.data.network.LastFmApiConfig.API_KEY
    ): LastFmArtistResponse
} 