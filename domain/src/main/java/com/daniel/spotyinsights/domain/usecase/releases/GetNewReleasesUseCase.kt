package com.daniel.spotyinsights.domain.usecase.releases

import com.daniel.spotyinsights.domain.model.NewRelease
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.NewReleasesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewReleasesUseCase @Inject constructor(
    private val repository: NewReleasesRepository
) {
    operator fun invoke(): Flow<Result<List<NewRelease>>> {
        return repository.getNewReleases()
    }
} 