package com.daniel.spotyinsights.domain.usecase.tracks

import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.model.Track
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.repository.TopTracksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopTracksUseCase @Inject constructor(
    private val repository: TopTracksRepository
) {
    operator fun invoke(timeRange: TimeRange): Flow<Result<List<Track>>> {
        return repository.getTopTracks(timeRange)
    }
} 