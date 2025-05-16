package com.daniel.spotyinsights.presentation.releases

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daniel.spotyinsights.R
import com.daniel.spotyinsights.presentation.components.ErrorState
import com.daniel.spotyinsights.presentation.components.ReleaseItem
import com.daniel.spotyinsights.presentation.components.ReleaseItemSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReleasesScreen(
    viewModel: NewReleasesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NewReleasesEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_new_releases)) }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            state = pullToRefreshState,
            onRefresh = { viewModel.setEvent(NewReleasesEvent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                when {
                    state.error != null && state.releases.isEmpty() -> {
                        ErrorState(
                            message = state.error!!,
                            onRetry = { viewModel.setEvent(NewReleasesEvent.Refresh) }
                        )
                    }

                    state.isLoading && state.releases.isEmpty() -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(10) {
                                ReleaseItemSkeleton()
                            }
                        }
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = state.releases,
                                key = { it.id }
                            ) { release ->
                                ReleaseItem(
                                    release = release,
                                    onReleaseClick = { clickedRelease ->
                                        uriHandler.openUri(clickedRelease.spotifyUrl)
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