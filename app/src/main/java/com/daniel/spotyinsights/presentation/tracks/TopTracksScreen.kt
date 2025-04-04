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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.daniel.spotyinsights.R
import com.daniel.spotyinsights.presentation.components.ErrorState
import com.daniel.spotyinsights.presentation.components.TimeRangeSelector
import com.daniel.spotyinsights.presentation.components.TrackItem
import com.daniel.spotyinsights.presentation.components.TrackItemSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopTracksScreen(
    viewModel: TopTracksViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing) {
            viewModel.onRefresh()
        }
    }

    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.top_tracks)) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TimeRangeSelector(
                    selectedTimeRange = state.selectedTimeRange,
                    onTimeRangeSelected = viewModel::onTimeRangeSelected
                )

                Spacer(modifier = Modifier.height(8.dp))

                when {
                    state.isLoading -> {
                        LazyColumn {
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
                                message = state.error,
                                onRetry = { viewModel.onRefresh() }
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
                                        uriHandler.openUri(clickedTrack.spotifyUrl)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
} 