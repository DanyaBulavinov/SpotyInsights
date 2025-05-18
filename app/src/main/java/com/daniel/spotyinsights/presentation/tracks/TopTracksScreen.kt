package com.daniel.spotyinsights.presentation.tracks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.daniel.spotyinsights.R
import com.daniel.spotyinsights.presentation.components.ErrorState
import com.daniel.spotyinsights.presentation.components.TimeRangeSelector
import com.daniel.spotyinsights.presentation.components.TrackItem
import com.daniel.spotyinsights.presentation.components.TrackItemSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopTracksScreen(
    viewModel: TopTracksViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TopTracksEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.nav_top_tracks)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            state = pullToRefreshState,
            onRefresh = { viewModel.setEvent(TopTracksEvent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TimeRangeSelector(
                    selectedTimeRange = state.selectedTimeRange,
                    onTimeRangeSelected = { timeRange ->
                        viewModel.setEvent(TopTracksEvent.TimeRangeSelected(timeRange))
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                when {
                    state.isLoading -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(10) {
                                TrackItemSkeleton()
                            }
                        }
                    }

                    state.error != null && state.tracks.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorState(
                                message = state.error ?: "",
                                onRetry = { viewModel.setEvent(TopTracksEvent.Refresh) }
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = state.tracks,
                                key = { it.id }
                            ) { track ->
                                TrackItem(
                                    track = track,
                                    onTrackClick = { clickedTrack ->
                                        navController.navigate("track_detail/${clickedTrack.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 