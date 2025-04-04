package com.daniel.spotyinsights.domain.usecase.artists

import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.repository.TopArtistsRepository
import javax.inject.Inject

class RefreshTopArtistsUseCase @Inject constructor(
    private val repository: TopArtistsRepository
) {
    suspend operator fun invoke(timeRange: TimeRange): Result<Unit> {
        return repository.refreshTopArtists(timeRange)
    }
} 