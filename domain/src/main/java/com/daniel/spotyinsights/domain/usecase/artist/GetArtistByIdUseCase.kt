package com.daniel.spotyinsights.domain.usecase.artist

import com.daniel.spotyinsights.domain.model.DetailedArtist
import com.daniel.spotyinsights.domain.repository.ArtistRepository
import javax.inject.Inject

class GetArtistByIdUseCase @Inject constructor(
    private val repository: ArtistRepository
) {
    suspend operator fun invoke(id: String): DetailedArtist {
        return repository.getArtistById(id)
    }
} 