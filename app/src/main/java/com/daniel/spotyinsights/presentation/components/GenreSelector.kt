package com.daniel.spotyinsights.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreSelector(
    availableGenres: List<String>,
    selectedGenres: List<String>,
    onGenreSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        availableGenres.forEach { genre ->
            FilterChip(
                selected = selectedGenres.contains(genre),
                onClick = { onGenreSelected(genre) },
                label = { Text(text = genre) },
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
} 