package com.daniel.spotyinsights.presentation.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.spotyinsights.domain.model.Track
import com.daniel.spotyinsights.domain.model.RecommendationParameters
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.usecase.recommendations.GetGenreSeedsUseCase
import com.daniel.spotyinsights.domain.usecase.recommendations.GetRecommendationsUseCase
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
class RecommendationsViewModel @Inject constructor(
    private val getRecommendationsUseCase: GetRecommendationsUseCase,
    private val getGenreSeedsUseCase: GetGenreSeedsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendationsState())
    val uiState: StateFlow<RecommendationsState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<RecommendationsEffect>()
    val effect: SharedFlow<RecommendationsEffect> = _effect.asSharedFlow()

    init {
        loadInitialData()
    }

    fun setEvent(event: RecommendationsEvent) {
        when (event) {
            is RecommendationsEvent.GenreSelected -> handleGenreSelection(event.genre)
            RecommendationsEvent.Refresh -> loadRecommendations()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                when (val result = getGenreSeedsUseCase()) {
                    is Result.Success -> {
                        _uiState.update { it.copy(availableGenres = result.data) }
                        loadRecommendations()
                    }
                    is Result.Error -> {
                        handleError(Exception(result.exception.message))
                    }
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleGenreSelection(genre: String) {
        _uiState.update { currentState ->
            val currentSelectedGenres = currentState.selectedGenres.toMutableList()
            if (currentSelectedGenres.contains(genre)) {
                currentSelectedGenres.remove(genre)
            } else if (currentSelectedGenres.size < 5) {
                currentSelectedGenres.add(genre)
            }
            currentState.copy(selectedGenres = currentSelectedGenres)
        }
        loadRecommendations()
    }

    private fun loadRecommendations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val parameters = RecommendationParameters(
                    seedGenres = _uiState.value.selectedGenres
                )
                getRecommendationsUseCase(parameters).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update { it.copy(
                                recommendations = result.data.tracks,
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
        _effect.emit(RecommendationsEffect.ShowError(error.message ?: "An unexpected error occurred"))
    }
}

data class RecommendationsState(
    val recommendations: List<Track> = emptyList(),
    val availableGenres: List<String> = emptyList(),
    val selectedGenres: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface RecommendationsEvent {
    data class GenreSelected(val genre: String) : RecommendationsEvent
    data object Refresh : RecommendationsEvent
}

sealed interface RecommendationsEffect {
    data class ShowError(val message: String) : RecommendationsEffect
} 