package com.daniel.spotyinsights.domain.repository

import com.daniel.spotyinsights.domain.model.Artist
import com.daniel.spotyinsights.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface TopArtistsRepository {
    /**
     * Get user's top artists from cache if available and not expired
     * @param timeRange The time range to get top artists for
     * @return Flow of Result containing list of artists
     */
    fun getTopArtists(timeRange: TimeRange): Flow<Result<List<Artist>>>
    
    /**
     * Force refresh of user's top artists from the network
     * @param timeRange The time range to refresh top artists for
     * @return Result indicating success or failure
     */
    suspend fun refreshTopArtists(timeRange: TimeRange): Result<Unit>
} 