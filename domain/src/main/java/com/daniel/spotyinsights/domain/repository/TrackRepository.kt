package com.daniel.spotyinsights.domain.repository

import com.daniel.spotyinsights.domain.model.Track

interface TrackRepository {
    suspend fun getTrackById(id: String): Track
} 