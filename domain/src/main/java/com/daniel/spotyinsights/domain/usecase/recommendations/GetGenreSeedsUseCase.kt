package com.daniel.spotyinsights.domain.usecase.recommendations

import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.RecommendationsRepository
import javax.inject.Inject

class GetGenreSeedsUseCase @Inject constructor(
    private val repository: RecommendationsRepository
) {
    suspend operator fun invoke(): Result<List<String>> {
        return repository.getAvailableGenreSeeds()
    }
} 