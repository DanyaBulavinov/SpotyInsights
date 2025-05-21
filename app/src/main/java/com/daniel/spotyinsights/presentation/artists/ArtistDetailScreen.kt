package com.daniel.spotyinsights.presentation.artists

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.blur
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
import androidx.compose.ui.text.style.TextAlign
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
import com.daniel.spotyinsights.domain.model.DetailedArtist
import com.daniel.spotyinsights.domain.model.Track
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    artistId: String,
    onBack: () -> Unit,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val fallbackColor = Color(0xFF90CAF9)
    val fallbackColor2 = Color(0xFFB2FF59)

    // Dominant and vibrant color extraction state
    var dominantColor by remember { mutableStateOf<Color?>(fallbackColor) }
    var vibrantColor by remember { mutableStateOf<Color?>(fallbackColor2) }

    // Extract dominant and vibrant color from artist image
    LaunchedEffect(state.artist?.images?.firstOrNull()) {
        val imageUrl = state.artist?.images?.firstOrNull()
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

    LaunchedEffect(artistId) {
        viewModel.setEvent(ArtistDetailEvent.LoadArtist(artistId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Artist Details") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            (dominantColor ?: fallbackColor).ensureMinLuminance().lighten(0.001f)
                                .copy(alpha = 0.85f),
                            (vibrantColor ?: fallbackColor2).ensureMinLuminance().lighten(0.001f)
                                .copy(alpha = 0.65f),
                            (dominantColor ?: fallbackColor).lighten(0.85f).ensureMinLuminance()
                                .lighten(0.001f).copy(alpha = 0.45f),
                            (vibrantColor ?: fallbackColor2).lighten(0.7f).ensureMinLuminance()
                                .lighten(0.001f).copy(alpha = 0.35f)
                        ),
                        start = Offset(
                            x = screenWidth * (0.05f + (artistId.hashFloat() * 0.2f)),
                            y = screenHeight * (0.05f + (artistId.hashFloat() * 0.2f))
                        ),
                        end = Offset(
                            x = screenWidth * (0.95f - (artistId.hashFloat() * 0.2f)),
                            y = screenHeight * (0.95f - (artistId.hashFloat() * 0.2f))
                        )
                    )
                    // color = Color(0xFFFFEB3B) // Hardcoded bright yellow
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    state.error != null -> {
                        Text(
                            text = state.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    state.artist != null -> {
                        AnimatedVisibility(
                            visible = state.artist != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            ArtistDetailContent(
                                artist = state.artist!!,
                                topTracks = state.topTracks,
                                onTrackClick = { trackId ->
                                    navController.navigate("track_detail/$trackId")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistDetailContent(
    artist: DetailedArtist,
    topTracks: List<Track>,
    onTrackClick: (String) -> Unit,
    topPadding: Dp = 16.dp
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = topPadding, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Centered artist image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = artist.images.firstOrNull(),
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
                    .background(Color.White.copy(alpha = 0.3f)) // More transparent, lighter glass
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
                    Text(
                        text = artist.name,
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (artist.genres.isNotEmpty()) {
                        Text(
                            text = artist.genres.joinToString(", "),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(22.dp))
                    // Info boxes row (unified background)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoBox(
                            label = "Popularity",
                            value = artist.popularity.toString(),
                            unified = true
                        )
                        InfoBox(
                            label = "Followers",
                            value = artist.followers?.toString() ?: "-",
                            unified = true
                        )
                    }
                }
            }
        }
        // Top Tracks horizontal list
        if (topTracks.isNotEmpty()) {
            Text(
                text = "Top Tracks",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
            )
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp)
            ) {
                topTracks.forEach { track ->
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .width(100.dp)
                            .clickable { onTrackClick(track.id) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = track.album.imageUrl,
                            contentDescription = track.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = track.name,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
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
private fun Color.lighten(factor: Float = 0.7f): Color {
    return Color(
        red = red + (1f - red) * factor,
        green = green + (1f - green) * factor,
        blue = blue + (1f - blue) * factor,
        alpha = alpha
    )
}

// Helper to clamp color luminance (avoid dark backgrounds)
private fun Color.ensureMinLuminance(minLuminance: Float = 0.6f): Color {
    val lum = 0.299f * red + 0.587f * green + 0.114f * blue
    return if (lum < minLuminance) this.lighten(minLuminance) else this
}

// Helper to get a hash-based float for randomizing gradient
private fun String.hashFloat(): Float {
    return (this.hashCode().ushr(1) % 1000) / 1000f
}