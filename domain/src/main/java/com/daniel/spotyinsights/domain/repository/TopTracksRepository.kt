package com.daniel.spotyinsights.domain.repository

import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.model.Track
import kotlinx.coroutines.flow.Flow

enum class TimeRange {
    SHORT_TERM, // 4 weeks
    MEDIUM_TERM, // 6 months
    LONG_TERM // years
}

interface TopTracksRepository {
    fun getTopTracks(timeRange: TimeRange): Flow<Result<List<Track>>>
    
    suspend fun refreshTopTracks(timeRange: TimeRange): Result<Unit>
} 