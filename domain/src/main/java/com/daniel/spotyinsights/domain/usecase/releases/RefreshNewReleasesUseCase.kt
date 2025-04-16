package com.daniel.spotyinsights.domain.usecase.releases

import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.NewReleasesRepository
import javax.inject.Inject

class RefreshNewReleasesUseCase @Inject constructor(
    private val repository: NewReleasesRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshNewReleases()
    }
} 