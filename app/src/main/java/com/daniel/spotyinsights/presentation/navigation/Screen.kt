package com.daniel.spotyinsights.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.daniel.spotyinsights.R

sealed class Screen(
    val route: String,
    @DrawableRes val iconRes: Int,
    @StringRes val labelResId: Int
) {
    data object TopTracks : Screen(
        route = "top_tracks",
        iconRes = R.drawable.ic_top_tracks,
        labelResId = R.string.nav_top_tracks
    )

    data object TopArtists : Screen(
        route = "top_artists",
        iconRes = R.drawable.ic_top_artists,
        labelResId = R.string.nav_top_artists
    )

    data object NewReleases : Screen(
        route = "new_releases",
        iconRes = R.drawable.ic_new_releases,
        labelResId = R.string.nav_new_releases
    )

    data object Stats : Screen(
        route = "stats",
        iconRes = R.drawable.ic_pie_chart,
        labelResId = R.string.nav_stats
    )

    companion object {
        val bottomNavItems = listOf(
            TopTracks,
            TopArtists,
            NewReleases,
            Stats,
        )
    }
}
