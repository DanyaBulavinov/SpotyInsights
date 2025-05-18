package com.daniel.spotyinsights.data.network.api

import com.daniel.spotyinsights.data.network.model.lastfm.LastFmArtistResponse
import com.daniel.spotyinsights.data.network.model.lastfm.LastFmUserTopTagsResponse
import com.daniel.spotyinsights.data.network.model.lastfm.LastFmRecentTracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmApiService {
    @GET("?method=artist.getinfo&format=json")
    suspend fun getArtistInfo(
        @Query("artist") artist: String,
        @Query("api_key") apiKey: String = com.daniel.spotyinsights.data.network.LastFmApiConfig.API_KEY
    ): LastFmArtistResponse

    @GET("?method=tag.getTopTags&format=json")
    suspend fun getTopTags(
        @Query("api_key") apiKey: String = com.daniel.spotyinsights.data.network.LastFmApiConfig.API_KEY,
    ): LastFmUserTopTagsResponse

    @GET("?method=user.getrecenttracks&format=json")
    suspend fun getRecentTracks(
        @Query("user") user: String,
        @Query("api_key") apiKey: String = com.daniel.spotyinsights.data.network.LastFmApiConfig.API_KEY,
        @Query("limit") limit: Int = 200
    ): LastFmRecentTracksResponse
}