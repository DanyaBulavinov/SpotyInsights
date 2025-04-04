package com.daniel.spotyinsights.presentation.top_artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.domain.usecase.GetTopArtistsUseCase
import com.daniel.spotyinsights.domain.usecase.RefreshTopArtistsUseCase
import com.daniel.spotyinsights.presentation.util.ErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopArtistsViewModel @Inject constructor(
    private val getTopArtistsUseCase: GetTopArtistsUseCase,
    private val refreshTopArtistsUseCase: RefreshTopArtistsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TopArtistsState())
    val state: StateFlow<TopArtistsState> = _state.asStateFlow()

    init {
        getTopArtists()
    }

    fun onTimeRangeSelected(timeRange: TimeRange) {
        _state.value = _state.value.copy(selectedTimeRange = timeRange)
        getTopArtists()
    }

    fun onRetry() {
        getTopArtists()
    }

    private fun getTopArtists() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            refreshTopArtistsUseCase(state.value.selectedTimeRange)
                .onFailure { exception ->
                    _state.value = _state.value.copy(
                        error = ErrorState(
                            message = exception.message ?: "Unknown error occurred",
                            retryAction = ::onRetry
                        )
                    )
                }

            getTopArtistsUseCase(state.value.selectedTimeRange)
                .catch { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = ErrorState(
                            message = exception.message ?: "Unknown error occurred",
                            retryAction = ::onRetry
                        )
                    )
                }
                .onEach { artists ->
                    _state.value = _state.value.copy(
                        artists = artists,
                        isLoading = false
                    )
                }
                .launchIn(viewModelScope)
        }
    }
} 