package com.daniel.spotyinsights.data.network.api

import com.daniel.spotyinsights.data.network.model.track.SpotifyTrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifyApiService {
    @GET("v1/me/top/tracks")
    suspend fun getTopTracks(
        @Query("time_range") timeRange: String, // short_term (4 weeks) | medium_term (6 months) | long_term (years)
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): SpotifyTrackResponse
} 