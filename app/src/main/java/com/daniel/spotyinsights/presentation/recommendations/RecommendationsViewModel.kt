package com.daniel.spotyinsights.presentation.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.spotyinsights.domain.usecase.GetGenreSeedsUseCase
import com.daniel.spotyinsights.domain.usecase.GetRecommendationsUseCase
import com.daniel.spotyinsights.domain.usecase.RefreshRecommendationsUseCase
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
class RecommendationsViewModel @Inject constructor(
    private val getRecommendationsUseCase: GetRecommendationsUseCase,
    private val refreshRecommendationsUseCase: RefreshRecommendationsUseCase,
    private val getGenreSeedsUseCase: GetGenreSeedsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RecommendationsState())
    val state: StateFlow<RecommendationsState> = _state.asStateFlow()

    init {
        loadGenreSeeds()
        getRecommendations()
    }

    fun onGenreSelected(genre: String) {
        val currentGenres = _state.value.selectedGenres.toMutableList()
        if (currentGenres.contains(genre)) {
            currentGenres.remove(genre)
        } else if (currentGenres.size < 5) {
            currentGenres.add(genre)
        }
        _state.value = _state.value.copy(selectedGenres = currentGenres)
        getRecommendations()
    }

    fun onRetry() {
        getRecommendations()
    }

    private fun loadGenreSeeds() {
        viewModelScope.launch {
            getGenreSeedsUseCase()
                .catch { exception ->
                    _state.value = _state.value.copy(
                        error = ErrorState(
                            message = exception.message ?: "Failed to load genre seeds",
                            retryAction = ::loadGenreSeeds
                        )
                    )
                }
                .onEach { genres ->
                    _state.value = _state.value.copy(
                        availableGenres = genres
                    )
                }
                .launchIn(viewModelScope)
        }
    }

    private fun getRecommendations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            refreshRecommendationsUseCase(state.value.selectedGenres)
                .onFailure { exception ->
                    _state.value = _state.value.copy(
                        error = ErrorState(
                            message = exception.message ?: "Failed to refresh recommendations",
                            retryAction = ::onRetry
                        )
                    )
                }

            getRecommendationsUseCase(state.value.selectedGenres)
                .catch { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = ErrorState(
                            message = exception.message ?: "Failed to load recommendations",
                            retryAction = ::onRetry
                        )
                    )
                }
                .onEach { recommendations ->
                    _state.value = _state.value.copy(
                        recommendations = recommendations,
                        isLoading = false
                    )
                }
                .launchIn(viewModelScope)
        }
    }
} 