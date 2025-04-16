package com.daniel.spotyinsights.presentation.releases

import androidx.lifecycle.viewModelScope
import com.daniel.spotyinsights.base.BaseViewModel
import com.daniel.spotyinsights.base.UiEffect
import com.daniel.spotyinsights.base.UiEvent
import com.daniel.spotyinsights.base.UiState
import com.daniel.spotyinsights.domain.model.NewRelease
import com.daniel.spotyinsights.domain.model.Result
import com.daniel.spotyinsights.domain.usecase.releases.GetNewReleasesUseCase
import com.daniel.spotyinsights.domain.usecase.releases.RefreshNewReleasesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewReleasesState(
    val releases: List<NewRelease> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isRefreshing: Boolean = false
) : UiState

sealed interface NewReleasesEvent : UiEvent {
    data object Refresh : NewReleasesEvent
}

sealed interface NewReleasesEffect : UiEffect {
    data class ShowError(val message: String) : NewReleasesEffect
}

@HiltViewModel
class NewReleasesViewModel @Inject constructor(
    private val getNewReleasesUseCase: GetNewReleasesUseCase,
    private val refreshNewReleasesUseCase: RefreshNewReleasesUseCase
) : BaseViewModel<NewReleasesState, NewReleasesEvent, NewReleasesEffect>() {

    private var currentLoadJob: Job? = null

    override fun createInitialState(): NewReleasesState = NewReleasesState()

    init {
        loadReleases()
    }

    override fun handleEvent(event: NewReleasesEvent) {
        when (event) {
            is NewReleasesEvent.Refresh -> onRefresh()
        }
    }

    private fun onRefresh() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }

            when (val result = refreshNewReleasesUseCase()) {
                is Result.Success -> {
                    setState { copy(error = null) }
                    loadReleases()
                }
                is Result.Error -> {
                    setState {
                        copy(
                            error = result.exception.message ?: "Failed to refresh releases",
                            isRefreshing = false
                        )
                    }
                    setEffect {
                        NewReleasesEffect.ShowError(
                            result.exception.message ?: "Failed to refresh releases"
                        )
                    }
                }
                is Result.Loading -> {
                    // Loading state is handled by isRefreshing flag
                }
            }
        }
    }

    private fun loadReleases() {
        currentLoadJob?.cancel()

        currentLoadJob = viewModelScope.launch {
            try {
                getNewReleasesUseCase()
                    .onEach { result ->
                        when (result) {
                            is Result.Success -> {
                                setState {
                                    copy(
                                        releases = result.data,
                                        isLoading = false,
                                        error = null,
                                        isRefreshing = false
                                    )
                                }
                            }
                            is Result.Error -> {
                                setState {
                                    copy(
                                        error = result.exception.message ?: "Failed to load releases",
                                        isLoading = false,
                                        isRefreshing = false
                                    )
                                }
                                setEffect {
                                    NewReleasesEffect.ShowError(
                                        result.exception.message ?: "Failed to load releases"
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
                                NewReleasesEffect.ShowError(
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
                        NewReleasesEffect.ShowError(
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