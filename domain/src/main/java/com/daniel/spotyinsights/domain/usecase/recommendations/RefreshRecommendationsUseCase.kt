package com.daniel.spotyinsights.domain.usecase.recommendations

import com.daniel.spotyinsights.domain.model.RecommendationParameters
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.RecommendationsRepository
import javax.inject.Inject

class RefreshRecommendationsUseCase @Inject constructor(
    private val repository: RecommendationsRepository
) {
    suspend operator fun invoke(parameters: RecommendationParameters): Result<Unit> {
        return repository.refreshRecommendations(parameters)
    }
} 