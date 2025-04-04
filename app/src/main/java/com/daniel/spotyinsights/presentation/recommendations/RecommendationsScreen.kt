package com.daniel.spotyinsights.presentation.recommendations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.daniel.spotyinsights.presentation.components.ErrorState
import com.daniel.spotyinsights.presentation.components.GenreSelector
import com.daniel.spotyinsights.presentation.components.RecommendationItem
import com.daniel.spotyinsights.presentation.components.RecommendationItemSkeleton

@Composable
fun RecommendationsScreen(
    viewModel: RecommendationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Recommendations",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Select up to 5 genres:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            GenreSelector(
                availableGenres = state.availableGenres,
                selectedGenres = state.selectedGenres,
                onGenreSelected = viewModel::onGenreSelected,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.error != null -> {
                    ErrorState(
                        message = state.error?.message ?: "",
                        onRetry = state.error?.retryAction ?: {}
                    )
                }
                state.isLoading -> {
                    LazyColumn {
                        items(10) {
                            RecommendationItemSkeleton()
                        }
                    }
                }
                else -> {
                    LazyColumn {
                        items(state.recommendations) { track ->
                            RecommendationItem(
                                track = track,
                                onTrackClick = { /* TODO: Handle track click */ }
                            )
                        }
                    }
                }
            }
        }
    }
} 