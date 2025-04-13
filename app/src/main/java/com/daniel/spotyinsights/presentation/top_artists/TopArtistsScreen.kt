package com.daniel.spotyinsights.presentation.top_artists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.daniel.spotyinsights.presentation.components.ArtistItem
import com.daniel.spotyinsights.presentation.components.ArtistItemSkeleton
import com.daniel.spotyinsights.presentation.components.ErrorState
import com.daniel.spotyinsights.presentation.components.TimeRangeSelector

@Composable
fun TopArtistsScreen(
    viewModel: TopArtistsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Your Top Artists",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            TimeRangeSelector(
                selectedTimeRange = state.selectedTimeRange,
                onTimeRangeSelected = viewModel::onTimeRangeSelected,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.error != null -> {
                        ErrorState(
                            message = state.error?.message ?: "",
                            onRetry = state.error?.retryAction ?: {}
                        )
                    }
                    state.isLoading -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(10) {
                                ArtistItemSkeleton()
                            }
                        }
                    }
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.artists) { artist ->
                                ArtistItem(
                                    artist = artist,
                                    onArtistClick = { /* TODO: Handle artist click */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 