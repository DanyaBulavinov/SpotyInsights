package com.daniel.spotyinsights.presentation.top_artists

import androidx.lifecycle.viewModelScope
import com.daniel.spotyinsights.base.BaseViewModel
import com.daniel.spotyinsights.base.UiEffect
import com.daniel.spotyinsights.base.UiEvent
import com.daniel.spotyinsights.base.UiState
import com.daniel.spotyinsights.domain.model.DetailedArtist
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.usecase.artist.GetTopArtistsUseCase
import com.daniel.spotyinsights.domain.usecase.artist.RefreshTopArtistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopArtistsViewModel @Inject constructor(
    private val getTopArtistsUseCase: GetTopArtistsUseCase,
    private val refreshTopArtistsUseCase: RefreshTopArtistsUseCase
) : BaseViewModel<TopArtistsState, TopArtistsEvent, TopArtistsEffect>() {

    private var currentLoadJob: Job? = null
    private var isInitialLoad = true

    override fun createInitialState(): TopArtistsState = TopArtistsState()

    init {
        loadArtists()
    }

    override fun handleEvent(event: TopArtistsEvent) {
        when (event) {
            is TopArtistsEvent.TimeRangeSelected -> {
                if (event.timeRange != uiState.value.selectedTimeRange) {
                    setState {
                        copy(
                            selectedTimeRange = event.timeRange,
                            artists = emptyList(), // Clear current list to avoid showing old data
                            isLoading = true
                        )
                    }
                    loadArtists(forceRefresh = true)
                }
            }

            TopArtistsEvent.Refresh -> onRefresh()
        }
    }

    private fun onRefresh() {
        loadArtists(forceRefresh = true)
    }

    private fun loadArtists(forceRefresh: Boolean = false) {
        // Cancel any ongoing loading operation
        currentLoadJob?.cancel()

        currentLoadJob = viewModelScope.launch {
            try {
                if (forceRefresh) {
                    setState { copy(isRefreshing = true) }
                    when (val result = refreshTopArtistsUseCase(uiState.value.selectedTimeRange)) {
                        is Result.Success -> {
                            setState { copy(error = null) }
                        }

                        is Result.Error -> {
                            setState {
                                copy(
                                    error = result.exception.message ?: "Failed to refresh artists",
                                    isRefreshing = false,
                                    isLoading = false
                                )
                            }
                            setEffect {
                                TopArtistsEffect.ShowError(
                                    result.exception.message ?: "Failed to refresh artists"
                                )
                            }
                            return@launch
                        }

                        is Result.Loading -> {
                            // Loading state is handled by isRefreshing flag
                        }
                    }
                }

                getTopArtistsUseCase(uiState.value.selectedTimeRange)
                    .onEach { result ->
                        when (result) {
                            is Result.Success -> {
                                if (result.data.isEmpty() && isInitialLoad) {
                                    // If it's the initial load and we got empty data, trigger a refresh
                                    isInitialLoad = false
                                    loadArtists(forceRefresh = true)
                                } else {
                                    setState {
                                        copy(
                                            artists = result.data,
                                            isLoading = false,
                                            error = null,
                                            isRefreshing = false
                                        )
                                    }
                                    isInitialLoad = false
                                }
                            }

                            is Result.Error -> {
                                setState {
                                    copy(
                                        error = result.exception.message
                                            ?: "Failed to load artists",
                                        isLoading = false,
                                        isRefreshing = false
                                    )
                                }
                                setEffect {
                                    TopArtistsEffect.ShowError(
                                        result.exception.message ?: "Failed to load artists"
                                    )
                                }
                            }

                            is Result.Loading -> {
                                if (!forceRefresh) {
                                    setState {
                                        copy(
                                            isLoading = true,
                                            error = null
                                        )
                                    }
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
                                TopArtistsEffect.ShowError(
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
                        TopArtistsEffect.ShowError(
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

sealed interface TopArtistsEvent : UiEvent {
    data class TimeRangeSelected(val timeRange: TimeRange) : TopArtistsEvent
    data object Refresh : TopArtistsEvent
}

sealed interface TopArtistsEffect : UiEffect {
    data class ShowError(val message: String) : TopArtistsEffect
}

data class TopArtistsState(
    val artists: List<DetailedArtist> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTimeRange: TimeRange = TimeRange.MEDIUM_TERM,
    val isRefreshing: Boolean = false
) : UiState