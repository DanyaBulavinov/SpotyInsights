package com.daniel.spotyinsights.data.network.api

import com.daniel.spotyinsights.data.network.model.artist.SpotifyArtistResponse
import com.daniel.spotyinsights.data.network.model.recommendations.SpotifyAvailableGenreSeedsResponse
import com.daniel.spotyinsights.data.network.model.recommendations.SpotifyRecommendationsResponse
import com.daniel.spotyinsights.data.network.model.track.SpotifyTrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifyApiService {
    @GET("me/top/tracks")
    suspend fun getTopTracks(
        @Query("time_range") timeRange: String, // short_term (4 weeks) | medium_term (6 months) | long_term (years)
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): SpotifyTrackResponse

    @GET("me/top/artists")
    suspend fun getTopArtists(
        @Query("time_range") timeRange: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): SpotifyArtistResponse

    @GET("recommendations")
    suspend fun getRecommendations(
        @Query("seed_artists") seedArtists: String? = null,
        @Query("seed_tracks") seedTracks: String? = null,
        @Query("seed_genres") seedGenres: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("min_popularity") minPopularity: Int? = null,
        @Query("max_popularity") maxPopularity: Int? = null,
        @Query("target_popularity") targetPopularity: Int? = null
    ): SpotifyRecommendationsResponse

    @GET("recommendations/available-genre-seeds")
    suspend fun getAvailableGenreSeeds(): SpotifyAvailableGenreSeedsResponse
} 