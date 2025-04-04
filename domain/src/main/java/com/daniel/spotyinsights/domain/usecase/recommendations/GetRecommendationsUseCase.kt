package com.daniel.spotyinsights.domain.usecase.recommendations

import com.daniel.spotyinsights.domain.model.RecommendationParameters
import com.daniel.spotyinsights.domain.model.Recommendations
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.RecommendationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecommendationsUseCase @Inject constructor(
    private val repository: RecommendationsRepository
) {
    operator fun invoke(parameters: RecommendationParameters): Flow<Result<Recommendations>> {
        return repository.getRecommendations(parameters)
    }
} 