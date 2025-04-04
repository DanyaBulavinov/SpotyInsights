package com.daniel.spotyinsights.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.daniel.spotyinsights.R
import com.daniel.spotyinsights.domain.repository.TimeRange

@Composable
fun TimeRangeSelector(
    selectedTimeRange: TimeRange,
    onTimeRangeSelected: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TimeRange.entries.forEach { timeRange ->
            FilterChip(
                selected = timeRange == selectedTimeRange,
                onClick = { onTimeRangeSelected(timeRange) },
                label = {
                    Text(
                        text = when (timeRange) {
                            TimeRange.SHORT_TERM -> stringResource(R.string.time_range_short)
                            TimeRange.MEDIUM_TERM -> stringResource(R.string.time_range_medium)
                            TimeRange.LONG_TERM -> stringResource(R.string.time_range_long)
                        }
                    )
                }
            )
        }
    }
} 