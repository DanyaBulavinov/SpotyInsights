package com.daniel.spotyinsights.presentation.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.usecase.tracks.GetTopTracksUseCase
import com.daniel.spotyinsights.domain.usecase.tracks.RefreshTopTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopTracksViewModel @Inject constructor(
    private val getTopTracksUseCase: GetTopTracksUseCase,
    private val refreshTopTracksUseCase: RefreshTopTracksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TopTracksState())
    val state: StateFlow<TopTracksState> = _state.asStateFlow()

    init {
        loadTracks()
    }

    fun onTimeRangeSelected(timeRange: TimeRange) {
        _state.update { it.copy(selectedTimeRange = timeRange) }
        loadTracks()
    }

    fun onRefresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            
            when (val result = refreshTopTracksUseCase(state.value.selectedTimeRange)) {
                is Result.Success -> {
                    // Refresh successful, new data will be emitted via Flow
                    _state.update { it.copy(error = null) }
                }
                is Result.Error -> {
                    _state.update { 
                        it.copy(
                            error = result.exception.message ?: "Failed to refresh tracks",
                            isRefreshing = false
                        )
                    }
                }
            }
        }
    }

    private fun loadTracks() {
        getTopTracksUseCase(state.value.selectedTimeRange)
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                tracks = result.data,
                                isLoading = false,
                                error = null,
                                isRefreshing = false
                            )
                        }
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                error = result.exception.message ?: "Failed to load tracks",
                                isLoading = false,
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
            .catch { throwable ->
                _state.update {
                    it.copy(
                        error = throwable.message ?: "Unknown error occurred",
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }
} 