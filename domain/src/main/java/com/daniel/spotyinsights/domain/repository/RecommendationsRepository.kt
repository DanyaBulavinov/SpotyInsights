package com.daniel.spotyinsights.domain.repository

import com.daniel.spotyinsights.domain.model.Recommendations
import com.daniel.spotyinsights.domain.model.RecommendationParameters
import com.daniel.spotyinsights.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface RecommendationsRepository {
    /**
     * Get recommendations based on the provided parameters
     * @param parameters Parameters to use for generating recommendations
     * @return Flow of Result containing recommendations
     */
    fun getRecommendations(parameters: RecommendationParameters): Flow<Result<Recommendations>>
    
    /**
     * Get available genre seeds that can be used for recommendations
     * @return Result containing list of available genre seeds
     */
    suspend fun getAvailableGenreSeeds(): Result<List<String>>
    
    /**
     * Force refresh of recommendations for the given parameters
     * @param parameters Parameters to use for generating recommendations
     * @return Result indicating success or failure
     */
    suspend fun refreshRecommendations(parameters: RecommendationParameters): Result<Unit>
} 