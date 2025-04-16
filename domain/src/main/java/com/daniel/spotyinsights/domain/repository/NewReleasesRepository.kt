package com.daniel.spotyinsights.domain.repository

import com.daniel.spotyinsights.domain.model.NewRelease
import com.daniel.spotyinsights.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface NewReleasesRepository {
    fun getNewReleases(): Flow<Result<List<NewRelease>>>
    suspend fun refreshNewReleases(): Result<Unit>
} 