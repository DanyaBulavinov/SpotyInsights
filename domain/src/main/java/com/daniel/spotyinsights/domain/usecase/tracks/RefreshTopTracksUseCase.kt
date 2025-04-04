package com.daniel.spotyinsights.domain.usecase.tracks

import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.repository.TopTracksRepository
import javax.inject.Inject

class RefreshTopTracksUseCase @Inject constructor(
    private val repository: TopTracksRepository
) {
    suspend operator fun invoke(timeRange: TimeRange): Result<Unit> {
        return repository.refreshTopTracks(timeRange)
    }
} 