package com.daniel.spotyinsights.presentation.navigation

sealed class Screen(val route: String) {
    data object TopTracks : Screen("top_tracks")
    data object TopArtists : Screen("top_artists")
    data object Recommendations : Screen("recommendations")

    companion object {
        val bottomNavItems = listOf(
            TopTracks,
            TopArtists,
            Recommendations
        )
    }
} 