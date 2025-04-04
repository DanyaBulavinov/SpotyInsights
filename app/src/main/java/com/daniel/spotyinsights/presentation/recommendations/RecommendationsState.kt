package com.daniel.spotyinsights.presentation.recommendations

import com.daniel.spotyinsights.domain.model.Track
import com.daniel.spotyinsights.presentation.util.ErrorState

data class RecommendationsState(
    val recommendations: List<Track> = emptyList(),
    val availableGenres: List<String> = emptyList(),
    val selectedGenres: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: ErrorState? = null
) 