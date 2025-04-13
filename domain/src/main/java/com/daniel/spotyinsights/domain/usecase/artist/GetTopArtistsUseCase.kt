package com.daniel.spotyinsights.domain.usecase.artist

import com.daniel.spotyinsights.domain.model.DetailedArtist
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.repository.TopArtistsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopArtistsUseCase @Inject constructor(
    private val repository: TopArtistsRepository
) {
    operator fun invoke(timeRange: TimeRange): Flow<Result<List<DetailedArtist>>> {
        return repository.getTopArtists(timeRange)
    }
} 