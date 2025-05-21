package com.daniel.spotyinsights.presentation.tracks

import androidx.lifecycle.viewModelScope
import com.daniel.spotyinsights.base.BaseViewModel
import com.daniel.spotyinsights.base.UiEffect
import com.daniel.spotyinsights.base.UiEvent
import com.daniel.spotyinsights.base.UiState
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.model.Track
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.usecase.tracks.GetTopTracksUseCase
import com.daniel.spotyinsights.domain.usecase.tracks.RefreshTopTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TopTracksState(
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val selectedTimeRange: TimeRange = TimeRange.MEDIUM_TERM
) : UiState

sealed interface TopTracksEvent : UiEvent {
    data class TimeRangeSelected(val timeRange: TimeRange) : TopTracksEvent
    data object Refresh : TopTracksEvent
}

sealed interface TopTracksEffect : UiEffect {
    data class ShowError(val message: String) : TopTracksEffect
}

@HiltViewModel
class TopTracksViewModel @Inject constructor(
    private val getTopTracksUseCase: GetTopTracksUseCase,
    private val refreshTopTracksUseCase: RefreshTopTracksUseCase,
) : BaseViewModel<TopTracksState, TopTracksEvent, TopTracksEffect>() {

    private var currentLoadJob: Job? = null

    override fun createInitialState(): TopTracksState = TopTracksState()

    init {
        loadTracks()
    }

    override fun handleEvent(event: TopTracksEvent) {
        when (event) {
            is TopTracksEvent.TimeRangeSelected -> {
                setState { copy(selectedTimeRange = event.timeRange) }
                // Always load from local DB first for instant UI update
                loadTracks()
                // Then trigger a refresh in the background
                viewModelScope.launch {
                    refreshTopTracksUseCase(event.timeRange)
                }
            }

            is TopTracksEvent.Refresh -> onRefresh()
        }
    }

    private fun onRefresh() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }

            when (val result = refreshTopTracksUseCase(uiState.value.selectedTimeRange)) {
                is Result.Success -> {
                    setState { copy(error = null) }
                    loadTracks() // Reload tracks after successful refresh
                }

                is Result.Error -> {
                    setState {
                        copy(
                            error = result.exception.message ?: "Failed to refresh tracks",
                            isRefreshing = false
                        )
                    }
                    setEffect {
                        TopTracksEffect.ShowError(
                            result.exception.message ?: "Failed to refresh tracks"
                        )
                    }
                }

                is Result.Loading -> {
                    // Loading state is handled by isRefreshing flag
                }
            }
        }
    }

    private fun loadTracks() {
        // Cancel any ongoing loading operation
        currentLoadJob?.cancel()

        currentLoadJob = viewModelScope.launch {
            try {
                getTopTracksUseCase(uiState.value.selectedTimeRange)
                    .onEach { result ->
                        when (result) {
                            is Result.Success -> {
                                setState {
                                    copy(
                                        tracks = result.data,
                                        isLoading = false,
                                        error = null,
                                        isRefreshing = false
                                    )
                                }
                            }

                            is Result.Error -> {
                                setState {
                                    copy(
                                        error = result.exception.message ?: "Failed to load tracks",
                                        isLoading = false,
                                        isRefreshing = false
                                    )
                                }
                                setEffect {
                                    TopTracksEffect.ShowError(
                                        result.exception.message ?: "Failed to load tracks"
                                    )
                                }
                            }

                            is Result.Loading -> {
                                setState {
                                    copy(
                                        isLoading = true,
                                        error = null
                                    )
                                }
                            }
                        }
                    }
                    .catch { throwable ->
                        if (currentLoadJob?.isCancelled == false) {
                            setState {
                                copy(
                                    error = throwable.message ?: "Unknown error occurred",
                                    isLoading = false,
                                    isRefreshing = false
                                )
                            }
                            setEffect {
                                TopTracksEffect.ShowError(
                                    throwable.message ?: "Unknown error occurred"
                                )
                            }
                        }
                    }
                    .launchIn(this)
            } catch (e: Exception) {
                if (currentLoadJob?.isCancelled == false) {
                    setState {
                        copy(
                            error = e.message ?: "Unknown error occurred",
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                    setEffect {
                        TopTracksEffect.ShowError(
                            e.message ?: "Unknown error occurred"
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentLoadJob?.cancel()
    }
}