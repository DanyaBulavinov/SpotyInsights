package com.daniel.spotyinsights.presentation.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.style.PieChartDefaults
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val playCountPerDay by viewModel.playCountPerDay.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadTopTags()
        viewModel.loadPlayCountPerDay()
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Stats",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (tags.isNotEmpty()) {
            val dataSet = tags.map { it.count?.toFloat() ?: 0f }
                .toChartDataSet(
                    title = "Top Genres in the World",
                    labels = tags.map { it.name ?: "" }
                )
            val pieColors = listOf(
                Color(0xFF6EC6FF), // Light Blue
                Color(0xFFFFB74D), // Soft Orange
                Color(0xFF81C784), // Mint Green
                Color(0xFFF06292), // Pinkish
                Color(0xFFBA68C8), // Lavender
                Color(0xFFFFD54F), // Gold
                Color(0xFFA1887F), // Taupe
                Color(0xFF4DB6AC), // Teal
                Color(0xFFFF8A65), // Coral
                Color(0xFF9575CD)  // Soft Purple
            )
            PieChart(
                dataSet = dataSet,
                style = PieChartDefaults.style(
                    legendVisible = true,
                    pieColors = pieColors
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            // Simple line chart for playcount per day with short date and playcount labels
            val lineData = playCountPerDay.map { it.count.toFloat() }
            val shortDateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
            val lineLabels = playCountPerDay.map {
                val date = try {
                    LocalDate.parse(it.date)
                } catch (e: Exception) {
                    null
                }
                val formattedDate = date?.format(shortDateFormatter) ?: it.date
                "$formattedDate: ${it.count}"
            }
            LineChart(
                dataSet = lineData.toChartDataSet(
                    title = "Playcount per Day",
                    labels = lineLabels
                ),
                // You can add style here if needed
            )
        } else {
            CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
        }
    }
} 