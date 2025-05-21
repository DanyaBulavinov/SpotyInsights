package com.daniel.spotyinsights.presentation.artists

import androidx.lifecycle.viewModelScope
import com.daniel.spotyinsights.base.BaseViewModel
import com.daniel.spotyinsights.base.UiEffect
import com.daniel.spotyinsights.base.UiEvent
import com.daniel.spotyinsights.base.UiState
import com.daniel.spotyinsights.domain.model.DetailedArtist
import com.daniel.spotyinsights.domain.usecase.artist.GetArtistByIdUseCase
import com.daniel.spotyinsights.domain.usecase.artist.GetArtistTopTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val getArtistByIdUseCase: GetArtistByIdUseCase,
    private val getArtistTopTracksUseCase: GetArtistTopTracksUseCase
) : BaseViewModel<ArtistDetailState, ArtistDetailEvent, ArtistDetailEffect>() {

    override fun createInitialState(): ArtistDetailState = ArtistDetailState()

    override fun handleEvent(event: ArtistDetailEvent) {
        when (event) {
            is ArtistDetailEvent.LoadArtist -> loadArtist(event.artistId)
        }
    }

    private fun loadArtist(artistId: String) {
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val artist = getArtistByIdUseCase(artistId)
                val topTracks = getArtistTopTracksUseCase(artistId)
                setState { copy(artist = artist, topTracks = topTracks, isLoading = false, error = null) }
            } catch (e: Exception) {
                setState { copy(error = e.message ?: "Unknown error", isLoading = false) }
            }
        }
    }
}

sealed class ArtistDetailEvent : UiEvent {
    data class LoadArtist(val artistId: String) : ArtistDetailEvent()
}

sealed class ArtistDetailEffect : UiEffect

data class ArtistDetailState(
    val artist: DetailedArtist? = null,
    val topTracks: List<com.daniel.spotyinsights.domain.model.Track> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState 