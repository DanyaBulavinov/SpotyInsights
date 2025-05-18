package com.daniel.spotyinsights.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.spotyinsights.domain.model.LastFmTagDomain
import com.daniel.spotyinsights.domain.model.PlayCountPerDay
import com.daniel.spotyinsights.domain.repository.LastFmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val lastFmRepository: LastFmRepository
) : ViewModel() {
    private val _tags = MutableStateFlow<List<LastFmTagDomain>>(emptyList())
    val tags: StateFlow<List<LastFmTagDomain>> = _tags

    private val _playCountPerDay = MutableStateFlow<List<PlayCountPerDay>>(emptyList())
    val playCountPerDay: StateFlow<List<PlayCountPerDay>> = _playCountPerDay

    fun loadTopTags() {
        viewModelScope.launch {
            _tags.value = lastFmRepository.getTopTags()
        }
    }

    fun loadPlayCountPerDay() {
        viewModelScope.launch {
            _playCountPerDay.value = lastFmRepository.getPlayCountPerDay("Avertin21")
        }
    }
} 