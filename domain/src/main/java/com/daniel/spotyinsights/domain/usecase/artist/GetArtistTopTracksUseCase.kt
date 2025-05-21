package com.daniel.spotyinsights.domain.usecase.artist

import com.daniel.spotyinsights.domain.model.Track
import com.daniel.spotyinsights.domain.repository.TrackRepository
import javax.inject.Inject

class GetArtistTopTracksUseCase @Inject constructor(
    private val trackRepository: TrackRepository
) {
    suspend operator fun invoke(artistId: String): List<Track> {
        return trackRepository.getArtistTopTracks(artistId)
    }
} 