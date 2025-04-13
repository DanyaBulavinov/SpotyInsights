package com.daniel.spotyinsights.presentation.top_artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.spotyinsights.domain.model.DetailedArtist
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.usecase.artist.GetTopArtistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopArtistsViewModel @Inject constructor(
    private val getTopArtistsUseCase: GetTopArtistsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopArtistsState())
    val uiState: StateFlow<TopArtistsState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<TopArtistsEffect>()
    val effect: SharedFlow<TopArtistsEffect> = _effect.asSharedFlow()

    init {
        loadArtists()
    }

    fun setEvent(event: TopArtistsEvent) {
        when (event) {
            is TopArtistsEvent.TimeRangeSelected -> {
                _uiState.update { it.copy(selectedTimeRange = event.timeRange) }
                loadArtists()
            }
            TopArtistsEvent.Refresh -> loadArtists()
        }
    }

    private fun loadArtists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                getTopArtistsUseCase(uiState.value.selectedTimeRange).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update { it.copy(
                                artists = result.data,
                                isLoading = false,
                                error = null
                            ) }
                        }
                        is Result.Error -> {
                            handleError(Exception(result.exception.message))
                        }
                        is Result.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private suspend fun handleError(error: Exception) {
        _uiState.update { it.copy(
            isLoading = false,
            error = error.message ?: "An unexpected error occurred"
        ) }
        _effect.emit(TopArtistsEffect.ShowError(error.message ?: "An unexpected error occurred"))
    }
}

sealed interface TopArtistsEvent {
    data class TimeRangeSelected(val timeRange: TimeRange) : TopArtistsEvent
    data object Refresh : TopArtistsEvent
}

sealed interface TopArtistsEffect {
    data class ShowError(val message: String) : TopArtistsEffect
}

data class TopArtistsState(
    val artists: List<DetailedArtist> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTimeRange: TimeRange = TimeRange.MEDIUM_TERM
)