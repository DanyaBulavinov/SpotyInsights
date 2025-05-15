package com.daniel.spotyinsights.data.network

import com.daniel.spotyinsights.data.BuildConfig

object LastFmApiConfig {
    const val BASE_URL = "https://ws.audioscrobbler.com/2.0/"
    val API_KEY: String get() = BuildConfig.LASTFM_API_KEY
    val SHARED_SECRET: String get() = BuildConfig.LASTFM_SHARED_SECRET
} 