package com.daniel.spotyinsights.presentation.tracks

import com.daniel.spotyinsights.domain.model.Track
import com.daniel.spotyinsights.domain.repository.TimeRange

data class TopTracksState(
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTimeRange: TimeRange = TimeRange.MEDIUM_TERM,
    val isRefreshing: Boolean = false
) 