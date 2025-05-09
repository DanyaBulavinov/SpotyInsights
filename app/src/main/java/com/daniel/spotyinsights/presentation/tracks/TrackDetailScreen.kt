package com.daniel.spotyinsights.presentation.tracks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.daniel.spotyinsights.R
import com.daniel.spotyinsights.domain.model.Track
import androidx.compose.ui.draw.blur

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailScreen(
    trackId: String,
    onBack: () -> Unit,
    viewModel: TrackDetailViewModel = hiltViewModel()
) {
    val track by viewModel.track.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val fallbackColor = Color(0xFF90CAF9) // Strong fallback color, no alpha
    val fallbackColor2 = Color(0xFFB2FF59) // A second fallback color for gradient

    // Dominant and vibrant color extraction state
    var dominantColor by remember { mutableStateOf<Color?>(fallbackColor) }
    var vibrantColor by remember { mutableStateOf<Color?>(fallbackColor2) }

    // Extract dominant and vibrant color from album image when track loads
    LaunchedEffect(track?.album?.imageUrl) {
        val imageUrl = track?.album?.imageUrl
        if (imageUrl != null) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                bitmap?.let {
                    Palette.from(it).generate { palette ->
                        palette?.let {
                            dominantColor = Color(palette.getDominantColor(fallbackColor.toArgb()))
                            // Try vibrant, else use muted, else fallback
                            val vibrant = palette.vibrantSwatch?.rgb
                            val muted = palette.mutedSwatch?.rgb
                            vibrantColor = when {
                                vibrant != null -> Color(vibrant)
                                muted != null -> Color(muted)
                                else -> fallbackColor2
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(trackId) {
        viewModel.loadTrackDetails(trackId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.track_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Diagonal, multi-stop gradient with more vibrancy and variety
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // Diagonal, multi-stop gradient with more vibrancy and variety
                    brush = Brush.linearGradient(
                        colors = listOf(
                            (dominantColor ?: fallbackColor).ensureMinLuminance().copy(alpha = 0.85f),
                            (vibrantColor ?: fallbackColor2).ensureMinLuminance().copy(alpha = 0.65f),
                            (dominantColor ?: fallbackColor).lighten(0.85f).ensureMinLuminance().copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.85f)
                        ),
                        start = Offset(
                            x = screenWidth * (0.05f + (trackId.hashFloat() * 0.2f)),
                            y = screenHeight * (0.05f + (trackId.hashFloat() * 0.2f))
                        ),
                        end = Offset(
                            x = screenWidth * (0.95f - (trackId.hashFloat() * 0.2f)),
                            y = screenHeight * (0.95f - (trackId.hashFloat() * 0.2f))
                        )
                    )
                )
        ) {
            // Debug: Show current color values (top-left, visible)
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "Dominant: #" + (dominantColor ?: fallbackColor).toArgb().toUInt().toString(16),
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "Vibrant: #" + (vibrantColor ?: fallbackColor2).toArgb().toUInt().toString(16),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    error != null -> {
                        Text(
                            text = error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    track != null -> {
                        AnimatedVisibility(
                            visible = track != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            TrackDetailContent(track!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackDetailContent(track: Track, topPadding: Dp = 16.dp) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = topPadding, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Centered album image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = track.album.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(28.dp))
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Unified frosted glass card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            // Unified blurred background layer
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(24.dp)
                    .background(Color.White.copy(alpha = 0.18f))
            )
            // Card content (sharp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = track.name,
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    // Artist(s)
                    Text(
                        text = "by " + track.artists.joinToString { it.name },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Album and release
                    Text(
                        text = "Album: ${track.album.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1
                    )
                    Text(
                        text = "Released: ${track.album.releaseDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                    // Info boxes row (unified background)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoBox(label = "Duration", value = "${track.durationMs / 1000 / 60}:${(track.durationMs / 1000) % 60}", unified = true)
                        InfoBox(label = "Popularity", value = track.popularity.toString(), unified = true)
                        InfoBox(label = "Explicit", value = if (track.explicit) "Yes" else "No", unified = true)
                    }
                    track.previewUrl?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Preview available",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBox(label: String, value: String, unified: Boolean = false) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
    ) {
        // Use no extra blur or background if unified, so the card's blur shows through
        if (!unified) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(16.dp)
                    .background(Color.White.copy(alpha = 0.22f))
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Helper to lighten a color
fun Color.lighten(factor: Float = 0.7f): Color {
    return Color(
        red = red + (1f - red) * factor,
        green = green + (1f - green) * factor,
        blue = blue + (1f - blue) * factor,
        alpha = alpha
    )
}

// Helper to clamp color luminance (avoid dark backgrounds)
fun Color.ensureMinLuminance(minLuminance: Float = 0.6f): Color {
    val lum = 0.299f * red + 0.587f * green + 0.114f * blue
    return if (lum < minLuminance) this.lighten(minLuminance) else this
}

// Helper to get a hash-based float for randomizing gradient
fun String.hashFloat(): Float {
    return (this.hashCode().ushr(1) % 1000) / 1000f
} 