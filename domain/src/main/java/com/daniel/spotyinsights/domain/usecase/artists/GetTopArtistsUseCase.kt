package com.daniel.spotyinsights.domain.usecase.artists

import com.daniel.spotyinsights.domain.model.Artist
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.repository.TopArtistsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopArtistsUseCase @Inject constructor(
    private val repository: TopArtistsRepository
) {
    operator fun invoke(timeRange: TimeRange): Flow<Result<List<Artist>>> {
        return repository.getTopArtists(timeRange)
    }
} 