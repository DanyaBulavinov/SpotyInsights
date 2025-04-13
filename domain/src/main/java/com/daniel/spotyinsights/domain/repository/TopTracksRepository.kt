package com.daniel.spotyinsights.domain.repository

import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TopTracksRepository {
    fun getTopTracks(timeRange: TimeRange): Flow<Result<List<Track>>>
    
    suspend fun refreshTopTracks(timeRange: TimeRange): Result<Unit>
} 