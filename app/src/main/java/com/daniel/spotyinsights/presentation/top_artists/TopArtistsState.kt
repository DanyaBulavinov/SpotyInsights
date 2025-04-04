package com.daniel.spotyinsights.presentation.top_artists

import com.daniel.spotyinsights.domain.model.Artist
import com.daniel.spotyinsights.domain.repository.TimeRange
import com.daniel.spotyinsights.presentation.util.ErrorState

data class TopArtistsState(
    val artists: List<Artist> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTimeRange: TimeRange = TimeRange.SHORT_TERM,
    val error: ErrorState? = null
) 