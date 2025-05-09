package com.daniel.spotyinsights.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.daniel.spotyinsights.R

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    @StringRes val labelResId: Int
) {
    data object TopTracks : Screen(
        route = "top_tracks",
        icon = Icons.Filled.PlayArrow,
        labelResId = R.string.nav_top_tracks
    )

    data object TopArtists : Screen(
        route = "top_artists",
        icon = Icons.Filled.Person,
        labelResId = R.string.nav_top_artists
    )

    data object NewReleases : Screen(
        route = "new_releases",
        icon = Icons.Filled.Star,
        labelResId = R.string.nav_new_releases
    )

    companion object {
        val bottomNavItems = listOf(
            TopTracks,
            TopArtists,
            NewReleases
        )
    }
}
